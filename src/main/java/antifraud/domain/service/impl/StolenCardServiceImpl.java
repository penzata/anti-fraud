package antifraud.domain.service.impl;

import antifraud.domain.model.StolenCard;
import antifraud.domain.service.StolenCardService;
import antifraud.exceptions.CardNotFoundException;
import antifraud.persistence.repository.StolenCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StolenCardServiceImpl implements StolenCardService {
    private final StolenCardRepository stolenCardRepository;

    @Transactional
    @Override
    public Optional<StolenCard> storeStolenCardNumber(StolenCard stolenCard) {
        return stolenCardRepository.existsByNumber(stolenCard.getNumber()) ?
                Optional.empty() :
                Optional.of(stolenCardRepository.save(stolenCard));
    }

    @Transactional
    @Override
    public void removeCardNumber(String number) {
        StolenCard foundCard = stolenCardRepository.findByNumber(number)
                .orElseThrow(() -> new CardNotFoundException(number));
        stolenCardRepository.deleteById(foundCard.getId());
    }

    @Override
    public List<StolenCard> showCardNumbers() {
        return stolenCardRepository.findAll().stream()
                .sorted(Comparator.comparingLong(StolenCard::getId))
                .toList();
    }

    @Override
    public boolean existsByNumber(String number) {

        return stolenCardRepository.existsByNumber(number);
    }
}