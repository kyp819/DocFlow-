package com.clinic.patientapp.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.clinic.patientapp.data.model.LoginResponse;
import com.clinic.patientapp.data.model.UserResponse;
import com.clinic.patientapp.data.repository.AuthRepository;
import com.clinic.patientapp.utils.Resource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Resource<LoginResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<UserResponse>> registerResult = new MutableLiveData<>();

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Resource<LoginResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Resource<UserResponse>> getRegisterResult() {
        return registerResult;
    }

    public void login(String email, String password) {
        authRepository.login(email, password, loginResult);
    }

    public void register(String fullName, String email, String password, String phone) {
        authRepository.register(fullName, email, password, phone, registerResult);
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
}
