import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class SignupForm extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JLabel statusLabel;

    public SignupForm() {
        setTitle("Hospital Management System - Signup");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        // Root panel with center alignment
        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(new Color(240, 250, 255));
        add(rootPanel);

        // Signup form panel (card)
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 450));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 220, 255), 1));
        card.setLayout(null);

        JLabel heading = new JLabel("Create Account");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBounds(120, 20, 200, 30);
        card.add(heading);

        addLabel(card, "Full Name:", 70);
        nameField = new JTextField();
        nameField.setBounds(140, 70, 200, 25);
        card.add(nameField);

        addLabel(card, "Email:", 120);
        emailField = new JTextField();
        emailField.setBounds(140, 120, 200, 25);
        card.add(emailField);

        addLabel(card, "Password:", 170);
        passwordField = new JPasswordField();
        passwordField.setBounds(140, 170, 200, 25);
        card.add(passwordField);

        addLabel(card, "Role:", 220);
        roleBox = new JComboBox<>(new String[]{"patient", "doctor"});
        roleBox.setBounds(140, 220, 200, 25);
        card.add(roleBox);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(140, 280, 120, 35);
        signupBtn.setBackground(new Color(0, 123, 255));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        card.add(signupBtn);

        statusLabel = new JLabel();
        statusLabel.setBounds(50, 330, 300, 25);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(statusLabel);

        signupBtn.addActionListener(e -> handleSignup());

        // Add card to center of root panel
        rootPanel.add(card);

        setVisible(true);
    }

    private void addLabel(JPanel panel, String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(30, y, 100, 25);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label);
    }

    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Invalid email format.");
            return;
        }

        if (password.length() < 6) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Password must be at least 6 characters.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospitalDB", "root", "sandy@F7"
            );

            String checkSql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Email already in use.");
                conn.close();
                return;
            }

            String insertSql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashPassword(password)); // password hashed
            stmt.setString(4, role);
            stmt.executeUpdate();

            statusLabel.setForeground(new Color(0, 153, 0));
            statusLabel.setText("Signup successful!");

            conn.close();

            JOptionPane.showMessageDialog(this, "Registration successful! Redirecting to login.");
            this.dispose();
            new LoginForm(); // open login selector

        } catch (SQLException e) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Database error.");
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Secure password hashing using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return password; // fallback (not secure)
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found.");
        }
        SwingUtilities.invokeLater(SignupForm::new);
    }
}
