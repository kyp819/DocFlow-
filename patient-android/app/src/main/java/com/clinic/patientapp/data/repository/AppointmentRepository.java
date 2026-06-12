package com.clinic.patientapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.clinic.patientapp.data.local.dao.AppointmentDao;
import com.clinic.patientapp.data.local.entity.AppointmentEntity;
import com.clinic.patientapp.data.model.AppointmentRequest;
import com.clinic.patientapp.data.model.AppointmentResponse;
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
public class AppointmentRepository {

    private final ApiService apiService;
    private final AppointmentDao appointmentDao;
    private final AppExecutors appExecutors;

    @Inject
    public AppointmentRepository(ApiService apiService, AppointmentDao appointmentDao, AppExecutors executors) {
        this.apiService = apiService;
        this.appointmentDao = appointmentDao;
        this.appExecutors = executors;
    }

    public LiveData<List<AppointmentEntity>> getCachedAppointments() {
        return appointmentDao.getAllAppointments();
    }

    public void refreshMyAppointments(MutableLiveData<Resource<Void>> syncResult) {
        if (syncResult != null) {
            syncResult.postValue(Resource.loading(null));
        }

        apiService.getMyAppointments().enqueue(new Callback<List<AppointmentResponse>>() {
            @Override
            public void onResponse(Call<List<AppointmentResponse>> call, Response<List<AppointmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appExecutors.diskIO().execute(() -> {
                        List<AppointmentEntity> entities = new ArrayList<>();
                        for (AppointmentResponse appt : response.body()) {
                            entities.add(mapToEntity(appt));
                        }
                        appointmentDao.deleteAll();
                        appointmentDao.insertAll(entities);

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
            public void onFailure(Call<List<AppointmentResponse>> call, Throwable t) {
                if (syncResult != null) {
                    syncResult.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
                }
            }
        });
    }

    public void bookAppointment(AppointmentRequest request, MutableLiveData<Resource<AppointmentResponse>> result) {
        result.postValue(Resource.loading(null));

        apiService.bookAppointment(request).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppointmentResponse apptResponse = response.body();
                    appExecutors.diskIO().execute(() -> {
                        appointmentDao.insert(mapToEntity(apptResponse));
                        result.postValue(Resource.success(apptResponse));
                    });
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
    }

    public void cancelAppointment(Long appointmentId, MutableLiveData<Resource<AppointmentResponse>> result) {
        result.postValue(Resource.loading(null));

        apiService.cancelAppointment(appointmentId).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppointmentResponse apptResponse = response.body();
                    appExecutors.diskIO().execute(() -> {
                        appointmentDao.insert(mapToEntity(apptResponse)); // update local cache with status CANCELLED
                        result.postValue(Resource.success(apptResponse));
                    });
                } else {
                    result.postValue(Resource.error(parseError(response), null));
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                result.postValue(Resource.error("Connection failed: " + t.getMessage(), null));
            }
        });
    }

    private AppointmentEntity mapToEntity(AppointmentResponse response) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(response.getId());
        entity.setAppointmentDate(response.getAppointmentDate());
        entity.setAppointmentTime(response.getAppointmentTime());
        entity.setStatus(response.getStatus().name());
        entity.setNotes(response.getNotes());
        entity.setCreatedAt(response.getCreatedAt() != null ? response.getCreatedAt().toString() : "");
        if (response.getDoctor() != null) {
            entity.setDoctorId(response.getDoctor().getId());
            entity.setDoctorSpecialization(response.getDoctor().getSpecialization());
            if (response.getDoctor().getUser() != null) {
                entity.setDoctorName(response.getDoctor().getUser().getFullName());
            }
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
        return "Operation failed. Double check date/time overlaps.";
    }
}
