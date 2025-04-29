package com.adepuu.montrack_v2.auth.domain.valueObject;

import com.adepuu.montrack_v2.auth.domain.entities.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDetail implements UserDetails {
  private Set<UserRole> userRoles = new LinkedHashSet<>();
  private String email;
  private String password;

  @Override
  @JsonIgnore // Ignore for serialization to prevent the setter issue
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var authorities = new ArrayList<GrantedAuthority>();

    for (UserRole role : userRoles) {
      authorities.add(
        new SimpleGrantedAuthority(role.getRole().getName().toUpperCase())
      );
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }
  
  // These methods are required by UserDetails interface but not used in this application
  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }
}
