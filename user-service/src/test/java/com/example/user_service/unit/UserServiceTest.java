package com.example.user_service.unit;

import com.example.common.dto.UserResponseDTO;
import com.example.common.exception.UserNotFoundException;
import com.example.user_service.dto.UserRequestDTO;
import com.example.user_service.model.UserModel;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // Тест успешного создания пользователя
    @Test
    void testCreateUserSuccess() {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com");

        // Мокаем, что пользователя с таким email нет
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);

        // Мокаем сохранение (ничего не делаем, просто для безопасности)
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userService.createUser(request);

        assertNotNull(response.id());
        assertEquals("Alice", response.name());
        assertEquals("alice@example.com", response.email());

        verify(userRepository, times(1)).existsByEmail("alice@example.com");
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    // Тест, если пользователь с таким email уже существует
    @Test
    void testCreateUserEmailExists() {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));

        verify(userRepository, times(1)).existsByEmail("alice@example.com");
        verify(userRepository, never()).save(any(UserModel.class));
    }

    // Тест успешного поиска пользователя по ID
    @Test
    void testFindByIdSuccess() {
        UserModel user = new UserModel("Alice", "alice@example.com");
        user.setId("user-1");


        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.findById("user-1");

        assertEquals("user-1", response.id());
        assertEquals("Alice", response.name());
        assertEquals("alice@example.com", response.email());

        verify(userRepository, times(1)).findById("user-1");
    }

    // Тест, если пользователя не существует
    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById("user-1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById("user-1"));

        verify(userRepository, times(1)).findById("user-1");
    }
}
