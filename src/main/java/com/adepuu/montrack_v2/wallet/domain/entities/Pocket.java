package com.adepuu.montrack_v2.wallet.domain.entities;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "pocket")
@Entity
public class Pocket {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_gen")
  @SequenceGenerator(name = "wallet_id_gen", sequenceName = "wallet_id_seq", allocationSize = 1)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Column(name = "budget", nullable = false)
  private Double budget;

  @Column(name = "realized", nullable = false)
  private Double realized;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "is_goal", nullable = false)
  @ColumnDefault("false")
  private boolean isGoal = false;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("now()")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;
}
