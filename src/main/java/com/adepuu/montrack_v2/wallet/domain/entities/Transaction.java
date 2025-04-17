package com.adepuu.montrack_v2.wallet.domain.entities;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import com.adepuu.montrack_v2.auth.domain.entities.User;
import com.adepuu.montrack_v2.wallet.domain.enums.TransactionType;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_gen")
  @SequenceGenerator(name = "wallet_id_gen", sequenceName = "wallet_id_seq", allocationSize = 1)
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pocket_id", nullable = false)
  private Pocket pocket;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "amount", nullable = false)
  private Double amount;

  @ColumnDefault("'EXPENSE'::transaction_type")
  @Column(name = "transaction_type", columnDefinition = "transaction_type not null")
  private TransactionType type;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "attachment_url", nullable = false)
  private String attachmentUrl;

  @ColumnDefault("now()")
  @Column(name = "created_at")
  private Instant createdAt;

  @ColumnDefault("now()")
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

}
