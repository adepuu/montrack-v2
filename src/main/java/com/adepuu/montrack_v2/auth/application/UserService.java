package com.adepuu.montrack_v2.auth.application;

import org.springframework.data.domain.Pageable;

import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.presentation.dtos.UpdateProfileRequest;
import com.adepuu.montrack_v2.common.PaginatedResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(User request);
    PaginatedResponse<User> getAllUsers(Pageable pageable, String search);
    User updateProfile(UpdateProfileRequest request, Integer userId);
    User getUserByEmail(String email);
    User profile(Integer id);
}
