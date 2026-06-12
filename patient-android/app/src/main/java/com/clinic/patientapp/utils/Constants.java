package com.clinic.patientapp.utils;

public class Constants {
    // 10.0.2.2 is the IP address pointing to the host machine's localhost from Android Emulator
    public static final String BASE_URL = "https://docflow-p3f1.onrender.com/api/";
    
    // Local development URL (uncomment to point to local server)
    // public static final String BASE_URL = "http://10.0.2.2:8080/api/";

    public static final String PREFS_NAME = "patient_app_secure_prefs";
    public static final String KEY_JWT_TOKEN = "jwt_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_ROLE = "user_role";
}
