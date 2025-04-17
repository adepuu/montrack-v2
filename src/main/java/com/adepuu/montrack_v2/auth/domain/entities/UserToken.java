package com.adepuu.montrack_v2.auth.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_tokens")
public class UserToken {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_tokens_id_gen")
  @SequenceGenerator(name = "user_tokens_id_gen", sequenceName = "user_tokens_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id")
  private User user;

  @Size(max = 255)
  @NotNull
  @Column(name = "token", nullable = false)
  private String token;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("now()")
  @Column(name = "updated_at")
  private Instant updatedAt;
  @Column(name = "deleted_at")
  private Instant deletedAt;

/*
 TODO [Reverse Engineering] create field to map the 'token_type' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "token_type", columnDefinition = "token_type not null")
    private Object tokenType;
*/
}