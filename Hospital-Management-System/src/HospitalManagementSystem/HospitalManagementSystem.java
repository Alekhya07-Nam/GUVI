package HospitalManagementSystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.*;


public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Hanuman@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("\nHOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Add Doctor");
                System.out.println("5. Search Doctor");
                System.out.println("6. Book Appointment"); 
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        doctor.viewDoctorsGUI();
                        break;
                    case 4:
                        doctor.addDoctorGUI();
                        break;
                     case 5:
                        doctor.searchDoctorGUI();
                        break;
                    case 6:
                       showAppointmentPopup(patient, doctor, connection);
                        break;
                    case 7:
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void showAppointmentPopup(Patient patient, Doctor doctor, Connection connection) {
        JFrame frame = new JFrame("Book Appointment");
        frame.setSize(350, 250);
        frame.setLayout(new GridLayout(5, 2, 10, 10));
        frame.setLocationRelativeTo(null);

        JTextField patientIdField = new JTextField();
        JTextField doctorIdField = new JTextField();
        JTextField dateField = new JTextField();

        frame.add(new JLabel("Patient ID:"));
        frame.add(patientIdField);
        frame.add(new JLabel("Doctor ID:"));
        frame.add(doctorIdField);
        frame.add(new JLabel("Appointment Date (YYYY-MM-DD):"));
        frame.add(dateField);

        JButton bookButton = new JButton("Book");
        frame.add(new JLabel());
        frame.add(bookButton);

        bookButton.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(patientIdField.getText().trim());
                int doctorId = Integer.parseInt(doctorIdField.getText().trim());
                String appointmentDateStr = dateField.getText().trim();
                LocalDate appointmentDate;
                try {
                    appointmentDate = LocalDate.parse(appointmentDateStr);
                    if (appointmentDate.isBefore(LocalDate.now())) {
                        JOptionPane.showMessageDialog(frame, "Appointment date cannot be in the past.");
                        return;
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format! Use YYYY-MM-DD.");
                    return;
                }
                if (!patient.getPatientById(patientId)) {
                    JOptionPane.showMessageDialog(frame, "Patient ID does not exist.");
                    return;
                }
                if (!doctor.getDoctorById(doctorId)) {
                    JOptionPane.showMessageDialog(frame, "Doctor ID does not exist.");
                    return;
                }
                if (!checkDoctorAvailability(doctorId, appointmentDateStr, connection)) {
                    JOptionPane.showMessageDialog(frame, "Doctor already has an appointment on this date.");
                    return;
                }
                if (!checkPatientAvailability(patientId, appointmentDateStr, connection)) {
                    JOptionPane.showMessageDialog(frame, "Patient already has an appointment on this date.");
                    return;
                }
                String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, patientId);
                stmt.setInt(2, doctorId);
                stmt.setString(3, appointmentDateStr);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Appointment Booked Successfully!");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to book appointment.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric IDs.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database error occurred.");
            }
        });

        frame.setVisible(true);
    }

 

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

public static boolean checkPatientAvailability(int patientId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE patient_id = ? AND appointment_date = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            stmt.setString(2, appointmentDate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
