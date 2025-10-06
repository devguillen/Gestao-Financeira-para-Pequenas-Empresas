package com.gestao.financas.service;

import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("gabriel");
        user.setPassword("123456");
    }

    @Test
    void testRegister() {
        when(encoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.register(user);

        assertNotNull(saved);
        assertEquals("hashedPassword", saved.getPassword());
        verify(encoder, times(1)).encode("123456");
        verify(repository, times(1)).save(user);
    }

    @Test
    void testFindByUsernameFound() {
        when(repository.findByUsername("gabriel")).thenReturn(Optional.of(user));

        Optional<User> found = userService.findByUsername("gabriel");

        assertTrue(found.isPresent());
        assertEquals("gabriel", found.get().getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(repository.findByUsername("john")).thenReturn(Optional.empty());

        Optional<User> found = userService.findByUsername("john");

        assertFalse(found.isPresent());
    }

    @Test
    void testGetUserByIdFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserById(2L));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }
}
