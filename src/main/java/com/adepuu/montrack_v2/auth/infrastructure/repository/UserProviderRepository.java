package com.adepuu.montrack_v2.auth.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adepuu.montrack_v2.auth.domain.entities.UserProvider;
import java.util.List;
import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.auth.domain.enums.LoginProviders;


public interface UserProviderRepository extends JpaRepository<UserProvider, Integer> {
  List<UserProvider> findByUserAndProvider(User user, LoginProviders provider);
}
