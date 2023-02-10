package antifraud.persistence.repository;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomUserRepositoryTest {
    private final TestEntityManager entityManager;
    private final CustomUserRepository customUserRepository;
    private CustomUser customUser;

    @BeforeEach
    void setup() {
        this.customUser = CustomUserFactory.create("JohnDoe", "johndoe1", "secret");
    }

    @Test
    void WhenUsernameExistsThenReturnTrue() {
        entityManager.persist(customUser);
        String username = customUser.getUsername();

        boolean result = customUserRepository.existsByUsername(username);

        assertTrue(result);
    }

    @Test
    void WhenUsernameDoesNotExistsThenReturnFalse() {
        boolean result = customUserRepository.existsByUsername(any());

        assertFalse(result);
    }

    @Test
    void WhenFindByNonExistentUsernameThenReturnEmpty() {
        Optional<CustomUser> nonExistentUser = customUserRepository.findByUsernameIgnoreCase(any());

        assertThat(nonExistentUser).isEmpty();
    }

    @Test
    void WhenFindByUsernameThenReturnFoundUser() {
        entityManager.persist(customUser);
        String username = customUser.getUsername();

        Optional<CustomUser> foundUser = customUserRepository.findByUsernameIgnoreCase(username);

        assertThat(foundUser).isPresent();
    }

    @Test
    void WhenDbIsEmptyThenCountReturnZero() {
        long expected = 0;

        long result = customUserRepository.count();

        assertEquals(expected, result);
    }

    @Test
    void WhenDbHasTwoRecordsThenCountReturnTwo() {
        CustomUser secondUser = CustomUserFactory.create("JaneDoe", "jane333doe", "secretz");
        entityManager.persist(customUser);
        entityManager.persist(secondUser);
        long expected = 2;

        long result = customUserRepository.count();

        assertEquals(expected, result);
    }

    @Test
    void WhenSaveThenStoreUser() {
        CustomUser savedUser = customUserRepository.save(customUser);

        assertAll(
                () -> assertThat(savedUser)
                        .hasFieldOrPropertyWithValue("id", 1L),
                () -> assertThat(savedUser)
                        .hasFieldOrPropertyWithValue("name", "JohnDoe"),
                () -> assertThat(savedUser)
                        .hasFieldOrPropertyWithValue("username", "johndoe1"),
                () -> assertThat(savedUser)
                        .hasFieldOrPropertyWithValue("password", "secret")
        );
    }

    @Test
    void WhenSaveFirstUserThenUserIdIsOne() {
        long expected = 1;

        CustomUser savedUser = customUserRepository.save(customUser);
        Long result = savedUser.getId();

        assertEquals(expected, result);
    }

    @Test
    void WhenSaveTwoUsersThenSecondUserIdIsTwo() {
        CustomUser secondUser = CustomUserFactory.create("JaneDoe", "jane333doe", "secretz");
        long expected = 2;

        customUserRepository.save(customUser);
        CustomUser secondSavedUser = customUserRepository.save(secondUser);
        Long result = secondSavedUser.getId();

        assertEquals(expected, result);
    }

    @Test
    void WhenSavingOneUserMultipleTimesThenIdDoesntChange() {
        long expected = 1;

        customUserRepository.save(customUser);
        customUserRepository.save(customUser);
        customUserRepository.save(customUser);
        Long result = customUser.getId();

        assertEquals(expected, result);
    }

    @Test
    void WhenDeleteByNonExistentIdThenThrowException() {
        Executable executable = () -> customUserRepository.deleteById(3L);

        assertThrows(EmptyResultDataAccessException.class, executable);
    }

    @Test
    void WhenDeleteByIdIsSuccessfulThenReturnCountZero() {
        entityManager.persist(customUser);
        Long id = customUser.getId();
        long expectedCount = 0;

        customUserRepository.deleteById(id);
        long resultCount = customUserRepository.count();

        assertEquals(expectedCount, resultCount);
    }

}