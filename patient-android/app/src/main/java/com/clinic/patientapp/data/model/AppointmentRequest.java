package com.clinic.patientapp.data.model;

public class AppointmentRequest {
    private Long doctorId;
    private String appointmentDate; // yyyy-MM-dd
    private String appointmentTime; // HH:mm:ss
    private String notes;

    public AppointmentRequest() {}

    public AppointmentRequest(Long doctorId, String appointmentDate, String appointmentTime, String notes) {
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.notes = notes;
    }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
