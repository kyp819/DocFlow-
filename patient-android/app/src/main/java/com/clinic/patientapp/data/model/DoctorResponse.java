package com.clinic.patientapp.data.model;

public class DoctorResponse {
    private Long id;
    private String specialization;
    private String qualification;
    private Integer experience;
    private double consultationFee;
    private String hospitalName;
    private String bio;
    private UserResponse user;

    public DoctorResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
}
