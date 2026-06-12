package com.clinic.patientapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.clinic.patientapp.data.local.entity.DoctorEntity;

import java.util.List;

@Dao
public interface DoctorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DoctorEntity> doctors);

    @Query("SELECT * FROM doctors")
    LiveData<List<DoctorEntity>> getAllDoctors();

    @Query("SELECT * FROM doctors WHERE name LIKE :query OR specialization LIKE :query")
    LiveData<List<DoctorEntity>> searchDoctors(String query);

    @Query("SELECT * FROM doctors WHERE id = :id")
    LiveData<DoctorEntity> getDoctorById(long id);

    @Query("DELETE FROM doctors")
    void deleteAll();
}
