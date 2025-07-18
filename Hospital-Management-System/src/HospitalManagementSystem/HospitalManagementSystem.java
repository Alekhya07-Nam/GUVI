package HospitalManagementSystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
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
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection);
            Doctor doctor = new Doctor(connection);

            JFrame frame = new JFrame("\uD83C\uDFE5 Hospital Management System");
            frame.setSize(700, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel backgroundPanel = new JPanel(new BorderLayout()) {
                private Image backgroundImage;
                {
                    try {
                        URL bgUrl = new URL("https://riseapps.co/wp-content/uploads/2023/05/How-to-Develop-Hospital-Management-System-for-Data-Automation-Full-Guide.png");
                        backgroundImage = new ImageIcon(bgUrl).getImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (backgroundImage != null) {
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    }
                }
            };

            // Logo from web
            

            JPanel outerPanel = new JPanel();
            outerPanel.setLayout(new GridBagLayout());
            outerPanel.setOpaque(false);

            JPanel cardPanel = new JPanel(new BorderLayout());
            cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
            cardPanel.setBackground(new Color(255, 255, 255, 220));

            JLabel welcomeLabel = new JLabel(" Hospital Management System", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
            welcomeLabel.setForeground(new Color(0, 51, 102));
            cardPanel.add(welcomeLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 15, 15));
            buttonPanel.setOpaque(false);

            JButton[] buttons = {
                createButton("Add Patient", "https://cdn-icons-png.flaticon.com/512/1159/1159633.png"),
                createButton("View Patients", "https://cdn-icons-png.flaticon.com/512/599/599305.png"),
                createButton("View Doctors", "https://cdn-icons-png.flaticon.com/512/3774/3774299.png"),
                createButton("Add Doctor", "https://cdn-icons-png.flaticon.com/512/4285/4285451.png"),
                createButton("Search Doctor", "https://cdn-icons-png.flaticon.com/512/3031/3031293.png"),
                createButton("Book Appointment", "https://cdn-icons-png.flaticon.com/512/747/747310.png"),
                 createButton("View Appointment", "https://cdn-icons-png.flaticon.com/512/747/747310.png"),
                  createButton("Ask Suggestions", "https://cdn-icons-png.flaticon.com/512/747/747310.png"),
                createButton("Exit", "https://cdn-icons-png.flaticon.com/512/1828/1828479.png")
            };

            for (JButton button : buttons) {
                buttonPanel.add(button);
            }

            cardPanel.add(buttonPanel, BorderLayout.CENTER);
            outerPanel.add(cardPanel);
            backgroundPanel.add(outerPanel, BorderLayout.CENTER);
            frame.add(backgroundPanel);

            buttons[0].addActionListener(e -> patient.addPatient());
            buttons[1].addActionListener(e -> patient.viewPatients());
            buttons[2].addActionListener(e -> doctor.viewDoctorsGUI());
            buttons[3].addActionListener(e -> doctor.addDoctorGUI());
            buttons[4].addActionListener(e -> doctor.searchDoctorGUI());
            buttons[5].addActionListener(e -> showAppointmentPopup(patient, doctor, connection));
   buttons[7].addActionListener(e -> {
    new SymptomSuggestion(); 
});




          buttons[6].addActionListener(e -> {
    String patientName = JOptionPane.showInputDialog(null, "Enter Patient Name:");
    if (patientName != null && !patientName.trim().isEmpty()) {
        AppointmentSystem appointmentSystem = new AppointmentSystem();
        String result = appointmentSystem.viewAppointments(connection, patientName);
        JOptionPane.showMessageDialog(null, result);
    } else {
        JOptionPane.showMessageDialog(null, "Patient name is required.");
    }
});

            buttons[8].addActionListener((ActionEvent e) -> {
                JOptionPane.showMessageDialog(frame, "Thank you for using the Hospital Management System!");
                frame.dispose();
            });

            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static JButton createButton(String text, String iconUrl) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(245, 255, 250));
        button.setForeground(new Color(0, 51, 102));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);

        try {
            ImageIcon icon = new ImageIcon(new URL(iconUrl));
            button.setIcon(scaleIcon(icon, 30, 30));
        } catch (Exception e) {
            e.printStackTrace();
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(204, 229, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 255, 250));
            }
        });

        return button;
    }

    private static Icon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

   public static void showAppointmentPopup(Patient patient, Doctor doctor, Connection connection) {
    JFrame frame = new JFrame("Book Appointment");
    frame.setSize(400, 300);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel titleLabel = new JLabel("Book an Appointment");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    panel.add(titleLabel, gbc);
    gbc.gridwidth = 1;

    JLabel patientIdLabel = new JLabel("Patient ID:");
    JTextField patientIdField = new JTextField();
    JLabel doctorIdLabel = new JLabel("Doctor ID:");
    JTextField doctorIdField = new JTextField();
    JLabel dateLabel = new JLabel("Appointment Date (YYYY-MM-DD):");
    JTextField dateField = new JTextField();

    gbc.gridy++;
    panel.add(patientIdLabel, gbc);
    gbc.gridx = 1;
    panel.add(patientIdField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    panel.add(doctorIdLabel, gbc);
    gbc.gridx = 1;
    panel.add(doctorIdField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    panel.add(dateLabel, gbc);
    gbc.gridx = 1;
    panel.add(dateField, gbc);
    JButton bookButton = new JButton("Book Appointment");
    JButton clearButton = new JButton("Clear");

    gbc.gridy++;
    gbc.gridx = 0;
    panel.add(clearButton, gbc);
    gbc.gridx = 1;
    panel.add(bookButton, gbc);

    frame.add(panel);

    clearButton.addActionListener(e -> {
        patientIdField.setText("");
        doctorIdField.setText("");
        dateField.setText("");
    });

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

            String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
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
