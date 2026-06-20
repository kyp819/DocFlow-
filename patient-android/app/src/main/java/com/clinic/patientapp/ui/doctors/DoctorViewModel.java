package com.clinic.patientapp.ui.doctors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.clinic.patientapp.data.local.entity.DoctorEntity;
import com.clinic.patientapp.data.model.AvailabilityResponse;
import com.clinic.patientapp.data.repository.DoctorRepository;
import com.clinic.patientapp.utils.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DoctorViewModel extends ViewModel {

    private final DoctorRepository doctorRepository;

    private final MutableLiveData<Resource<Void>> syncResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<AvailabilityResponse>>> availabilities = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    private final LiveData<List<DoctorEntity>> doctorsList;

    @Inject
    public DoctorViewModel(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
        
        doctorsList = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return doctorRepository.getCachedDoctors();
            } else {
                return doctorRepository.searchCachedDoctors(query);
            }
        });
    }

    public LiveData<List<DoctorEntity>> getDoctorsList() {
        return doctorsList;
    }

    public LiveData<Resource<Void>> getSyncResult() {
        return syncResult;
    }

    public LiveData<Resource<List<AvailabilityResponse>>> getAvailabilities() {
        return availabilities;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void refreshDoctors() {
        doctorRepository.refreshDoctors(syncResult);
    }

    public void loadAvailabilities(Long doctorId) {
        doctorRepository.getActiveAvailabilities(doctorId, availabilities);
    }
}
