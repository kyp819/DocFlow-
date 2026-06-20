package com.clinic.patientapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.clinic.patientapp.data.model.PatientRequest;
import com.clinic.patientapp.data.model.PatientResponse;
import com.clinic.patientapp.data.repository.PatientRepository;
import com.clinic.patientapp.utils.Resource;
import com.clinic.patientapp.utils.TokenManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final PatientRepository patientRepository;
    private final TokenManager tokenManager;

    private final MutableLiveData<Resource<PatientResponse>> profileResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<PatientResponse>> createProfileResult = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(PatientRepository patientRepository, TokenManager tokenManager) {
        this.patientRepository = patientRepository;
        this.tokenManager = tokenManager;
    }

    public LiveData<Resource<PatientResponse>> getProfileResult() {
        return profileResult;
    }

    public LiveData<Resource<PatientResponse>> getCreateProfileResult() {
        return createProfileResult;
    }

    public String getUserName() {
        return tokenManager.getUserName();
    }

    public String getUserEmail() {
        return tokenManager.getUserEmail();
    }

    public void loadMyProfile() {
        patientRepository.getMyProfile(profileResult);
    }

    public void createProfile(String gender, String dob, String address, String bloodGroup, String emergencyContact) {
        PatientRequest request = new PatientRequest(gender, dob, address, bloodGroup, emergencyContact);
        patientRepository.createProfile(request, createProfileResult);
    }

    public void logout() {
        tokenManager.clear();
    }
}
