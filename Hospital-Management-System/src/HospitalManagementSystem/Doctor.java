package HospitalManagementSystem;

import java.sql.*;
import javax.swing.*;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Doctors: ");
            System.out.println("+------------+--------------------+------------------+");
            System.out.println("| Doctor Id  | Name               | Specialization   |");
            System.out.println("+------------+--------------------+------------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("| %-10s | %-18s | %-16s |\n", id, name, specialization);
                System.out.println("+------------+--------------------+------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void viewDoctorsGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("\uD83E\uDDEA Doctor Directory");
            frame.setSize(750, 500);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setBackground(new java.awt.Color(245, 250, 255));
            panel.setLayout(new java.awt.BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel title = new JLabel("Doctors Directory");
            title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(title, java.awt.BorderLayout.NORTH);

            JPanel topPanel = new JPanel(null);
            topPanel.setPreferredSize(new java.awt.Dimension(700, 50));
            topPanel.setBackground(new java.awt.Color(245, 250, 255));

            String[] specializations = {
                "All", "Cardiologist", "Dermatologist", "Neurologist",
                "Pediatrician", "Orthopedic", "Psychiatrist", "General Physician"
            };

            JComboBox<String> specDropdown = new JComboBox<>(specializations);
            specDropdown.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            specDropdown.setBounds(180, 10, 250, 25);
            topPanel.add(specDropdown);

            JButton filterBtn = new JButton("Filter");
            filterBtn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            filterBtn.setBackground(new java.awt.Color(0, 123, 255));
            filterBtn.setForeground(java.awt.Color.WHITE);
            filterBtn.setBounds(450, 10, 100, 25);
            topPanel.add(filterBtn);

            panel.add(topPanel, java.awt.BorderLayout.SOUTH);

            String[] columns = {"Doctor ID", "Name", "Specialization"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));

            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, java.awt.BorderLayout.CENTER);

            Runnable loadDoctors = () -> {
                model.setRowCount(0);
                String selected = specDropdown.getSelectedItem().toString();

                String query;
                if (selected.equals("All")) {
                    query = "SELECT * FROM doctors";
                } else {
                    query = "SELECT * FROM doctors WHERE specialization = ?";
                }

                try {
                    PreparedStatement ps = connection.prepareStatement(query);
                    if (!selected.equals("All")) {
                        ps.setString(1, selected);
                    }
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String spec = rs.getString("specialization");
                        model.addRow(new Object[]{id, name, spec});
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            };

            loadDoctors.run();

            filterBtn.addActionListener(e -> loadDoctors.run());

            frame.add(panel);
            frame.setVisible(true);
        });
    }

        public void searchDoctorGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(" Search Doctor");
            frame.setSize(750, 450);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(null);
            panel.setBackground(new java.awt.Color(245, 250, 255));

            JLabel title = new JLabel("Search Doctor");
            title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
            title.setBounds(280, 20, 250, 30);
            panel.add(title);

            JLabel searchLabel = new JLabel("Search By:");
            searchLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            searchLabel.setBounds(40, 80, 100, 25);
            panel.add(searchLabel);

            String[] options = {"ID", "Name", "Specialization"};
            JComboBox<String> criteriaBox = new JComboBox<>(options);
            criteriaBox.setBounds(140, 80, 150, 25);
            panel.add(criteriaBox);

            JTextField searchField = new JTextField();
            searchField.setBounds(310, 80, 200, 25);
            panel.add(searchField);

            JButton searchBtn = new JButton("Search");
            searchBtn.setBounds(530, 80, 100, 25);
            searchBtn.setBackground(new java.awt.Color(0, 123, 255));
            searchBtn.setForeground(java.awt.Color.WHITE);
            searchBtn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            panel.add(searchBtn);

            String[] columns = {"Doctor ID", "Name", "Specialization"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(40, 130, 640, 230);
            panel.add(scrollPane);

            searchBtn.addActionListener(e -> {
                model.setRowCount(0); // clear table
                String criteria = criteriaBox.getSelectedItem().toString();
                String keyword = searchField.getText().trim();

                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a value to search.");
                    return;
                }

                String query = "";
                switch (criteria) {
                    case "ID":
                        query = "SELECT * FROM doctors WHERE id = ?";
                        break;
                    case "Name":
                        query = "SELECT * FROM doctors WHERE LOWER(name) LIKE ?";
                        break;
                    case "Specialization":
                        query = "SELECT * FROM doctors WHERE LOWER(specialization) LIKE ?";
                        break;
                }

                try {
                    PreparedStatement ps = connection.prepareStatement(query);
                    if (criteria.equals("ID")) {
                        ps.setInt(1, Integer.parseInt(keyword));
                    } else {
                        ps.setString(1, "%" + keyword.toLowerCase() + "%");
                    }

                    ResultSet rs = ps.executeQuery();
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String spec = rs.getString("specialization");
                        model.addRow(new Object[]{id, name, spec});
                    }

                    if (!found) {
                        JOptionPane.showMessageDialog(frame, "No matching doctor found.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid numeric ID.");
                }
            });

            frame.add(panel);
            frame.setVisible(true);
        });
}
    public void addDoctorGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("\u2795 Add New Doctor");
            frame.setSize(420, 350);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(new java.awt.Color(245, 250, 255));

            JLabel titleLabel = new JLabel("Add Doctor");
            titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
            titleLabel.setBounds(140, 20, 200, 30);
            panel.add(titleLabel);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            nameLabel.setBounds(50, 80, 100, 25);
            panel.add(nameLabel);

            JTextField nameField = new JTextField();
            nameField.setBounds(160, 80, 180, 25);
            panel.add(nameField);

            JLabel specLabel = new JLabel("Specialization:");
            specLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            specLabel.setBounds(50, 130, 100, 25);
            panel.add(specLabel);

            String[] specializations = {
                "Cardiologist", "Dermatologist", "Neurologist",
                "Pediatrician", "Orthopedic", "Psychiatrist", "General Physician"
            };
            JComboBox<String> specDropdown = new JComboBox<>(specializations);
            specDropdown.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            specDropdown.setBounds(160, 130, 180, 25);
            panel.add(specDropdown);

            JButton addButton = new JButton("Add Doctor");
            addButton.setBounds(80, 200, 120, 35);
            addButton.setBackground(new java.awt.Color(0, 123, 255));
            addButton.setForeground(java.awt.Color.WHITE);
            addButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            panel.add(addButton);

            JButton resetButton = new JButton("Clear");
            resetButton.setBounds(220, 200, 120, 35);
            resetButton.setBackground(new java.awt.Color(220, 53, 69));
            resetButton.setForeground(java.awt.Color.WHITE);
            resetButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            panel.add(resetButton);

            addButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String specialization = specDropdown.getSelectedItem().toString();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter doctor's name.");
                } else {
                    try {
                        String query = "INSERT INTO doctors(name, specialization) VALUES(?, ?)";
                        PreparedStatement ps = connection.prepareStatement(query);
                        ps.setString(1, name);
                        ps.setString(2, specialization);
                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            JDialog successDialog = new JDialog(frame, "Success", true);
                            successDialog.setSize(320, 90);
                            successDialog.setLayout(new java.awt.BorderLayout());
                            successDialog.setUndecorated(true);
                            successDialog.setLocationRelativeTo(frame);

                            JLabel message = new JLabel("Doctor added successfully!", SwingConstants.CENTER);
                            message.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
                            successDialog.add(message, java.awt.BorderLayout.CENTER);

                            Timer timer = new Timer(3000, event -> {
                                successDialog.dispose();
                                frame.dispose();
                            });
                            timer.setRepeats(false);
                            timer.start();

                            successDialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to add doctor.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
                    }
                }
            });

            resetButton.addActionListener(e -> {
                nameField.setText("");
                specDropdown.setSelectedIndex(0);
            });

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}

