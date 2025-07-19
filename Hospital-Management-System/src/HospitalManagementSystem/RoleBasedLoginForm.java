import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class RoleBasedLoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private String role;

    public RoleBasedLoginForm(String role) {
        this.role = role;

        setTitle(role.substring(0, 1).toUpperCase() + role.substring(1) + " Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 250, 255));
        setLayout(null);

        JLabel heading = new JLabel(role.toUpperCase() + " Login");
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBounds(130, 20, 200, 30);
        add(heading);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 80, 100, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 80, 180, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 130, 100, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 130, 180, 25);
        add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(140, 180, 120, 30);
        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.WHITE);
        add(loginBtn);

        statusLabel = new JLabel();
        statusLabel.setBounds(50, 220, 300, 25);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel);

        loginBtn.addActionListener(e -> handleLogin());

        setVisible(true);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("All fields are required.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospitalDB", "root", "sandy@F7"
            );

            String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                statusLabel.setForeground(new Color(0, 153, 0));
                statusLabel.setText("Login successful!");
                JOptionPane.showMessageDialog(this, "Welcome, " + role + "!");
                // TODO: Navigate to respective dashboard
                dispose(); // close login
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Invalid credentials.");
            }

            conn.close();
        } catch (SQLException e) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Database error.");
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return password;
        }
    }
}
