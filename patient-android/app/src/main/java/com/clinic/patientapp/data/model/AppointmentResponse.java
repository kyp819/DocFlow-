package com.clinic.patientapp.data.model;

public class AppointmentResponse {
    private Long id;
    private DoctorResponse doctor;
    private PatientResponse patient;
    private String appointmentDate;
    private String appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private String createdAt;

    public AppointmentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DoctorResponse getDoctor() { return doctor; }
    public void setDoctor(DoctorResponse doctor) { this.doctor = doctor; }

    public PatientResponse getPatient() { return patient; }
    public void setPatient(PatientResponse patient) { this.patient = patient; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
