package com.adepuu.montrack_v2.auth.application.impl;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.adepuu.montrack_v2.auth.application.UserService;
import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.domain.entities.UserProvider;
import com.adepuu.montrack_v2.auth.domain.enums.LoginProviders;
import com.adepuu.montrack_v2.auth.domain.exceptions.DuplicateUserException;
import com.adepuu.montrack_v2.auth.domain.exceptions.UserNotFoundException;
import com.adepuu.montrack_v2.auth.domain.valueObject.AuthUserDetail;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserProviderRepository;
import com.adepuu.montrack_v2.auth.infrastructure.repository.UserRepository;
import com.adepuu.montrack_v2.auth.infrastructure.repository.specification.UserSpecification;
import com.adepuu.montrack_v2.auth.presentation.dtos.UpdateProfileRequest;
import com.adepuu.montrack_v2.common.PaginatedResponse;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserProviderRepository userProviderRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
      UserProviderRepository userProviderRepository) {
    this.userRepository = userRepository;
    this.userProviderRepository = userProviderRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 1. Fetch users with same email from the database and count them
  // 2. If count != 0, throw DuplicateUserException
  // 3. If count == 0, hash the password and pin
  // 4. Save the user to the database
  // 5. Return the saved user
  @Override
  @Transactional
  @Caching(evict = {
    @CacheEvict(value = "userByEmailCache", allEntries = true),
    @CacheEvict(value = "allUsersCache", allEntries = true)
  })
  public User registerUser(User request) {
    if (request == null || request.getEmail() == null || request.getPassword() == null || request.getPin() == null) {
      throw new IllegalArgumentException("User request malformed");
    }
    long userWithSameEmail = userRepository.countByEmail(request.getEmail());
    if (userWithSameEmail != 0) {
      throw new DuplicateUserException("User with this email already exists");
    }

    request.setPassword(passwordEncoder.encode(request.getPassword()));
    request.setPin(passwordEncoder.encode(request.getPin()));
    request.setEmailVerified(false);

    User newlyInsertedUser = userRepository.save(request);

    UserProvider userProvider = new UserProvider();
    userProvider.setUser(newlyInsertedUser);
    userProvider.setProvider(LoginProviders.LOCAL);
    userProvider.setProviderUserId(newlyInsertedUser.getId().toString());

    userProviderRepository.save(userProvider);

    return newlyInsertedUser;
  }

  @Override
  @Cacheable(value = "allUsersCache", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #search")
  public PaginatedResponse<User> getAllUsers(Pageable pageable, String search) {
    Page<User> data = userRepository.findAll(UserSpecification.getFilteredUsers(search), pageable).map(user -> {
      user.setPassword(null); // Don't send password to the client
      user.setPin(null); // Don't send pin to the client
      return user;
    });

    PaginatedResponse<User> paginatedResponse = new PaginatedResponse<>();
    paginatedResponse.setPage(pageable.getPageNumber());
    paginatedResponse.setSize(pageable.getPageSize());
    paginatedResponse.setTotalElements(data.getTotalElements());
    paginatedResponse.setTotalPages(data.getTotalPages());
    paginatedResponse.setContent(data.getContent());

    return paginatedResponse;
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "userByEmailCache", key = "#result.email"),
    @CacheEvict(value = "userProfileCache", key = "#userId"),
    @CacheEvict(value = "allUsersCache", allEntries = true)
  })
  public User updateProfile(UpdateProfileRequest request, Integer userId) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }

    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

    if (request.getName() != null) {
      user.setName(request.getName());
    }
    
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }

    userRepository.saveAndFlush(user); 
    return user;
  }

  @Override
  @Cacheable(value = "userByEmailCache", key = "#email")
  public User getUserByEmail(String email) {
    Optional<User> user = userRepository.findUserByEmail(email);
    if (user.isPresent()) {
      return user.get();
    } else {
      throw new UserNotFoundException("User not found");
    }
  }

  @Override
  @Cacheable(value = "userProfileCache", key = "#id")
  public User profile(Integer id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      User foundUser = user.get();
      foundUser.setPassword(null); // Don't send password to the client
      foundUser.setPin(null); // Don't send pin to the client
      return foundUser;
    } else {
      throw new UserNotFoundException("User not found");
    }
  }

  @Override
  @Cacheable(value = "userDetailsCache", key = "#email")
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = getUserByEmail(email);
    AuthUserDetail userDetails = new AuthUserDetail();
    userDetails.setEmail(email);
    userDetails.setPassword(user.getPassword());
    userDetails.setUserRoles(user.getUserRoles());
    return userDetails;
  }
}
