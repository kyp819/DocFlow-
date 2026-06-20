package com.clinic.patientapp.ui.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.clinic.patientapp.data.model.AppointmentRequest;
import com.clinic.patientapp.data.model.AvailabilityResponse;
import com.clinic.patientapp.databinding.ActivityBookAppointmentBinding;
import com.clinic.patientapp.ui.doctors.DoctorViewModel;
import com.clinic.patientapp.utils.Resource;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookAppointmentActivity extends AppCompatActivity {

    private ActivityBookAppointmentBinding binding;
    private DoctorViewModel doctorViewModel;
    private AppointmentViewModel appointmentViewModel;

    private Long doctorId;
    private String selectedDate = "";
    private String selectedTime = "";
    private List<AvailabilityResponse> availabilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        doctorViewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        doctorId = getIntent().getLongExtra("doctor_id", -1);
        String doctorName = getIntent().getStringExtra("doctor_name");
        String specialization = getIntent().getStringExtra("doctor_specialization");
        double fee = getIntent().getDoubleExtra("consultation_fee", 0.0);
        String hospital = getIntent().getStringExtra("hospital_name");

        binding.tvDoctorName.setText("Dr. " + doctorName);
        binding.tvSpecialization.setText(specialization);
        binding.tvHospital.setText(hospital);
        binding.tvFee.setText("Consultation Fee: $" + String.format("%.2f", fee));

        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());
        binding.btnSelectTime.setOnClickListener(v -> showTimePicker());
        binding.btnBookAppointment.setOnClickListener(v -> attemptBooking());

        observeViewModel();

        if (doctorId != -1) {
            doctorViewModel.loadAvailabilities(doctorId);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedDate = formatter.format(selectedCal.getTime());
            binding.tvSelectedDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute);
            String amPm = hourOfDay >= 12 ? "PM" : "AM";
            int hour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
            binding.tvSelectedTime.setText(String.format(Locale.US, "%02d:%02d %s", hour, minute, amPm));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void observeViewModel() {
        doctorViewModel.getAvailabilities().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.tvAvailabilityList.setText("Loading schedules...");
                    break;
                case SUCCESS:
                    availabilityList = resource.data;
                    displayAvailabilitySchedule(availabilityList);
                    break;
                case ERROR:
                    binding.tvAvailabilityList.setText("Failed to load schedules.");
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        appointmentViewModel.getBookingResult().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnBookAppointment.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnBookAppointment.setEnabled(true);
                    Toast.makeText(this, "Appointment Booked Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnBookAppointment.setEnabled(true);
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void displayAvailabilitySchedule(List<AvailabilityResponse> list) {
        if (list == null || list.isEmpty()) {
            binding.tvAvailabilityList.setText("Doctor has not configured active schedules.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (AvailabilityResponse av : list) {
            sb.append("• ").append(av.getDayOfWeek())
              .append(": ").append(av.getStartTime().substring(0, 5))
              .append(" - ").append(av.getEndTime().substring(0, 5))
              .append("\n");
        }
        binding.tvAvailabilityList.setText(sb.toString().trim());
    }

    private void attemptBooking() {
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            return;
        }

        if (availabilityList == null || availabilityList.isEmpty()) {
            Toast.makeText(this, "Doctor has no active schedule configured", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateDoctorAvailability()) {
            return;
        }

        String notes = binding.etNotes.getText().toString().trim();
        AppointmentRequest request = new AppointmentRequest(doctorId, selectedDate, selectedTime, notes);
        appointmentViewModel.bookAppointment(request);
    }

    private boolean validateDoctorAvailability() {
        LocalDate date = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime time = LocalTime.parse(selectedTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        DayOfWeek day = date.getDayOfWeek();

        boolean dayMatch = false;
        boolean timeMatch = false;
        String availableWindow = "";

        for (AvailabilityResponse av : availabilityList) {
            if (av.getDayOfWeek().equals(day.name())) {
                dayMatch = true;
                LocalTime start = LocalTime.parse(av.getStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                LocalTime end = LocalTime.parse(av.getEndTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                availableWindow = av.getStartTime().substring(0, 5) + " - " + av.getEndTime().substring(0, 5);

                if (!time.isBefore(start) && !time.isAfter(end)) {
                    timeMatch = true;
                    break;
                }
            }
        }

        if (!dayMatch) {
            Toast.makeText(this, "Dr. is not available on " + day.name() + "s.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!timeMatch) {
            Toast.makeText(this, "Selected time falls outside availability: " + availableWindow, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
