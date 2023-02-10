package antifraud.domain.service;

import antifraud.domain.model.StolenCard;

import java.util.List;
import java.util.Optional;

public interface StolenCardService {
    Optional<StolenCard> storeStolenCardNumber(StolenCard stolenCard);

    void removeCardNumber(String number);

    List<StolenCard> showCardNumbers();

    boolean existsByNumber(String number);
}