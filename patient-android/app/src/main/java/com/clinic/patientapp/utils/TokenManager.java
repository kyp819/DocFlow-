package com.clinic.patientapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import dagger.hilt.android.qualifiers.ApplicationContext;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Singleton
public class TokenManager {
    private SharedPreferences sharedPreferences;

    @Inject
    public TokenManager(@ApplicationContext Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    Constants.PREFS_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Fallback to basic private shared preferences if encrypted creation encounters exceptions
            sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(Constants.KEY_JWT_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.KEY_JWT_TOKEN, null);
    }

    public void saveUserData(Long id, String email, String name, String role) {
        sharedPreferences.edit()
                .putLong(Constants.KEY_USER_ID, id != null ? id : -1L)
                .putString(Constants.KEY_USER_EMAIL, email)
                .putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_ROLE, role)
                .apply();
    }

    public Long getUserId() {
        long id = sharedPreferences.getLong(Constants.KEY_USER_ID, -1L);
        return id != -1L ? id : null;
    }

    public String getUserEmail() {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, null);
    }

    public String getUserRole() {
        return sharedPreferences.getString(Constants.KEY_USER_ROLE, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
