package com.clinic.patientapp.ui.appointments;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clinic.patientapp.data.local.entity.AppointmentEntity;
import com.clinic.patientapp.databinding.ItemAppointmentBinding;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<AppointmentEntity> appointments = new ArrayList<>();
    private final OnAppointmentCancelListener cancelListener;

    public interface OnAppointmentCancelListener {
        void onAppointmentCancel(AppointmentEntity appointment);
    }

    public AppointmentAdapter(OnAppointmentCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setAppointments(List<AppointmentEntity> newAppointments) {
        appointments.clear();
        if (newAppointments != null) {
            appointments.addAll(newAppointments);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentBinding binding = ItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AppointmentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentEntity appointment = appointments.get(position);
        holder.bind(appointment, cancelListener);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final ItemAppointmentBinding binding;

        public AppointmentViewHolder(ItemAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AppointmentEntity appointment, OnAppointmentCancelListener listener) {
            binding.tvDoctorName.setText("Dr. " + appointment.getDoctorName());
            binding.tvSpecialization.setText(appointment.getDoctorSpecialization());
            binding.tvDate.setText(appointment.getAppointmentDate());
            binding.tvTime.setText(appointment.getAppointmentTime());
            binding.tvNotes.setText(appointment.getNotes() != null && !appointment.getNotes().isEmpty()
                    ? "Notes: " + appointment.getNotes() : "No notes added");

            String status = appointment.getStatus();
            binding.tvStatus.setText(status);

            if ("BOOKED".equals(status) || "RESCHEDULED".equals(status)) {
                binding.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                binding.btnCancel.setVisibility(View.VISIBLE);
            } else if ("CANCELLED".equals(status)) {
                binding.tvStatus.setTextColor(Color.parseColor("#C62828"));
                binding.btnCancel.setVisibility(View.GONE);
            } else {
                binding.tvStatus.setTextColor(Color.parseColor("#37474F"));
                binding.btnCancel.setVisibility(View.GONE);
            }

            binding.btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAppointmentCancel(appointment);
                }
            });
        }
    }
}
