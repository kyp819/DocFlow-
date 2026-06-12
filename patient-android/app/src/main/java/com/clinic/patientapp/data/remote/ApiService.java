package com.clinic.patientapp.data.remote;

import com.clinic.patientapp.data.model.AppointmentRequest;
import com.clinic.patientapp.data.model.AppointmentResponse;
import com.clinic.patientapp.data.model.AvailabilityResponse;
import com.clinic.patientapp.data.model.DoctorResponse;
import com.clinic.patientapp.data.model.LoginRequest;
import com.clinic.patientapp.data.model.LoginResponse;
import com.clinic.patientapp.data.model.PatientRequest;
import com.clinic.patientapp.data.model.PatientResponse;
import com.clinic.patientapp.data.model.RegisterRequest;
import com.clinic.patientapp.data.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<UserResponse> register(@Body RegisterRequest request);

    @GET("doctors")
    Call<List<DoctorResponse>> searchDoctors(
            @Query("specialization") String specialization,
            @Query("name") String name
    );

    @GET("availabilities/doctor/{doctorId}/active")
    Call<List<AvailabilityResponse>> getActiveAvailabilities(@Path("doctorId") Long doctorId);

    @GET("appointments/me")
    Call<List<AppointmentResponse>> getMyAppointments();

    @POST("appointments")
    Call<AppointmentResponse> bookAppointment(@Body AppointmentRequest request);

    @PUT("appointments/{id}/cancel")
    Call<AppointmentResponse> cancelAppointment(@Path("id") Long id);

    @GET("patients/me")
    Call<PatientResponse> getMyProfile();

    @POST("patients")
    Call<PatientResponse> createProfile(@Body PatientRequest request);
}
