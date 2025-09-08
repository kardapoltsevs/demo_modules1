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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ConflServiceUnitTest {
    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private ConflService conflService;

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
    void isEmailAvailable_WhenEmailIsAvailable_ShouldReturnTrueAndSaveEmail() {

        String testEmail = "available@example.com";
        when(emailRepository.existsByEmail(testEmail)).thenReturn(false);

        boolean result = conflService.isEmailAvailable(testEmail);

        assertTrue(result);
        verify(emailRepository, times(1)).existsByEmail(testEmail);
        verify(emailRepository, times(1)).save(any(Email.class));
    }

    @Test
    @Tag("positive")
    void isEmailAvailable_WhenEmailIsNotAvailable_ShouldReturnFalseAndNotSave() {

        String testEmail = "taken@example.com";
        when(emailRepository.existsByEmail(testEmail)).thenReturn(true);

        boolean result = conflService.isEmailAvailable(testEmail);

        assertFalse(result);
        verify(emailRepository, times(1)).existsByEmail(testEmail);
        verify(emailRepository, never()).save(any(Email.class));
    }

    @Test
    @Tag("positive")
    void isEmailAvailable_WhenEmailIsAvailable_ShouldSaveCorrectEmail() {

        String testEmail = "new@example.com";
        when(emailRepository.existsByEmail(testEmail)).thenReturn(false);

        conflService.isEmailAvailable(testEmail);

        verify(emailRepository).save(argThat(email ->
                email.getEmail().equals(testEmail)
        ));
    }

    @Test
    @Tag("positive")
    void isEmailAvailable_WithEmptyEmail_ShouldWorkCorrectly() {

        String emptyEmail = "";
        when(emailRepository.existsByEmail(emptyEmail)).thenReturn(false);

        boolean result = conflService.isEmailAvailable(emptyEmail);

        assertTrue(result);
        verify(emailRepository, times(1)).existsByEmail(emptyEmail);
        verify(emailRepository, times(1)).save(any(Email.class));
    }

    @Test
    @Tag("negative")
    void getAllEmails_WhenRepositoryThrowsException_ShouldPropagateException() {

        when(emailRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            conflService.getAllEmails();
        });
    }

    @Test
    @Tag("negative")
    void isEmailAvailable_WhenRepositoryThrowsException_ShouldPropagateException() {

        String testEmail = "test@example.com";
        when(emailRepository.existsByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            conflService.isEmailAvailable(testEmail);
        });
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
