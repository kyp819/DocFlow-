package com.clinic.service;

import com.clinic.dto.PatientRequest;
import com.clinic.dto.PatientResponse;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request, String currentUserEmail);
    PatientResponse updatePatient(Long id, PatientRequest request, String currentUserEmail);
    void deletePatient(Long id);
    PatientResponse getPatientById(Long id);
    PatientResponse getPatientByUserId(Long userId);
    PatientResponse getPatientByEmail(String email);
}
