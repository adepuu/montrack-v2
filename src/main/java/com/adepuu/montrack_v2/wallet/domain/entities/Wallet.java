package com.adepuu.montrack_v2.wallet.domain.entities;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

import com.adepuu.montrack_v2.auth.domain.entities.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "wallet")
@Entity
public class Wallet {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_gen")
  @SequenceGenerator(name = "wallet_id_gen", sequenceName = "wallet_id_seq", allocationSize = 1)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "budget", nullable = false)
  private Double budget;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "is_active", nullable = false)
  @ColumnDefault("true")
  private boolean isActive = true;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("now()")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
  private Set<Pocket> pockets = new LinkedHashSet<>();  
}
