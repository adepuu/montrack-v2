package com.adepuu.montrack_v2.auth.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Filter;

import com.adepuu.montrack_v2.wallet.domain.entities.Wallet;
import com.adepuu.montrack_v2.wallet.domain.entities.Transaction;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Filter(name = "deletedAtFilter", condition = "deleted_at is null")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
  @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @Size(max = 255)
  @NotNull
  @Column(name = "email", nullable = false)
  private String email;

  @Size(max = 255)
  @NotNull
  @Column(name = "password", nullable = false)
  private String password;

  @Size(max = 255)
  @NotNull
  @Column(name = "pin", nullable = false)
  private String pin;

  @ColumnDefault("false")
  @Column(name = "email_verified")
  private Boolean emailVerified;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("now()")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<UserProvider> userProviders = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<UserRole> userRoles = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<UserToken> userTokens = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<Wallet> userWallets = new LinkedHashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<Transaction> userTransactions = new LinkedHashSet<>();

  @PrePersist
  public void prePersist() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }

  @PreRemove
  public void preRemove() {
    deletedAt = Instant.now();
  }
}