package com.phumlanidev.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Comment: this is the placeholder for documentation.
 */
@Entity
@Table(name = "audit_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "action")
  private String action;
  @Column(name = "user_id")
  private String userId;
  @Column(name = "username")
  private String username;
  @Column(name = "ip_address")
  private String ipAddress;
  @Column(name = "details")
  private String details;
  @Column(name = "timestamp")
  @Builder.Default
  private Instant timestamp = Instant.now();

}
