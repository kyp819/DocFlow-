package com.clinic.patientapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.clinic.patientapp.data.local.dao.DoctorDao;
import com.clinic.patientapp.data.local.entity.DoctorEntity;
import com.clinic.patientapp.data.model.AvailabilityResponse;
import com.clinic.patientapp.data.model.DoctorResponse;
import com.clinic.patientapp.data.model.ErrorResponse;
import com.clinic.patientapp.data.remote.ApiService;
import com.clinic.patientapp.utils.AppExecutors;
import com.clinic.patientapp.utils.Resource;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class DoctorRepository {

    private final ApiService apiService;
    private final DoctorDao doctorDao;
    private final AppExecutors appExecutors;

    @Inject
    public DoctorRepository(ApiService apiService, DoctorDao doctorDao, AppExecutors executors) {
        this.apiService = apiService;
        this.doctorDao = doctorDao;
        this.appExecutors = executors;
    }

    public LiveData<List<DoctorEntity>> getCachedDoctors() {
        return doctorDao.getAllDoctors();
    }

    public LiveData<List<DoctorEntity>> searchCachedDoctors(String nameOrSpecialization) {
        return doctorDao.searchDoctors("%" + nameOrSpecialization + "%");
    }

    public LiveData<DoctorEntity> getCachedDoctorById(long doctorId) {
        return doctorDao.getDoctorById(doctorId);
    }

    public void refreshDoctors(MutableLiveData<Resource<Void>> syncResult) {
        if (syncResult != null) {
            syncResult.postValue(Resource.loading(null));
        }

        apiService.searchDoctors(null, null).enqueue(new Callback<List<DoctorResponse>>() {
            @Override
            public void onResponse(Call<List<DoctorResponse>> call, Response<List<DoctorResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appExecutors.diskIO().execute(() -> {
                        List<DoctorEntity> entities = new ArrayList<>();
                        for (DoctorResponse doc : response.body()) {
                            entities.add(mapToEntity(doc));
                        }
                        // Replace database entries inside a transaction
                        doctorDao.deleteAll();
                        doctorDao.insertAll(entities);
                        
                        if (syncResult != null) {
                            syncResult.postValue(Resource.success(null));
                        }
                    });
                } else {
                    if (syncResult != null) {
                        syncResult.postValue(Resource.error(parseError(response), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DoctorResponse>> call, Throwable t) {
                if (syncResult != null) {
                    syncResult.postValue(Resource.error("Sync failed: " + t.getMessage(), null));
                }
            }
        });
    }

    public void getActiveAvailabilities(Long doctorId, MutableLiveData<Resource<List<AvailabilityResponse>>> result) {
        result.postValue(Resource.loading(null));
        apiService.getActiveAvailabilities(doctorId).enqueue(new Callback<List<AvailabilityResponse>>() {
            @Override
            public void onResponse(Call<List<AvailabilityResponse>> call, Response<List<AvailabilityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<List<AvailabilityResponse>> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
    }

    private DoctorEntity mapToEntity(DoctorResponse response) {
        DoctorEntity entity = new DoctorEntity();
        entity.setId(response.getId());
        entity.setSpecialization(response.getSpecialization());
        entity.setQualification(response.getQualification());
        entity.setExperience(response.getExperience());
        entity.setConsultationFee(response.getConsultationFee());
        entity.setHospitalName(response.getHospitalName());
        entity.setBio(response.getBio());
        if (response.getUser() != null) {
            entity.setName(response.getUser().getFullName());
            entity.setEmail(response.getUser().getEmail());
            entity.setPhone(response.getUser().getPhone());
        }
        return entity;
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
        return "Failed to load doctor details.";
    }
}
