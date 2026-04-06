package com.clinic.service;

import com.clinic.dto.DoctorRequest;
import com.clinic.dto.DoctorResponse;
import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest request, String currentUserEmail);
    DoctorResponse updateDoctor(Long id, DoctorRequest request, String currentUserEmail);
    void deleteDoctor(Long id);
    DoctorResponse getDoctorById(Long id);
    DoctorResponse getDoctorByUserId(Long userId);
    List<DoctorResponse> listAndSearchDoctors(String specialization, String name);
}
