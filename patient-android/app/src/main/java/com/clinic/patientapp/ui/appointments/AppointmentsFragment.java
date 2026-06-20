package com.clinic.patientapp.ui.appointments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.clinic.patientapp.databinding.FragmentAppointmentsBinding;
import com.clinic.patientapp.utils.Resource;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppointmentsFragment extends Fragment {

    private FragmentAppointmentsBinding binding;
    private AppointmentViewModel viewModel;
    private AppointmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();

        observeViewModel();

        viewModel.refreshAppointments();
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(appt -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cancel Appointment")
                    .setMessage("Are you sure you want to cancel this appointment with Dr. " + appt.getDoctorName() + "?")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> viewModel.cancelAppointment(appt.getId()))
                    .setNegativeButton("No", null)
                    .show();
        });

        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAppointments.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refreshAppointments());
    }

    private void observeViewModel() {
        viewModel.getCachedAppointments().observe(getViewLifecycleOwner(), appts -> {
            adapter.setAppointments(appts);
            if (appts == null || appts.isEmpty()) {
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

        viewModel.getCancelResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    Toast.makeText(getContext(), "Appointment cancelled successfully!", Toast.LENGTH_SHORT).show();
                    viewModel.refreshAppointments();
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
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
