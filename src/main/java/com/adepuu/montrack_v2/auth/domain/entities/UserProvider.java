package com.adepuu.montrack_v2.auth.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.adepuu.montrack_v2.auth.domain.enums.LoginProviders;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_providers")
public class UserProvider {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_providers_id_gen")
  @SequenceGenerator(name = "user_providers_id_gen", sequenceName = "user_providers_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;

  @Size(max = 255)
  @NotNull
  @Column(name = "provider_user_id", nullable = false)
  private String providerUserId;

  @Size(max = 255)
  @Column(name = "provider_access_token")
  private String providerAccessToken;

  @Size(max = 255)
  @Column(name = "provider_refresh_token")
  private String providerRefreshToken;

  @Column(name = "provider_expires_at")
  private Instant providerExpiresAt;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("now()")
  @Column(name = "updated_at")
  private Instant updatedAt;
  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider")
  @JdbcType(value = PostgreSQLEnumJdbcType.class)
  private LoginProviders provider;
}