package com.clinic.patientapp.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.clinic.patientapp.data.model.ErrorResponse;
import com.clinic.patientapp.data.model.PatientRequest;
import com.clinic.patientapp.data.model.PatientResponse;
import com.clinic.patientapp.data.remote.ApiService;
import com.clinic.patientapp.utils.Resource;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PatientRepository {

    private final ApiService apiService;

    @Inject
    public PatientRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void getMyProfile(MutableLiveData<Resource<PatientResponse>> result) {
        result.postValue(Resource.loading(null));

        apiService.getMyProfile().enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
    }

    public void createProfile(PatientRequest request, MutableLiveData<Resource<PatientResponse>> result) {
        result.postValue(Resource.loading(null));

        apiService.createProfile(request).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
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
        return "Failed to load profile. Please make sure profile is created.";
    }
}
