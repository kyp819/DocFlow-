package com.clinic.patientapp.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.clinic.patientapp.data.model.ErrorResponse;
import com.clinic.patientapp.data.model.LoginRequest;
import com.clinic.patientapp.data.model.LoginResponse;
import com.clinic.patientapp.data.model.RegisterRequest;
import com.clinic.patientapp.data.model.Role;
import com.clinic.patientapp.data.model.UserResponse;
import com.clinic.patientapp.data.remote.ApiService;
import com.clinic.patientapp.utils.Resource;
import com.clinic.patientapp.utils.TokenManager;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepository {

    private final ApiService apiService;
    private final TokenManager tokenManager;

    @Inject
    public AuthRepository(ApiService apiService, TokenManager tokenManager) {
        this.apiService = apiService;
        this.tokenManager = tokenManager;
    }

    public void login(String email, String password, MutableLiveData<Resource<LoginResponse>> result) {
        result.postValue(Resource.loading(null));
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    tokenManager.saveToken(loginResponse.getAccessToken());
                    UserResponse user = loginResponse.getUser();
                    if (user != null) {
                        tokenManager.saveUserData(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
                    }
                    result.postValue(Resource.success(loginResponse));
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
    }

    public void register(String fullName, String email, String password, String phone, MutableLiveData<Resource<UserResponse>> result) {
        result.postValue(Resource.loading(null));
        RegisterRequest request = new RegisterRequest(fullName, email, password, phone, Role.PATIENT);

        apiService.register(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
    }

    public void logout() {
        tokenManager.clear();
    }

    public boolean isLoggedIn() {
        return tokenManager.getToken() != null;
    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                ErrorResponse errorObj = new Gson().fromJson(errorJson, ErrorResponse.class);
                if (errorObj != null && errorObj.getMessage() != null) {
                    return errorObj.getMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Authentication failed. Please verify credentials.";
    }
}
