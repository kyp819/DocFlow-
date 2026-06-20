package com.clinic.patientapp.ui.doctors;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.clinic.patientapp.databinding.FragmentDoctorsBinding;
import com.clinic.patientapp.ui.appointments.BookAppointmentActivity;
import com.clinic.patientapp.utils.Resource;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DoctorsFragment extends Fragment {

    private FragmentDoctorsBinding binding;
    private DoctorViewModel viewModel;
    private DoctorAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDoctorsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupSwipeRefresh();

        observeViewModel();

        // Fetch doctors on view creation
        viewModel.refreshDoctors();
    }

    private void setupRecyclerView() {
        adapter = new DoctorAdapter(doctor -> {
            Intent intent = new Intent(getActivity(), BookAppointmentActivity.class);
            intent.putExtra("doctor_id", doctor.getId());
            intent.putExtra("doctor_name", doctor.getName());
            intent.putExtra("doctor_specialization", doctor.getSpecialization());
            intent.putExtra("consultation_fee", doctor.getConsultationFee());
            intent.putExtra("hospital_name", doctor.getHospitalName());
            startActivity(intent);
        });

        binding.rvDoctors.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDoctors.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refreshDoctors());
    }

    private void observeViewModel() {
        viewModel.getDoctorsList().observe(getViewLifecycleOwner(), doctors -> {
            adapter.setDoctors(doctors);
            if (doctors == null || doctors.isEmpty()) {
                binding.tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyState.setVisibility(View.GONE);
            }
        });

        viewModel.getSyncResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.swipeRefresh.setRefreshing(true);
                    break;
                case SUCCESS:
                    binding.swipeRefresh.setRefreshing(false);
                    break;
                case ERROR:
                    binding.swipeRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
