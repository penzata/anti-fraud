package antifraud.domain.model;

import antifraud.domain.model.enums.UserAccess;
import antifraud.domain.model.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomUserFactoryTest {

    @Test
    void WhenCreatingUserFromCreateFactoryThenReturnObjectWithCreatedFields() {
        String expectedName = "John";
        String expectedUsername = "johndoe1";
        String expectedPass = "secret";

        CustomUser customUser = CustomUserFactory.create("John", "johndoe1", "secret");

        assertAll(
                () -> assertEquals(expectedName, customUser.getName()),
                () -> assertEquals(expectedUsername, customUser.getUsername()),
                () -> assertEquals(expectedPass, customUser.getPassword())
        );
    }

    @Test
    void WhenCreatingUserFromCreateWithRoleFactoryThenReturnObjectWithCreatedFields() {
        CustomUser userWithRole = CustomUserFactory.createWithRole("johndoe1", UserRole.SUPPORT);

        assertAll(
                () -> assertThat(userWithRole)
                        .hasFieldOrPropertyWithValue("username", "johndoe1"),
                () -> assertThat(userWithRole)
                        .hasFieldOrPropertyWithValue("role", UserRole.SUPPORT)
        );
    }

    @Test
    void WhenCreatingUserFromCreateWithAccessFactoryThenReturnObjectWithCreatedFields() {
        CustomUser userWithAccess = CustomUserFactory.createWithAccess("johndoe1", UserAccess.UNLOCK);

        assertAll(
                () -> assertThat(userWithAccess)
                        .hasFieldOrPropertyWithValue("username", "johndoe1"),
                () -> assertThat(userWithAccess)
                        .hasFieldOrPropertyWithValue("access", UserAccess.UNLOCK)
        );
    }

    @Test
    void WhenMockingFactoryMethodThenReturnRightObject() {
        try (MockedStatic<CustomUserFactory> mockedFactory = mockStatic(CustomUserFactory.class)) {
            mockedFactory
                    .when(() -> CustomUserFactory.createWithAccess(any(), any()))
                    .thenReturn(CustomUser.builder()
                            .username("JoHnDoE1")
                            .access(UserAccess.UNLOCK)
                            .build());

            CustomUser userWithAccess = CustomUserFactory.createWithAccess(any(), any());

            assertAll(
                    () -> assertThat(userWithAccess)
                            .hasFieldOrPropertyWithValue("username", "JoHnDoE1"),
                    () -> assertThat(userWithAccess)
                            .hasFieldOrPropertyWithValue("access", UserAccess.UNLOCK)
            );
        }
    }
}