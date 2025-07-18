package HospitalManagementSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class SymptomSuggestion {
    // Node class for Tree structure
    private static class Node {
        String symptom;
        String suggestion;
        Node left, right;

        Node(String symptom, String suggestion) {
            this.symptom = symptom;
            this.suggestion = suggestion;
        }
    }

    private Node root;

    public void insert(String symptom, String suggestion) {
        root = insertRec(root, symptom, suggestion);
    }

    private Node insertRec(Node root, String symptom, String suggestion) {
        if (root == null) return new Node(symptom, suggestion);
        if (symptom.compareTo(root.symptom) < 0)
            root.left = insertRec(root.left, symptom, suggestion);
        else if (symptom.compareTo(root.symptom) > 0)
            root.right = insertRec(root.right, symptom, suggestion);
        return root;
    }

    public String getSuggestionForSymptom(String symptom) {
        Node current = root;
        while (current != null) {
            int cmp = symptom.compareTo(current.symptom);
            if (cmp == 0) return current.suggestion;
            else if (cmp < 0) current = current.left;
            else current = current.right;
        }
        return " No suggestion available for this symptom.";
    }

    public void populateTree() {
        insert("fever", "Take paracetamol and rest. Drink warm water.");
        insert("headache", "Take adequate rest and  drink more water.");
        insert("cold", " Have hot soup and stay warm .");
        insert("cough", " Use cough syrup and steam inhalation.");
        insert("stomach pain", " Eat light food and consider an antacid.");
        insert("vomiting", "Stay hydrated. Take oral rehydration salts.");
    }

    // Main UI
    public SymptomSuggestion() {
        JFrame frame = new JFrame(" Health Symptom Suggestion System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLayout(new BorderLayout(10, 10));

        populateTree();

        // Title and Icon
        JLabel title = new JLabel("Enter Your Symptom Below ", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(title, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
       JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        inputPanel.add(iconLabel);

        JTextField inputField = new JTextField(20);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputPanel.add(inputField);

        JButton checkButton = new JButton("Get Suggestion ");
        checkButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputPanel.add(checkButton);

        frame.add(inputPanel, BorderLayout.CENTER);

        // Output area
        JTextArea outputArea = new JTextArea(5, 40);
        outputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        outputArea.setBackground(new Color(245, 255, 250));
        outputArea.setBorder(BorderFactory.createTitledBorder("Suggestion"));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        frame.add(scrollPane, BorderLayout.SOUTH);

        // Button Action
        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String symptom = inputField.getText().trim().toLowerCase();
                if (!symptom.isEmpty()) {
                    String result = getSuggestionForSymptom(symptom);
                    outputArea.setText(result);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a symptom.", "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new SymptomSuggestion());
}

}
