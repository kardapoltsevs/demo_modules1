package service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.request.UserRequest;
import com.example.user.response.UserResponse;
import com.example.user.service.ConflService;
import com.example.user.service.UserService;
import com.example.user.utils.JwtUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private ConflService conflService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRequest userRequest;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @Tag("positive")
    public void getAllUsers_shouldReturnListOfUsers() {
        List<User> expectedUsers = Arrays.asList(
                User.builder().email("test1@example.com").name("User1").build(),
                User.builder().email("test2@example.com").name("User2").build()
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();
    }

    @Test
    @Tag("positive")
    void createUser_WhenEmailAvailable_ShouldReturnUserResponse() {

        String authHeader = "Bearer token123";
        String email = "test@example.com";
        String name = "Test User";
        String token = "token123";

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        userRequest.setName(name);

        User savedUser = User.builder()
                .email(email)
                .name(name)
                .build();

        Map<String, Object> responseBody = Map.of("available", true);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(jwtUtils.extractToken(authHeader)).thenReturn(token);
        when(conflService.checkEmail(email, token)).thenReturn(responseEntity);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> result = userService.createUser(authHeader, userRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserResponse);

        UserResponse userResponse = (UserResponse) result.getBody();
        assertEquals(email, userResponse.getEmail());
        assertEquals(name, userResponse.getName());

        verify(jwtUtils).extractToken(authHeader);
        verify(conflService).checkEmail(email, token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Tag("negative")
    void createUser_WhenEmailNotAvailable_ShouldReturnUnprocessableEntity() {
        String authHeader = "Bearer token123";
        String email = "existing@example.com";
        String name = "Existing User";
        String token = "token123";

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        userRequest.setName(name);

        Map<String, Object> responseBody = Map.of("available", false);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(jwtUtils.extractToken(authHeader)).thenReturn(token);
        when(conflService.checkEmail(email, token)).thenReturn(responseEntity);

        ResponseEntity<?> result = userService.createUser(authHeader, userRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("Такой email уже зарегистрирован", result.getBody());

        verify(jwtUtils).extractToken(authHeader);
        verify(conflService).checkEmail(email, token);
        verify(userRepository, never()).save(any(User.class));
    }
}