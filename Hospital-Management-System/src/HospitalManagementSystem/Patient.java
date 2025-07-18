package HospitalManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;

public class Patient {
    private Connection connection;

    public Patient(Connection connection) {
        this.connection = connection;
    }

    // Inner class to represent patient data, similar to DoctorData
    public class PatientData {
        private int id;
        private String name;
        private int age;
        private String gender;
        private List<Appointment> appointments; // To store appointments associated with this patient

        public PatientData(int id, String name, int age, String gender) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.appointments = new ArrayList<>();
        }

        // Getters for PatientData
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public List<Appointment> getAppointments() {
            return appointments;
        }

        // Method to add an appointment to this patient's list
        public void addAppointment(Appointment appointment) {
            this.appointments.add(appointment);
        }

        @Override
        public String toString() {
            return "Patient ID: " + id + ", Name: " + name + ", Age: " + age + ", Gender: " + gender;
        }
    }

    // Dummy Appointment class (You'll need to define this properly elsewhere, if not already)
    // For the purpose of this merge, a basic Appointment class is assumed.
    public class Appointment {
        private String patientName;
        private LocalDate date;
        private LocalTime time;
        private String doctorName;

        public Appointment(String patientName, LocalDate date, LocalTime time, String doctorName) {
            this.patientName = patientName;
            this.date = date;
            this.time = time;
            this.doctorName = doctorName;
        }

        public String getPatientName() {
            return patientName;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getTime() {
            return time;
        }

        public String getDoctorName() {
            return doctorName;
        }

        @Override
        public String toString() {
            return "Appointment [patientName=" + patientName + ", date=" + date + ", time=" + time + ", doctorName=" + doctorName + "]";
        }
    }


    public void addPatient() {
        JFrame frame = new JFrame("Add Patient");
        frame.setSize(450, 350); // Increased size slightly
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 250, 255)); // Light blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Add New Patient");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Name
        JLabel nameLabel = new JLabel("Patient Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        // Age
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(ageLabel, gbc);

        JTextField ageField = new JTextField(20);
        ageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(ageField, gbc);

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(genderLabel, gbc);

        String[] genders = {"Male", "Female", "Other"};
        JComboBox<String> genderComboBox = new JComboBox<>(genders);
        genderComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(genderComboBox, gbc);

        // Add Button
        JButton addButton = new JButton("Add Patient");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(0, 123, 255)); // Blue color
        addButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String gender = (String) genderComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Patient name cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (age <= 0) {
                    JOptionPane.showMessageDialog(frame, "Age must be a positive number!", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, age);
                preparedStatement.setString(3, gender);

                int rows = preparedStatement.executeUpdate();
                if (rows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newPatientId = generatedKeys.getInt(1);
                    }
                    JOptionPane.showMessageDialog(frame, "Patient Added Successfully!");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to Add Patient!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid age (numeric value)!", "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Error checking patient by ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public void viewPatients() {
        JFrame frame = new JFrame("All Patient Records");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(240, 248, 255)); 
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel filterLabel = new JLabel("in");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String[] filterOptions = {"Patient ID", "Name", "Gender", "Age"};
        JComboBox<String> filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(filterLabel);
        topPanel.add(filterCombo);

        String[] columns = {"Patient ID", "Name", "Gender", "Age"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFillsViewportHeight(true); // Table fills the entire height of the scroll pane

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        try {
            String query = "SELECT id, name, gender, age FROM patients ORDER BY name";
            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");
                    model.addRow(new Object[]{id, name, gender, age});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error loading patient data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }

            private void filterTable() {
                String text = searchField.getText().trim();
                int colIndex = filterCombo.getSelectedIndex(); // Corresponds to the column index in the table
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, colIndex));
                    } catch (PatternSyntaxException e) {
                        sorter.setRowFilter(null); // Clear filter if regex is invalid
                        System.err.println("Invalid regex pattern: " + text); // For debugging
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    public void viewPatientWithBillsGUI() {
        JFrame frame = new JFrame("Patient Records with Bills and Appointments");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600); // Larger size
        frame.setLayout(new BorderLayout(10, 10)); // Added padding
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(240, 248, 255)); // Light background
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel filterLabel = new JLabel("in");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Ensure filter options match the column order in the table model
        String[] filterOptions = {"Patient ID", "Name", "Gender", "Age", "Bill Amount", "Appointment Date"};
        JComboBox<String> filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(filterLabel);
        topPanel.add(filterCombo);

        String[] columns = {"Patient ID", "Name", "Gender", "Age", "Bill Amount", "Appointment Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        try {
            // Updated query to handle patients without appointments/bills (LEFT JOIN)
            String query = "SELECT p.id AS patient_id, p.name, p.gender, p.age, b.amount AS bill_amount, a.appointment_date " +
                    "FROM patients p " +
                    "LEFT JOIN appointments a ON p.id = a.patient_id " +
                    "LEFT JOIN bills b ON a.id = b.appointment_id AND a.id = b.appointment_id " + // Ensure bill matches appointment
                    "ORDER BY p.name, a.appointment_date"; // Order for better readability
            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("patient_id");
                    String name = rs.getString("name");
                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");
                    // Use Double for bill amount to handle NULLs gracefully
                    Object bill = rs.getObject("bill_amount"); // Can be NULL if no bill
                    String appointmentDate = rs.getString("appointment_date"); // Can be NULL if no appointment

                    model.addRow(new Object[]{id, name, gender, age, bill != null ? bill : "N/A", appointmentDate != null ? appointmentDate : "N/A"});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error loading patient, bill, or appointment data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }

            private void filterTable() {
                String text = searchField.getText().trim();
                int colIndex = filterCombo.getSelectedIndex();
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, colIndex));
                    } catch (PatternSyntaxException e) {
                        sorter.setRowFilter(null);
                        System.err.println("Invalid regex pattern: " + text);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}