package com.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Represents a patient profile in the clinic system.
 * 
 * <p>This entity extends the basic user account with patient-specific medical information
 * such as date of birth, blood group, and emergency contact details. Each patient has a
 * one-to-one relationship with a {@link User} account.</p>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Store medical profile data (blood group, gender, date of birth)</li>
 *   <li>Maintain contact information (address, emergency contact)</li>
 *   <li>Link to user authentication account</li>
 * </ul>
 * </p>
 * 
 * <p>Business relationships:
 * <ul>
 *   <li>One-to-one with {@link User} (inheritance pattern)</li>
 *   <li>One-to-many with {@link Appointment} (booked appointments)</li>
 * </ul>
 * </p>
 * 
 * @see User
 * @see Appointment
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String address;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "emergency_contact", nullable = false)
    private String emergencyContact;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
