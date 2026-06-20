package com.clinic.patientapp.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.clinic.patientapp.R;
import com.clinic.patientapp.databinding.ActivityMainBinding;
import com.clinic.patientapp.ui.appointments.AppointmentsFragment;
import com.clinic.patientapp.ui.doctors.DoctorsFragment;
import com.clinic.patientapp.ui.profile.ProfileFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load Doctors tab by default
        loadFragment(new DoctorsFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_doctors) {
                fragment = new DoctorsFragment();
            } else if (itemId == R.id.nav_appointments) {
                fragment = new AppointmentsFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
