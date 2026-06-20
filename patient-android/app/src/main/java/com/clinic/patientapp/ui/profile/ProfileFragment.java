package com.clinic.patientapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.clinic.patientapp.data.model.PatientResponse;
import com.clinic.patientapp.databinding.FragmentProfileBinding;
import com.clinic.patientapp.ui.auth.LoginActivity;
import com.clinic.patientapp.utils.Resource;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding.tvProfileName.setText(viewModel.getUserName());
        binding.tvProfileEmail.setText(viewModel.getUserEmail());

        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        binding.btnSubmitProfile.setOnClickListener(v -> {
            String gender = binding.etGender.getText().toString().trim();
            String dob = binding.etDob.getText().toString().trim();
            String bloodGroup = binding.etBloodGroup.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String emergencyContact = binding.etEmergencyContact.getText().toString().trim();

            if (gender.isEmpty() || dob.isEmpty() || bloodGroup.isEmpty() || address.isEmpty() || emergencyContact.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.createProfile(gender, dob, address, bloodGroup, emergencyContact);
        });

        observeViewModel();

        viewModel.loadMyProfile();
    }

    private void observeViewModel() {
        viewModel.getProfileResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.layoutViewProfile.setVisibility(View.GONE);
                    binding.layoutCreateProfile.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.layoutViewProfile.setVisibility(View.VISIBLE);
                    binding.layoutCreateProfile.setVisibility(View.GONE);
                    populateProfileData(resource.data);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.message != null && (resource.message.contains("not found") || resource.message.contains("404") || resource.message.contains("missing"))) {
                        binding.layoutCreateProfile.setVisibility(View.VISIBLE);
                        binding.layoutViewProfile.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
                        binding.layoutCreateProfile.setVisibility(View.VISIBLE);
                        binding.layoutViewProfile.setVisibility(View.GONE);
                    }
                    break;
            }
        });

        viewModel.getCreateProfileResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSubmitProfile.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitProfile.setEnabled(true);
                    Toast.makeText(getContext(), "Profile configured successfully!", Toast.LENGTH_SHORT).show();
                    viewModel.loadMyProfile();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitProfile.setEnabled(true);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void populateProfileData(PatientResponse profile) {
        if (profile == null) return;
        binding.tvProfileName.setText(profile.getUser() != null ? profile.getUser().getFullName() : viewModel.getUserName());
        binding.tvProfileEmail.setText(profile.getUser() != null ? profile.getUser().getEmail() : viewModel.getUserEmail());
        
        binding.tvGender.setText("Gender: " + profile.getGender());
        binding.tvDob.setText("Date of Birth: " + profile.getDateOfBirth());
        binding.tvBloodGroup.setText("Blood Group: " + profile.getBloodGroup());
        binding.tvAddress.setText("Address: " + profile.getAddress());
        binding.tvEmergencyContact.setText("Emergency Contact: " + profile.getEmergencyContact());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
