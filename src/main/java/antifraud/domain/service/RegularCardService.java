package antifraud.domain.service;

import antifraud.domain.model.RegularCard;

public interface RegularCardService {

    boolean existsByNumber(String cardNumber);

    RegularCard findByNumber(String cardNumber);

    RegularCard save(RegularCard regularCard);
}
