package com.clinic.patientapp.di;

import android.content.Context;
import androidx.room.Room;

import com.clinic.patientapp.data.local.AppDatabase;
import com.clinic.patientapp.data.local.dao.AppointmentDao;
import com.clinic.patientapp.data.local.dao.DoctorDao;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "clinic_appointments_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    public DoctorDao provideDoctorDao(AppDatabase database) {
        return database.doctorDao();
    }

    @Provides
    @Singleton
    public AppointmentDao provideAppointmentDao(AppDatabase database) {
        return database.appointmentDao();
    }
}
