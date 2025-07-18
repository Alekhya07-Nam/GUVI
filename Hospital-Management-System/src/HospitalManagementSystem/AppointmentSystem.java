package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.sql.Timestamp;


public class AppointmentSystem {

    public int getOrCreatePatientId(Connection conn, String patientName) throws SQLException {
    String selectSql = "SELECT id FROM patients WHERE name = ?";
    PreparedStatement selectStmt = conn.prepareStatement(selectSql);
    selectStmt.setString(1, patientName);
    ResultSet rs = selectStmt.executeQuery();

    if (rs.next()) {
        return rs.getInt("id");
    } else {
        String insertSql = "INSERT INTO patients (name) VALUES (?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
        insertStmt.setString(1, patientName);
        insertStmt.executeUpdate();

        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("Creating patient failed, no ID obtained.");
        }
    }
}


    // Method to get a doctor ID from their name
    public int getDoctorId(Connection conn, String doctorName) throws SQLException {
        String sql = "SELECT doctor_id FROM doctors WHERE doctor_name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, doctorName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("doctor_id");
        }
        return -1; // Indicate not found
    }

    // Method to get a doctor name from their ID
    public String getDoctorNameById(Connection conn, int doctorId) throws SQLException {
        String sql = "SELECT doctor_name FROM doctors WHERE doctor_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, doctorId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("doctor_name");
        }
        return null; // Indicate not found
    }

    public List<String> getAllPatientNames(Connection conn) throws SQLException {
        List<String> patientNames = new ArrayList<>();
        String sql = "SELECT patient_name FROM patients ORDER BY patient_name ASC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            patientNames.add(rs.getString("patient_name"));
        }
        return patientNames;
    }

    public List<String> getAllDoctorNames(Connection conn) throws SQLException {
        List<String> doctorNames = new ArrayList<>();
        String sql = "SELECT doctor_name FROM doctors ORDER BY doctor_name ASC"; // Added ORDER BY
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            doctorNames.add(rs.getString("doctor_name"));
        }
        return doctorNames;
    }
    public Map<LocalDate, List<LocalTime>> getDoctorAvailableSlots(Connection conn, String doctorName)
            throws SQLException {
        Map<LocalDate, List<LocalTime>> availableSlots = new TreeMap<>(); // TreeMap for sorted dates
        int doctorId = getDoctorId(conn, doctorName);

        if (doctorId == -1) {
            return availableSlots; // Doctor not found
        }

        String sql = "SELECT available_date, available_time FROM doctor_availability " +
                "WHERE doctor_id = ? AND is_booked = FALSE " +
                "ORDER BY available_date ASC, available_time ASC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, doctorId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            LocalDate date = rs.getDate("available_date").toLocalDate();
            LocalTime time = rs.getTime("available_time").toLocalTime();

            // Add to the list for the specific date
            availableSlots.computeIfAbsent(date, k -> new ArrayList<>()).add(time);
        }
        return availableSlots;
    }

    // Core booking algorithm (now checks and updates doctor_availability)
    public String bookAppointment(Connection conn, String patientName, String doctorName, LocalDate date,
            LocalTime time) {
        try {
            int patientId = getOrCreatePatientId(conn, patientName);
            int doctorId = getDoctorId(conn, doctorName);

            if (doctorId == -1) {
                return "Error: Doctor not found.";
            }

            // Algorithm: Check if the slot is available and not booked in
            // doctor_availability
            String checkAvailabilitySql = "SELECT availability_id FROM doctor_availability " +
                    "WHERE doctor_id = ? AND available_date = ? AND available_time = ? AND is_booked = FALSE";
            PreparedStatement checkStmt = conn.prepareStatement(checkAvailabilitySql);
            checkStmt.setInt(1, doctorId);
            checkStmt.setDate(2, java.sql.Date.valueOf(date));
            checkStmt.setTime(3, java.sql.Time.valueOf(time));

            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                // Slot is either not available or already booked
                return "Slot is not available or already booked. Please select a different time.";
            }
            int availabilityId = rs.getInt("availability_id"); // Get the ID of the available slot

            // Start a transaction for atomicity
            conn.setAutoCommit(false);

            try {
                // Step 1: Mark the slot as booked in doctor_availability
                String updateAvailabilitySql = "UPDATE doctor_availability SET is_booked = TRUE WHERE availability_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateAvailabilitySql);
                updateStmt.setInt(1, availabilityId);
                updateStmt.executeUpdate();

                // Step 2: Insert the new appointment into the appointments table
                String insertAppointmentSql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertAppointmentSql);
                insertStmt.setInt(1, patientId);
                insertStmt.setInt(2, doctorId);
                insertStmt.setDate(3, java.sql.Date.valueOf(date));
                insertStmt.setTime(4, java.sql.Time.valueOf(time));

                insertStmt.executeUpdate();

                conn.commit(); // Commit the transaction
                return "Slot booked successfully!";

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e; // Re-throw to be caught by outer catch block
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "An error occurred while booking the appointment: " + e.getMessage();
        }
    }
public String viewAppointments(Connection conn, String patientName) {
    try {
        int patientId = getOrCreatePatientId(conn, patientName);
        StringBuilder appointmentList = new StringBuilder();

        // Updated query: column names aligned with your DB schema
        String sql = "SELECT p.name AS patient_name, d.doctor_name, a.appointment_date " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE p.id = ? " +
                     "ORDER BY a.appointment_date ASC";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, patientId);
        ResultSet rs = stmt.executeQuery();

        if (!rs.isBeforeFirst()) {
            return "No appointments found.";
        }

        while (rs.next()) {
            String patName = rs.getString("patient_name");
            String docName = rs.getString("doctor_name");
            Timestamp apptDateTime = rs.getTimestamp("appointment_date");
            LocalDate date = apptDateTime.toLocalDateTime().toLocalDate();
            LocalTime time = apptDateTime.toLocalDateTime().toLocalTime();

            appointmentList.append(String.format("Appointment for %s with Dr. %s on %s at %s\n",
                    patName, docName, date, time));
        }
        return appointmentList.toString();
    } catch (SQLException e) {
        e.printStackTrace();
        return "An error occurred while viewing appointments: " + e.getMessage();
    }
}

}