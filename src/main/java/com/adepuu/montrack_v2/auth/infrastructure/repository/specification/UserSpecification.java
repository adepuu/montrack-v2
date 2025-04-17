package com.adepuu.montrack_v2.auth.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.adepuu.montrack_v2.auth.domain.entities.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class UserSpecification {
  public static Specification<User> searchByKeyword(String keyword) {
    return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (keyword == null || keyword.isEmpty()) {
        return cb.conjunction(); // No filtering if keyword is null or empty
      }
      return cb.or(
          cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
          cb.like(cb.lower(root.get("email")), "%" + keyword.toLowerCase() + "%")
      );
    };
  }

  public static Specification<User> getFilteredUsers(String searchText) {
    return Specification.where(searchByKeyword(searchText));
  }
}
