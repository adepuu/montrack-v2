package com.adepuu.montrack_v2.auth.application.impl;

import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.domain.exceptions.DuplicateUserException;
import com.adepuu.montrack_v2.auth.domain.exceptions.UserNotFoundException;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserProviderRepository;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserRepository;
import com.adepuu.montrack_v2.auth.presentation.dtos.UpdateProfileRequest;
import com.adepuu.montrack_v2.common.PaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private UserProviderRepository userProviderRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userServiceImpl;

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  private User testUser;
  private UpdateProfileRequest updateProfileRequest;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1);
    testUser.setEmail("test@example.com");
    testUser.setName("Test User");
    testUser.setPassword("password");
    testUser.setPin("1234");
    testUser.setEmailVerified(false);
    testUser.setCreatedAt(Instant.now());
    testUser.setUpdatedAt(Instant.now());

    updateProfileRequest = new UpdateProfileRequest();
    updateProfileRequest.setName("Updated User");
    updateProfileRequest.setEmail("updated@example.com");
  }

  @Nested
  @DisplayName("Register User Tests")
  class RegisterUserTest {
    @Test
    @DisplayName("Should register a user successfully")
    void shouldRegisterUserSuccessfully() {
      // Arrange/Given
      when(userRepository.countByEmail(testUser.getEmail())).thenReturn(0L);
      when(passwordEncoder.encode(testUser.getPassword())).thenReturn("hashedPassword");
      when(passwordEncoder.encode(testUser.getPin())).thenReturn("hashedPin");
      when(userRepository.save(testUser)).thenReturn(testUser);

      // When
      User result = userServiceImpl.registerUser(testUser);

      // Then
      verify(userRepository).countByEmail(testUser.getEmail());
      verify(passwordEncoder).encode("password");
      verify(passwordEncoder).encode("1234");
      verify(userRepository).save(testUser);

      assertEquals(testUser, result);
    }

    @Test
    @DisplayName("Should throw DuplicateUserException when user with same email exists")
    void shouldThrowDuplicateUserException() {
      // Arrange/Given
      when(userRepository.countByEmail(anyString())).thenReturn(1L);

      // Act & Assert
      assertThrows(DuplicateUserException.class, () -> {
        userServiceImpl.registerUser(testUser);
      });
      verify(userRepository).countByEmail(testUser.getEmail());
      verify(passwordEncoder, never()).encode("password");
      verify(passwordEncoder, never()).encode("1234");
      verify(userRepository, never()).save(testUser);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user request is malformed")
    void shouldThrowIllegalArgumentExceptionWhenUserRequestIsMalformed() {
      assertThrows(IllegalArgumentException.class, () -> {
        userServiceImpl.registerUser(null);
      });
      verify(userRepository, never()).countByEmail(testUser.getEmail());
      verify(passwordEncoder, never()).encode("password");
      verify(passwordEncoder, never()).encode("1234");
      verify(userRepository, never()).save(testUser);
    }
  }

  @Nested
  @DisplayName("Get All Users Tests")
  class GetAllUsersTest {
    @Test
    @DisplayName("Should return paginated list of users")
    void shouldReturnPaginatedListOfUsers() {
      // Arrange/Given
      Pageable pageable = PageRequest.of(0, 10);
      List<User> users = Collections.singletonList(testUser);
      Page<User> userPage = new PageImpl<>(users, pageable, users.size());

      // When
      when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class))).thenReturn(userPage);
      PaginatedResponse<User> result = userServiceImpl.getAllUsers(pageable, null);

      // Then
      verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
      assertEquals(0, result.getPage());
      assertEquals(10, result.getSize());
      assertEquals(1, result.getTotalElements());
      assertEquals(1, result.getTotalPages());
      assertEquals(users, result.getContent());
      assertNull(result.getContent().getFirst().getPassword());
      assertNull(result.getContent().getFirst().getPin());
    }

    @Test
    @DisplayName("Should return empty paginated list of users when no users found")
    void shouldReturnEmptyPaginatedListOfUsers() {
      // Arrange/Given
      Pageable pageable = PageRequest.of(0, 10);
      List<User> users = Collections.emptyList();
      Page<User> userPage = new PageImpl<>(users, pageable, users.size());

      // When
      when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class))).thenReturn(userPage);
      PaginatedResponse<User> result = userServiceImpl.getAllUsers(pageable, null);

      // Then
      verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
      assertEquals(0, result.getPage());
      assertEquals(10, result.getSize());
      assertEquals(0, result.getTotalElements());
      assertEquals(0, result.getTotalPages());
      assertEquals(users, result.getContent());
    }
  }

  @Nested
  @DisplayName("Update User Tests")
  class UpdateUserTest {
    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
      // Arrange/Given
      when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
      when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

      // When
      User result = userServiceImpl.updateProfile(updateProfileRequest, 1);

      // Assert
      verify(userRepository).findById(1);
      verify(userRepository).saveAndFlush(userArgumentCaptor.capture());

      User userCaptured = userArgumentCaptor.getValue();
      assertEquals("Updated User", userCaptured.getName());
      assertEquals("updated@example.com", userCaptured.getEmail());

      assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {
      when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

      assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateProfile(updateProfileRequest, 1));
      verify(userRepository).findById(1);
      verify(userRepository, never()).saveAndFlush(any(User.class));

    }

    @Test
    @DisplayName("Should throw exception when user is null")
    void shouldThrowExceptionWhenUserIdNull() {
      assertThrows(IllegalArgumentException.class, () -> userServiceImpl.updateProfile(updateProfileRequest, null));
      verify(userRepository, never()).findById(1);
      verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("Should handle partial update with null fields")
    void shouldHandlePartialFields() {
      // Arrange/Given
      UpdateProfileRequest req = new UpdateProfileRequest();
      req.setName("Updated User");
      req.setEmail(null);
      when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
      when(userRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

      // When
      User result = userServiceImpl.updateProfile(req, 1);

      // Assert
      verify(userRepository).findById(1);
      verify(userRepository).saveAndFlush(userArgumentCaptor.capture());

      User userCaptured = userArgumentCaptor.getValue();
      assertEquals("Updated User", userCaptured.getName());
      assertEquals("test@example.com", userCaptured.getEmail());

      assertNotNull(result);
    }
  }
}
