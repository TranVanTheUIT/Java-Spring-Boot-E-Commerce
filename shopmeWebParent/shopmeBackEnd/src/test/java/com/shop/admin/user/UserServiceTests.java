package com.shop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shop.commom.entity.Role;
import com.shop.commom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListAllUsers() {
        // Arrange
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(new User("user1@example.com", "password", "John", "Doe"));
        mockUsers.add(new User("user2@example.com", "password", "Jane", "Smith"));

        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<User> listUsers = userService.listAll();

        // Assert
        assertThat(listUsers).hasSize(2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testSaveUserWithoutChangingPassword() {
        // Arrange
        User existingUser = new User("user1@example.com", "password", "John", "Doe");
        existingUser.setId(1);

        User userToUpdate = new User("updated@example.com", "", "John", "Updated");
        userToUpdate.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.save(userToUpdate);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password"); // Password shouldn't change
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void testSaveUserWithNewPassword() {
        // Arrange
        User newUser = new User("new@example.com", "newpassword", "New", "User");

        when(passwordEncoder.encode("newpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.save(newUser);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encodedpassword");
        verify(passwordEncoder, times(1)).encode("newpassword");
    }

    @Test
    public void testCheckEmailUnique() {
        // Arrange
        String email = "test@example.com";
        Integer userId = null; // New user

        when(userRepository.getUserByEmail(email)).thenReturn(null);

        // Act
        boolean isUnique = userService.isEmailUnique(email);

        // Assert
        assertThat(isUnique).isTrue();
    }

    @Test
    public void testCheckEmailNotUnique() {
        // Arrange
        String email = "existing@example.com";

        User existingUser = new User();
        existingUser.setId(10);
        existingUser.setEmail(email);

        when(userRepository.getUserByEmail(email)).thenReturn(existingUser);

        // Act
        boolean isUnique = userService.isEmailUnique(email);

        // Assert
        assertThat(isUnique).isFalse();
    }
}