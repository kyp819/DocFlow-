package com.clinic.patientapp.ui.appointments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.clinic.patientapp.data.local.entity.AppointmentEntity;
import com.clinic.patientapp.data.model.AppointmentRequest;
import com.clinic.patientapp.data.model.AppointmentResponse;
import com.clinic.patientapp.data.repository.AppointmentRepository;
import com.clinic.patientapp.utils.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AppointmentViewModel extends ViewModel {

    private final AppointmentRepository appointmentRepository;

    private final MutableLiveData<Resource<Void>> syncResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<AppointmentResponse>> bookingResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<AppointmentResponse>> cancelResult = new MutableLiveData<>();

    @Inject
    public AppointmentViewModel(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public LiveData<List<AppointmentEntity>> getCachedAppointments() {
        return appointmentRepository.getCachedAppointments();
    }

    public LiveData<Resource<Void>> getSyncResult() {
        return syncResult;
    }

    public LiveData<Resource<AppointmentResponse>> getBookingResult() {
        return bookingResult;
    }

    public LiveData<Resource<AppointmentResponse>> getCancelResult() {
        return cancelResult;
    }

    public void refreshAppointments() {
        appointmentRepository.refreshMyAppointments(syncResult);
    }

    public void bookAppointment(AppointmentRequest request) {
        appointmentRepository.bookAppointment(request, bookingResult);
    }

    public void cancelAppointment(Long appointmentId) {
        appointmentRepository.cancelAppointment(appointmentId, cancelResult);
    }
}
