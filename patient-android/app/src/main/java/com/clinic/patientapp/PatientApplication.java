package com.clinic.patientapp;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PatientApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
