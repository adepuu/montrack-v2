package com.adepuu.montrack_v2.auth.presentation;

import java.nio.file.Path;

import com.adepuu.montrack_v2.common.security.Claims;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adepuu.montrack_v2.auth.application.UserService;
import com.adepuu.montrack_v2.auth.presentation.dtos.RegisterRequest;
import com.adepuu.montrack_v2.auth.presentation.dtos.UpdateProfileRequest;
import com.adepuu.montrack_v2.common.Response;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {
  private final UserService userService;

  public UserRestController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<?> getProfile() {
    Integer id = Claims.getUserId();
    return Response.successfulResponse("User profile fetched successfully",
        userService.profile(id));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    return Response.successfulResponse(
        "User registered successfully",
        userService.registerUser(request.toUser()));
  }

  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @GetMapping("/internal/user-list")
  public ResponseEntity<?> getUserList(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "sort", defaultValue = "id") String sort,
      @RequestParam(value = "search", defaultValue = "") String search) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sort)));

    return Response.successfulResponse(
        "User list fetched successfully",
        userService.getAllUsers(pageable, search));
  }

  @PutMapping
  public ResponseEntity<?> putMethodName(@RequestBody UpdateProfileRequest req) {
    Integer id = Claims.getUserId();
    return Response.successfulResponse(
        "User updated successfully",
        userService.updateProfile(req, id));
  }

  private Sort.Order getSortOrder(String sort) {
    String[] sortParts = sort.split(",");
    String property = sortParts[0];
    Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
    return Sort.Order.by(property).with(direction);
  }
}
