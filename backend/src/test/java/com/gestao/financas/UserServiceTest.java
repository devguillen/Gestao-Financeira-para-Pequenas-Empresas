package com.gestao.financas;

import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;
import com.gestao.financas.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repository;
    private PasswordEncoder encoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        userService = new UserService(repository, encoder); // injeção por construtor
    }

    @Test
    void register_encodesPasswordAndSavesUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("rawpass");

        when(encoder.encode("rawpass")).thenReturn("encodedpass");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(user);

        assertEquals("encodedpass", saved.getPassword());
        verify(repository).save(user);
    }

    @Test
    void findByUsername_returnsUserOptional() {
        User user = new User();
        user.setUsername("test");

        when(repository.findByUsername("test")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("test");

        assertTrue(result.isPresent());
        assertEquals("test", result.get().getUsername());
    }

    @Test
    void getUserById_returnsUser() {
        User user = new User();
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_userNotFound_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
