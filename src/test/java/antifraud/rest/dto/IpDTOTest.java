package antifraud.rest.dto;

import antifraud.domain.model.IP;
import antifraud.domain.model.IPFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@Slf4j
class IpDTOTest {
    private IP ipModel;
    private IP ipModel2;
    private IpDTO buildIpDTO;

    @BeforeEach
    void setup() {
        ipModel = IPFactory.create("196.168.01.1");
        ipModel2 = IPFactory.create("196.168.01.3");
        buildIpDTO = IpDTO.builder()
                .ip("196.168.01.1")
                .build();
    }

    @Test
    void WhenMockingFromModelThenReturnRightObject() {
        IpDTO buildIpDTO2 = IpDTO.builder()
                .ip("196.168.01.3")
                .build();

        try (MockedStatic<IpDTO> mockedIpDTO = mockStatic(IpDTO.class)) {
            mockedIpDTO
                    .when(() -> IpDTO.fromModel(any()))
                    .thenReturn(buildIpDTO)
                    .thenReturn(buildIpDTO2);

            IpDTO ipDTO = IpDTO.fromModel(ipModel);
            IpDTO ipDTO2 = IpDTO.fromModel(ipModel2);

            assertAll(
                    () -> assertThat(ipDTO)
                            .hasFieldOrPropertyWithValue("id", ipModel.getId()),
                    () -> assertThat(ipDTO)
                            .hasFieldOrPropertyWithValue("ip", ipModel.getIpAddress()),
                    () -> assertThat(ipDTO2)
                            .hasFieldOrPropertyWithValue("id", ipModel2.getId()),
                    () -> assertThat(ipDTO2)
                            .hasFieldOrPropertyWithValue("ip", ipModel2.getIpAddress())
            );
        }
    }

    @Test
    void WhenMockingFromModelWithFalseInputThenReturnNullObject() {
        try (MockedStatic<IpDTO> mockedIpDTO = mockStatic(IpDTO.class)) {
            mockedIpDTO
                    .when(() -> IpDTO.fromModel(ipModel))
                    .thenReturn(buildIpDTO);

            IpDTO ipDTO = IpDTO.fromModel(ipModel2);

            assertNull(ipDTO);
        }
    }

}