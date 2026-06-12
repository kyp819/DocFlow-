package com.clinic.patientapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.clinic.patientapp.data.local.entity.AppointmentEntity;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppointmentEntity> appointments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppointmentEntity appointment);

    @Query("SELECT * FROM appointments ORDER BY appointmentDate DESC, appointmentTime DESC")
    LiveData<List<AppointmentEntity>> getAllAppointments();

    @Query("SELECT * FROM appointments WHERE id = :id")
    LiveData<AppointmentEntity> getAppointmentById(long id);

    @Query("DELETE FROM appointments WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM appointments")
    void deleteAll();
}
