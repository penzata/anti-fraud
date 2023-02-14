package antifraud.rest.controller;

import antifraud.config.security.DelegatedAuthenticationEntryPoint;
import antifraud.config.security.DelegatedSecurityConfig;
import antifraud.domain.model.IP;
import antifraud.domain.model.IPFactory;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.exceptionhandler.ExceptionConstants;
import antifraud.exceptions.ExistingIpException;
import antifraud.rest.dto.IpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = IPController.class)
@Import({DelegatedAuthenticationEntryPoint.class, DelegatedSecurityConfig.class})
@WithMockUser(roles = "SUPPORT")
class IPControllerTest {
    private static final String URL = "http://localhost:28852/api/antifraud/suspicious-ip";
    private static final String VALID_CONTENT_INPUT = "{\"ip\":\"196.168.01.1\"}";
    private final MockMvc mockMvc;
    @MockBean
    private SuspiciousIPService suspiciousIPService;
    private IP ipOutput;

    @BeforeEach
    void setup() {
        this.ipOutput = IPFactory.createWithId(1L, "196.168.01.1");
    }

    @Test
    void WhenSavingNonExistentIpAddressThenReturnStatus200() throws Exception {
        given(suspiciousIPService.saveSuspiciousAddress(any())).willReturn(Optional.of(ipOutput));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_INPUT))
                .andExpect(status().isOk());
    }

    @Test
    void WhenSavingNonExistentIpAddressThenReturnSavedIp() throws Exception {
        given(suspiciousIPService.saveSuspiciousAddress(any())).willReturn(Optional.of(ipOutput));
        IpDTO expectedResponse = IpDTO.fromModel(ipOutput);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_INPUT))
                .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                .andExpect(jsonPath("$.ip").value(expectedResponse.ip()));
    }

    @Test
    void WhenInputIsValidThenMapsValueToServiceCorrectly() throws Exception {
        ArgumentCaptor<IP> ipCaptor = ArgumentCaptor.forClass(IP.class);
        given(suspiciousIPService.saveSuspiciousAddress(any())).willReturn(Optional.of(ipOutput));
        String inputIpAddress = "196.168.01.1";

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CONTENT_INPUT));

        then(suspiciousIPService).should(times(1))
                .saveSuspiciousAddress(ipCaptor.capture());
        verifyNoMoreInteractions(suspiciousIPService);

        String capturedIpAddress = ipCaptor.getValue().getIpAddress();
        assertEquals(inputIpAddress, capturedIpAddress);
        assertNull(ipCaptor.getValue().getId());
    }

    @Test
    void WhenSavingExistentIpAddressThenReturnStatus409() throws Exception {
        doThrow(ExistingIpException.class).when(suspiciousIPService).saveSuspiciousAddress(any());
        String exceptionMessage = String.format("{'status':'%s'}", ExceptionConstants.EXISTING_IP);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_INPUT))
                .andExpect(status().isConflict())
                .andExpect(content().json(exceptionMessage));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR", "MERCHANT", "INTRUDER"})
    void WhenAccessWithIncorrectRoleThenReturnStatus403() throws Exception {
        // accessing API with incorrect or non-existent role
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_INPUT))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void WhenAccessWithAnonymousUserThenReturnStatus401() throws Exception {
        // accessing API with an anonymous user
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_INPUT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void WhenGivingEmptyBodyThenReturnMethodArgumentNotValidException() throws Exception {
        String emptyBody = "{ }";

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void WhenGivingInvalidIpAddressThenReturnStatus400() throws Exception {
        String invalidIP = "{\"ip\":\"196.168.01\"}";

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidIP))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void WhenRequestBodyIsAbsentThenReturnHttpMessageNotReadableException() throws Exception {
        // absent request body
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof HttpMessageNotReadableException));
    }

    @Test
    void WhenUsingIncorrectHTTPMethodThenReturnStatus500() throws Exception {
        // using incorrect HTTP method
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT_INPUT))
                .andExpect(status().isInternalServerError());
    }
}