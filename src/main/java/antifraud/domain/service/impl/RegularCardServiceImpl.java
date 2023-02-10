package antifraud.domain.service.impl;

import antifraud.domain.model.RegularCard;
import antifraud.domain.service.RegularCardService;
import antifraud.persistence.repository.RegularCardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegularCardServiceImpl implements RegularCardService {
    private final RegularCardRepository regularCardRepository;

    @Override
    public boolean existsByNumber(String cardNumber) {
        return regularCardRepository.existsByNumber(cardNumber);
    }

    @Override
    public RegularCard findByNumber(String cardNumber) {
        return regularCardRepository.findByNumber(cardNumber);
    }

    @Override
    public RegularCard save(RegularCard regularCard) {
        return regularCardRepository.save(regularCard);
    }
}