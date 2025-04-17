package com.adepuu.montrack_v2.auth.application;

import org.springframework.data.domain.Pageable;

import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.presentation.dtos.UpdateProfileRequest;
import com.adepuu.montrack_v2.common.PaginatedResponse;

public interface UserService {
    User registerUser(User request);
    PaginatedResponse<User> getAllUsers(Pageable pageable, String search);
    User updateProfile(UpdateProfileRequest request, Integer userId);
}
