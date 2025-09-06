package service;

import com.example.user.service.ConflService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConflServiceTest {
    @Mock
    private RestTemplate restTemplate;

    private ConflService conflService;
    private final String testUrl = "https://api.example.com";
    private final String testToken = "test-token-123";
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        conflService = new ConflService(restTemplate);
        conflService.conflUrl = testUrl;
    }

    @Test
    void checkEmail_ShouldMakeCorrectApiCall() {

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setBearerAuth(testToken);
        HttpEntity<Void> expectedRequest = new HttpEntity<>(expectedHeaders);

        ResponseEntity<Map> expectedResponse = new ResponseEntity<>(Map.of("valid", true), HttpStatus.OK);

        when(restTemplate.exchange(
                eq(testUrl + "/api/confl/email/check?email=" + testEmail),
                eq(HttpMethod.GET),
                eq(expectedRequest),
                eq(Map.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Map> actualResponse = conflService.checkEmail(testEmail, testToken);

        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue((Boolean) actualResponse.getBody().get("valid"));

        verify(restTemplate).exchange(
                eq(testUrl + "/api/confl/email/check?email=" + testEmail),
                eq(HttpMethod.GET),
                argThat(entity ->
                        entity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).equals("Bearer " + testToken)
                ),
                eq(Map.class)
        );
    }

    @Test
    void checkEmail_ShouldHandleDifferentEmail() {
        // Arrange
        String differentEmail = "different@example.com";
        ResponseEntity<Map> expectedResponse = new ResponseEntity<>(Map.of("valid", false), HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(Map.class)
        )).thenReturn(expectedResponse);

        // Act
        ResponseEntity<Map> actualResponse = conflService.checkEmail(differentEmail, testToken);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertFalse((Boolean) actualResponse.getBody().get("valid"));
    }

    @Test
    void checkEmail_ShouldHandleDifferentHttpStatus() {
        // Arrange
        ResponseEntity<Map> expectedResponse = new ResponseEntity<>(Map.of("error", "Not found"), HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(Map.class)
        )).thenReturn(expectedResponse);

        // Act
        ResponseEntity<Map> actualResponse = conflService.checkEmail(testEmail, testToken);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertEquals("Not found", actualResponse.getBody().get("error"));
    }

    @Test
    void checkEmail_ShouldHandleEmptyResponse() {
        // Arrange
        ResponseEntity<Map> expectedResponse = new ResponseEntity<>(Map.of(), HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(Map.class)
        )).thenReturn(expectedResponse);

        // Act
        ResponseEntity<Map> actualResponse = conflService.checkEmail(testEmail, testToken);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue(actualResponse.getBody().isEmpty());
    }


    @Test
    void checkEmail_ShouldHandleRestClientException() {
        // Arrange
        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(Map.class)
        )).thenThrow(new org.springframework.web.client.RestClientException("Connection failed"));

        // Act & Assert
        assertThrows(org.springframework.web.client.RestClientException.class, () -> {
            conflService.checkEmail(testEmail, testToken);
        });
    }
}
