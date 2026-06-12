package com.clinic.patientapp.data.model;

public class PatientRequest {
    private String gender;
    private String dateOfBirth; // format yyyy-MM-dd
    private String address;
    private String bloodGroup;
    private String emergencyContact;

    public PatientRequest() {}

    public PatientRequest(String gender, String dateOfBirth, String address, String bloodGroup, String emergencyContact) {
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
}
