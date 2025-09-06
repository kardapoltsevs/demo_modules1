package service;

import com.example.user.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void getToken_ShouldReturnToken_WhenRequestIsSuccessful() {

        String expectedToken = "test-jwt-token";
        jwtService.jwtUrl = "http://auth-service";

        when(restTemplate.getForObject("http://auth-service/token", String.class))
                .thenReturn(expectedToken);

        String actualToken = jwtService.getToken();

        assertEquals(expectedToken, actualToken);
        verify(restTemplate).getForObject("http://auth-service/token", String.class);
    }

    @Test
    void getToken_ShouldThrowException_WhenRestTemplateFails() {

        jwtService.jwtUrl = "http://auth-service";

        when(restTemplate.getForObject("http://auth-service/token", String.class))
                .thenThrow(new RestClientException("Connection failed"));

        assertThrows(RestClientException.class, () -> jwtService.getToken());
    }
}
