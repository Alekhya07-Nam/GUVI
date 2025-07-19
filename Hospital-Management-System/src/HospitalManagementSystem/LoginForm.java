import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Hospital Login");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 250, 255));

        JLabel title = new JLabel("Login as:");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(140, 20, 200, 30);
        add(title);

        JButton doctorBtn = new JButton("Doctor");
        doctorBtn.setBounds(60, 80, 100, 30);
        add(doctorBtn);

        JButton patientBtn = new JButton("Patient");
        patientBtn.setBounds(220, 80, 100, 30);
        add(patientBtn);

        doctorBtn.addActionListener(e -> {
            dispose();
            new RoleBasedLoginForm("doctor");
        });

        patientBtn.addActionListener(e -> {
            dispose();
            new RoleBasedLoginForm("patient");
        });

        setVisible(true);
    }
}
