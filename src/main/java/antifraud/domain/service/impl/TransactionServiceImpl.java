package antifraud.domain.service.impl;

import antifraud.config.transaction.TransactionProperty;
import antifraud.domain.model.RegularCard;
import antifraud.domain.model.RegularCardFactory;
import antifraud.domain.model.Transaction;
import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.service.RegularCardService;
import antifraud.domain.service.StolenCardService;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.domain.service.TransactionService;
import antifraud.exceptions.ExistingFeedbackException;
import antifraud.exceptions.SameResultException;
import antifraud.exceptions.TransactionsNotFoundException;
import antifraud.persistence.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionProperty transactionProperty;
    private final TransactionRepository transactionRepository;
    private final SuspiciousIPService suspiciousIPService;
    private final StolenCardService stolenCardService;
    private final RegularCardService regularCardService;


    @Transactional
    @Override
    public Transaction processTransaction(Transaction transaction) {
        if (!regularCardService.existsByNumber(transaction.getCardNumber())) {
            setDefaultTransactionResultLimits(transaction);
        }
        transactionRepository.save(transaction);
        TransactionResult resultByAmountMoney = transactionResultByAmountMoney(transaction);
        String infoFromInitialResult = infoFromInitialTransactionResult(resultByAmountMoney);
        List<Transaction> transactionsInLastHourOfTransactionHistory =
                transactionRepository.findByCardNumberAndDateTimeBetween(transaction.getCardNumber(),
                        transaction.getDateTime().minusHours(1),
                        transaction.getDateTime());
        long ipUniqueCount =
                correlationCount(transactionsInLastHourOfTransactionHistory,
                        Transaction::getIpAddress);
        long regionUniqueCount =
                correlationCount(transactionsInLastHourOfTransactionHistory,
                        r -> r.getWorldRegion().name());
        List<String> infoFromCorrelation =
                infoFromTransactionCorrelationCount(ipUniqueCount, regionUniqueCount);
        List<String> infoFromBlacklists = infoFromCardAndIpBlacklists(transaction);

        TransactionResult resultBasedOnInfo =
                resultBasedOnInfoNumbers(ipUniqueCount,
                        regionUniqueCount,
                        infoFromBlacklists.size(),
                        resultByAmountMoney);
        if (!resultBasedOnInfo.equals(resultByAmountMoney)) {
            infoFromInitialResult = "";
        }
        transaction.setTransactionResult(resultBasedOnInfo);
        String allInfo = gatheredInfo(infoFromInitialResult, infoFromBlacklists, infoFromCorrelation);
        transaction.setTransactionInfo(allInfo);
        return transactionRepository.save(transaction);
    }

    private void setDefaultTransactionResultLimits(Transaction transaction) {
        RegularCard regularCard = RegularCardFactory.create(transaction.getCardNumber());
        regularCard.setAllowedLimit(transactionProperty.allowed());
        regularCard.setManualProcessingLimit(transactionProperty.manualProcessing());
        regularCardService.save(regularCard);
    }

    private TransactionResult transactionResultByAmountMoney(Transaction transaction) {
        RegularCard regularCard = regularCardService.findByNumber(transaction.getCardNumber());
        Long money = transaction.getMoney();
        if (money <= regularCard.getAllowedLimit()) {
            return TransactionResult.ALLOWED;
        } else if (money <= regularCard.getManualProcessingLimit()) {
            return TransactionResult.MANUAL_PROCESSING;
        } else {
            return TransactionResult.PROHIBITED;
        }
    }

    private String infoFromInitialTransactionResult(TransactionResult transactionResult) {
        return TransactionResult.ALLOWED.equals(transactionResult) ?
                "none" : "amount";
    }

    private long correlationCount(List<Transaction> transactions,
                                  Function<Transaction, String> transactionField) {
        return transactions.stream()
                .map(transactionField)
                .distinct()
                .count();
    }

    private List<String> infoFromTransactionCorrelationCount(Long ipUniqueCount, Long regionUniqueCount) {
        List<String> infoFromCorrelation = new ArrayList<>();
        if (ipUniqueCount >= transactionProperty.correlation()) {
            infoFromCorrelation.add("ip-correlation");
        }
        if (regionUniqueCount >= transactionProperty.correlation()) {
            infoFromCorrelation.add("region-correlation");
        }
        return infoFromCorrelation;
    }

    private List<String> infoFromCardAndIpBlacklists(Transaction transaction) {
        List<String> infoFromBlacklists = new ArrayList<>();
        boolean ipBlacklisted = suspiciousIPService.existsByIpAddress(transaction.getIpAddress());
        boolean cardBlacklisted = stolenCardService.existsByNumber(transaction.getCardNumber());
        if (ipBlacklisted) {
            infoFromBlacklists.add("ip");
        }
        if (cardBlacklisted) {
            infoFromBlacklists.add("card-number");
        }
        return infoFromBlacklists;
    }

    /**
     * @param ipUniqueCount     number of unique IPs from transactions happened in the last hour
     *                          in the transaction history.
     * @param regionUniqueCount number of unique regions from transactions happened in the last hour
     *                          in the transaction history.
     * @param blacklistSize     sum of Suspicious IP and Stolen Card blacklists' size.
     * @param result            Transaction with initial transaction's result based on the deposit money.
     * @return transaction result based on the information numbers. if nothing is changed based on numbers,
     * method returns initial transaction result.
     */
    private TransactionResult resultBasedOnInfoNumbers(long ipUniqueCount,
                                                       long regionUniqueCount,
                                                       int blacklistSize,
                                                       TransactionResult result) {
        if ((ipUniqueCount == transactionProperty.correlation() ||
                regionUniqueCount == transactionProperty.correlation()) &&
                !TransactionResult.PROHIBITED.equals(result)) {
            result = TransactionResult.MANUAL_PROCESSING;
        }
        if (ipUniqueCount > transactionProperty.correlation() ||
                regionUniqueCount > transactionProperty.correlation()) {
            result = TransactionResult.PROHIBITED;
        }
        if (blacklistSize > 0) {
            result = TransactionResult.PROHIBITED;
        }
        return result;
    }

    /**
     * @param infoFromResult      information based on the result (type) of the transaction,
     *                            based on the deposit amount of money.
     * @param infoFromBlacklists  information based on the result from checking
     *                            the Suspicious IP and Stolen Card blacklists.
     * @param infoFromCorrelation information based on the correlation of transactions in the last hour
     *                            in the transaction history.
     * @return all the information gathered from the type of transaction, blacklists and transaction history's last hour.
     */
    private String gatheredInfo(String infoFromResult,
                                List<String> infoFromBlacklists,
                                List<String> infoFromCorrelation) {
        if (!infoFromBlacklists.isEmpty() && infoFromResult.equals("none")) {
            infoFromResult = "";
        }
        infoFromBlacklists.add(infoFromResult);
        infoFromBlacklists.addAll(infoFromCorrelation);
        return infoFromBlacklists.stream()
                .filter(s -> s.length() != 0)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Override
    public Transaction giveFeedback(Transaction feedback) {
        Transaction transactionFromDB = transactionRepository.findById(feedback.getId())
                .orElseThrow(TransactionsNotFoundException::new);
        if (transactionRepository.existsByFeedbackAndFeedbackNotNull(transactionFromDB.getFeedback())) {
            throw new ExistingFeedbackException(HttpStatus.CONFLICT);
        }
        feedbackCheckForCollision(feedback, transactionFromDB);
        changeLimitsOfFraudDetectionAlgorithm(feedback, transactionFromDB);
        transactionFromDB.setFeedback(feedback.getFeedback());
        return transactionRepository.save(transactionFromDB);
    }

    /**
     * Checks if the provided feedback is the same as the current transaction result.
     * If it is, method throws an exception.
     *
     * @param feedback          Transaction with given feedback.
     * @param transactionFromDB Transaction with current transaction result.
     */
    private void feedbackCheckForCollision(Transaction feedback, Transaction transactionFromDB) {
        if (feedback.getFeedback().equals(transactionFromDB.getTransactionResult())) {
            throw new SameResultException();
        }
    }

    /**
     * Change the limits of ALLOWED and MANUAL_PROCESSING values.
     *
     * @param feedback          Transaction with given feedback.
     * @param transactionFromDB Transaction from DB with current transaction result.
     */
    private void changeLimitsOfFraudDetectionAlgorithm(Transaction feedback,
                                                       Transaction transactionFromDB) {
        RegularCard regularCard = regularCardService.findByNumber(transactionFromDB.getCardNumber());
        if (TransactionResult.ALLOWED.equals(feedback.getFeedback())) {
            increaseAllowedLimit(regularCard, transactionFromDB.getMoney());
            if (TransactionResult.PROHIBITED.equals(transactionFromDB.getTransactionResult())) {
                increaseManualLimit(regularCard, transactionFromDB.getMoney());
            }
        }
        if (TransactionResult.MANUAL_PROCESSING.equals(feedback.getFeedback())) {
            if (TransactionResult.ALLOWED.equals(transactionFromDB.getTransactionResult())) {
                decreaseAllowedLimit(regularCard, transactionFromDB.getMoney());
            } else if (TransactionResult.PROHIBITED.equals(transactionFromDB.getTransactionResult())) {
                increaseManualLimit(regularCard, transactionFromDB.getMoney());
            }
        }
        if (TransactionResult.PROHIBITED.equals(feedback.getFeedback())) {
            decreaseManualLimit(regularCard, transactionFromDB.getMoney());
            if (TransactionResult.ALLOWED.equals(transactionFromDB.getTransactionResult())) {
                decreaseAllowedLimit(regularCard, transactionFromDB.getMoney());
            }
        }
    }

    private void increaseAllowedLimit(RegularCard regularCard, Long money) {
        long newLimit = (long) Math.ceil(transactionProperty.currentLimitFactor() * regularCard.getAllowedLimit() +
                transactionProperty.currentDepositFactor() * money);
        regularCard.setAllowedLimit(newLimit);
        regularCardService.save(regularCard);
    }

    private void increaseManualLimit(RegularCard regularCard, Long money) {
        long newLimit = (long) Math.ceil(transactionProperty.currentLimitFactor() * regularCard.getManualProcessingLimit() +
                transactionProperty.currentDepositFactor() * money);
        regularCard.setManualProcessingLimit(newLimit);
        regularCardService.save(regularCard);
    }

    private void decreaseAllowedLimit(RegularCard regularCard, Long money) {
        long newLimit = (long) Math.ceil(transactionProperty.currentLimitFactor() * regularCard.getAllowedLimit() -
                transactionProperty.currentDepositFactor() * money);
        regularCard.setAllowedLimit(newLimit);
        regularCardService.save(regularCard);
    }

    private void decreaseManualLimit(RegularCard regularCard, Long money) {
        long newLimit = (long) Math.ceil(transactionProperty.currentLimitFactor() * regularCard.getManualProcessingLimit() -
                transactionProperty.currentDepositFactor() * money);
        regularCard.setManualProcessingLimit(newLimit);
        regularCardService.save(regularCard);
    }

    @Override
    public List<Transaction> showTransactionHistory() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> showTransactionHistoryForSpecificCardNumber(String cardNumber) {
        List<Transaction> transactionsByCardNumber = transactionRepository.findTransactionByCardNumber(cardNumber);
        if (transactionsByCardNumber.isEmpty()) {
            throw new TransactionsNotFoundException();
        }
        return transactionsByCardNumber;
    }
}