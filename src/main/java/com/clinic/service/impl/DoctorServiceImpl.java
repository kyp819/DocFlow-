package com.clinic.service.impl;

import com.clinic.dto.DoctorRequest;
import com.clinic.dto.DoctorResponse;
import com.clinic.entity.Doctor;
import com.clinic.entity.Role;
import com.clinic.entity.User;
import com.clinic.exception.BadRequestException;
import com.clinic.exception.ConflictException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.DtoMapper;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.DoctorService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             UserRepository userRepository,
                             DtoMapper dtoMapper) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    @Override
    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request, String currentUserEmail) {
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        } else {
            user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        }

        if (user.getRole() != Role.DOCTOR) {
            throw new BadRequestException("User role must be DOCTOR to create a doctor profile");
        }

        if (doctorRepository.findByUserId(user.getId()).isPresent()) {
            throw new ConflictException("Doctor profile already exists for this user");
        }

        Doctor doctor = Doctor.builder()
                .specialization(request.getSpecialization())
                .qualification(request.getQualification())
                .experience(request.getExperience())
                .consultationFee(request.getConsultationFee())
                .hospitalName(request.getHospitalName())
                .bio(request.getBio())
                .user(user)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return dtoMapper.toDoctorResponse(savedDoctor);
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request, String currentUserEmail) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        // Security check: Only Admin or the Doctor owner can update
        if (currentUser.getRole() != Role.ADMIN && !doctor.getUser().getEmail().equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not authorized to update this doctor profile");
        }

        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setExperience(request.getExperience());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setHospitalName(request.getHospitalName());
        doctor.setBio(request.getBio());

        Doctor updatedDoctor = doctorRepository.save(doctor);
        return dtoMapper.toDoctorResponse(updatedDoctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found with id: " + id));
        doctorRepository.delete(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found with id: " + id));
        return dtoMapper.toDoctorResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user id: " + userId));
        return dtoMapper.toDoctorResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> listAndSearchDoctors(String specialization, String name) {
        List<Doctor> doctors = doctorRepository.searchDoctors(
                specialization != null && !specialization.trim().isEmpty() ? "%" + specialization.trim().toLowerCase() + "%" : null,
                name != null && !name.trim().isEmpty() ? "%" + name.trim().toLowerCase() + "%" : null
        );
        return doctors.stream()
                .map(dtoMapper::toDoctorResponse)
                .collect(Collectors.toList());
    }
}
