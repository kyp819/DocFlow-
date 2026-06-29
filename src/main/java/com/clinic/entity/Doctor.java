package com.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Represents a doctor profile in the clinic system.
 * 
 * <p>This entity extends the basic user account with doctor-specific information such as
 * specialization, qualifications, and consultation fees. Each doctor has a one-to-one
 * relationship with a {@link User} account.</p>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Store professional credentials (specialization, qualification, experience)</li>
 *   <li>Maintain availability schedules through {@link DoctorAvailability}</li>
 *   <li>Track consultation fees</li>
 *   <li>Provide professional biography</li>
 * </ul>
 * </p>
 * 
 * <p>Business relationships:
 * <ul>
 *   <li>One-to-one with {@link User} (inheritance pattern)</li>
 *   <li>One-to-many with {@link DoctorAvailability} (schedules)</li>
 *   <li>One-to-many with {@link Appointment} (booked appointments)</li>
 * </ul>
 * </p>
 * 
 * @see User
 * @see DoctorAvailability
 * @see Appointment
 */
@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String qualification;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private BigDecimal consultationFee;

    @Column(nullable = false)
    private String hospitalName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
