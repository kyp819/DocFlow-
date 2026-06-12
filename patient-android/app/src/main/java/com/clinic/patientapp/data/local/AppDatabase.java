package com.clinic.patientapp.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.clinic.patientapp.data.local.dao.AppointmentDao;
import com.clinic.patientapp.data.local.dao.DoctorDao;
import com.clinic.patientapp.data.local.entity.AppointmentEntity;
import com.clinic.patientapp.data.local.entity.DoctorEntity;

@Database(entities = {DoctorEntity.class, AppointmentEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DoctorDao doctorDao();
    public abstract AppointmentDao appointmentDao();
}
