package com.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a user account in the clinic system.
 * 
 * <p>This entity stores authentication and profile information for all users including
 * admins, doctors, and patients. Users have role-based access control through the {@link Role} enum.</p>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Store authentication credentials (email, password)</li>
 *   <li>Maintain user identity information (fullName, phone)</li>
 *   <li>Track user status (enabled flag)</li>
 *   <li>Record account creation timestamp</li>
 * </ul>
 * </p>
 * 
 * <p>Security notes:
 * <ul>
 *   <li>Passwords are stored hashed (BCrypt) via Spring Security</li>
 *   <li>Email is unique and used for lookups</li>
 *   <li>Role determines access permissions throughout the application</li>
 * </ul>
 * </p>
 * 
 * @see Role
 * @see Doctor
 * @see Patient
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
