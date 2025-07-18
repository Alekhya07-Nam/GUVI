package HospitalManagementSystem;

import java.time.LocalDateTime;

public class ExtraAppointment {
    private Patient.PatientData patient;
    private Doctor.DoctorData doctor;
    private LocalDateTime appointmentTime;

    public ExtraAppointment(Patient.PatientData patient, Doctor.DoctorData doctor, LocalDateTime appointmentTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentTime = appointmentTime;
    }

    public Patient.PatientData getPatient() {
        return patient;
    }

    public Doctor.DoctorData getDoctor() {
        return doctor;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    @Override
    public String toString() {
        return String.format("Appointment for %s with Dr. %s on %s at %s",
                patient.getName(),
                doctor.getName(),
                appointmentTime.toLocalDate(),
                appointmentTime.toLocalTime());
    }
}
