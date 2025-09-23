package service;

import com.example.confl.model.Email;
import com.example.confl.repository.EmailRepository;
import com.example.confl.service.ConflService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ConflServiceUnitTest {
    @Mock
    private EmailRepository emailRepository;
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private ConflService conflService;

    private final String validToken = "valid-token-123";
    private final String invalidToken = "invalid-token-456";
    private final String testEmail = "test@example.com";


    @Test
    @Tag("positive")
    void getAllEmails_ShouldReturnAllEmails() {

        List<Email> expectedEmails = Arrays.asList(
                Email.builder().email("test1@example.com").build(),
                Email.builder().email("test2@example.com").build()
        );

        when(emailRepository.findAll()).thenReturn(expectedEmails);

        List<Email> actualEmails = conflService.getAllEmails();

        assertEquals(expectedEmails, actualEmails);
        verify(emailRepository, times(1)).findAll();
    }

    @Test
    @Tag("positive")
    void emailEqualsAndHashCode_ShouldWorkCorrectly() {

        Email email1 = Email.builder().email("test@example.com").build();
        Email email2 = Email.builder().email("test@example.com").build();

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}
