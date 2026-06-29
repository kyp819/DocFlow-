package com.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a clinic appointment booking.
 * 
 * <p>This entity manages the relationship between a patient and a doctor for a scheduled
 * appointment. It tracks the appointment date/time, status, and clinical notes.</p>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Link patient to doctor</li>
 *   <li>Store appointment datetime and status</li>
 *   <li>Track clinical notes for the appointment</li>
 *   <li>Record creation timestamp for audit purposes</li>
 * </ul>
 * </p>
 * 
 * <p>Status lifecycle:
 * <ul>
 *   <li>{@link AppointmentStatus#BOOKED} - Initial booking</li>
 *   <li>{@link AppointmentStatus#RESCHEDULED} - Changed to different time</li>
 *   <li>{@link AppointmentStatus#CANCELLED} - Cancelled by patient or doctor</li>
 *   <li>{@link AppointmentStatus#COMPLETED} - Appointment occurred</li>
 * </ul>
 * </p>
 * 
 * <p>Business rules:
 * <ul>
 *   <li>Appointment times must fall within doctor's availability schedule</li>
 *   <li>No overlapping appointments for doctor or patient</li>
 *   <li>Cannot book appointments in the past</li>
 *   <li>Each appointment is 30 minutes (default duration)</li>
 * </ul>
 * </p>
 * 
 * @see Doctor
 * @see Patient
 * @see AppointmentStatus
 * @see DoctorAvailability
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
