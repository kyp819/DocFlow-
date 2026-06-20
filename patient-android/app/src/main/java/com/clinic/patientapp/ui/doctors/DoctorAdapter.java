package com.clinic.patientapp.ui.doctors;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clinic.patientapp.data.local.entity.DoctorEntity;
import com.clinic.patientapp.databinding.ItemDoctorBinding;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final List<DoctorEntity> doctors = new ArrayList<>();
    private final OnDoctorClickListener clickListener;

    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorEntity doctor);
    }

    public DoctorAdapter(OnDoctorClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDoctors(List<DoctorEntity> newDoctors) {
        doctors.clear();
        if (newDoctors != null) {
            doctors.addAll(newDoctors);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoctorBinding binding = ItemDoctorBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DoctorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        DoctorEntity doctor = doctors.get(position);
        holder.bind(doctor, clickListener);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        private final ItemDoctorBinding binding;

        public DoctorViewHolder(ItemDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DoctorEntity doctor, OnDoctorClickListener listener) {
            binding.tvDoctorName.setText("Dr. " + doctor.getName());
            binding.tvSpecialization.setText(doctor.getSpecialization());
            binding.tvHospital.setText(doctor.getHospitalName());
            binding.tvExperience.setText(doctor.getExperience() + " yrs exp");
            binding.tvFee.setText("$" + String.format("%.2f", doctor.getConsultationFee()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDoctorClick(doctor);
                }
            });
        }
    }
}
