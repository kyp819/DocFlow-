package com.clinic.service.impl;

import com.clinic.dto.PatientRequest;
import com.clinic.dto.PatientResponse;
import com.clinic.entity.Patient;
import com.clinic.entity.Role;
import com.clinic.entity.User;
import com.clinic.exception.BadRequestException;
import com.clinic.exception.ConflictException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.DtoMapper;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.PatientService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    public PatientServiceImpl(PatientRepository patientRepository,
                              UserRepository userRepository,
                              DtoMapper dtoMapper) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    @Transactional
    public PatientResponse createPatient(PatientRequest request, String currentUserEmail) {
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        } else {
            user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        }

        if (user.getRole() != Role.PATIENT) {
            throw new BadRequestException("User role must be PATIENT to create a patient profile");
        }

        if (patientRepository.findByUserId(user.getId()).isPresent()) {
            throw new ConflictException("Patient profile already exists for this user");
        }

        Patient patient = Patient.builder()
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .bloodGroup(request.getBloodGroup())
                .emergencyContact(request.getEmergencyContact())
                .user(user)
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return dtoMapper.toPatientResponse(savedPatient);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long id, PatientRequest request, String currentUserEmail) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        // Security check: Only Admin or the Patient owner can update
        if (currentUser.getRole() != Role.ADMIN && !patient.getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not authorized to update this patient profile");
        }

        patient.setGender(request.getGender());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setAddress(request.getAddress());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setEmergencyContact(request.getEmergencyContact());

        Patient updatedPatient = patientRepository.save(patient);
        return dtoMapper.toPatientResponse(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found with id: " + id));
        patientRepository.delete(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found with id: " + id));
        return dtoMapper.toPatientResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user id: " + userId));
        return dtoMapper.toPatientResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByEmail(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for email: " + email));
        return dtoMapper.toPatientResponse(patient);
    }
}
