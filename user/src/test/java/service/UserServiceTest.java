package service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void getAllUsers_shouldReturnListOfUsers() {
        List<User> expectedUsers = Arrays.asList(
                User.builder().email("test1@example.com").name("User1").build(),
                User.builder().email("test2@example.com").name("User2").build()
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();

    }

    //простой тест для проверки
    @Test
    void simpleTest() {
        assertTrue(true, "Простой тест должен проходить");
    }
}