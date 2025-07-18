// package HospitalManagementSystem;

// import java.awt.*;
// import java.io.File;
// import java.sql.*;
// import java.util.*;
// import java.util.List;
// import javax.swing.*;
// import javax.swing.border.EmptyBorder;
// import javax.swing.table.DefaultTableCellRenderer;
// import java.awt.image.BufferedImage;

// import javax.swing.Timer;

// public class Doctor {
//     private Connection connection;
//     private TreeMap<Integer, DoctorData> doctorMap = new TreeMap<>();
//     private HashMap<String, List<DoctorData>> specializationMap = new HashMap<>();
//     private HashMap<String, List<DoctorData>> doctorByNameMap = new HashMap<>();

//     private ImageIcon defaultDoctorIcon;
//     private ImageIcon onlineIcon;
//     private ImageIcon infoIcon;

//     public Doctor(Connection connection) {
//         this.connection = connection;
//         loadIcons();
//         refreshDoctorData();
//     }

//     private void loadIcons() {
//         try {
//             defaultDoctorIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/default_doctor.png")).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
//             onlineIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/online_dot.png")).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
//             infoIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/info_icon.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
//         } catch (Exception e) {
//             System.err.println("Error loading icons: " + e.getMessage());
//             defaultDoctorIcon = new ImageIcon(new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB)); // Placeholder
//             onlineIcon = null;
//             infoIcon = null;
//         }
//     }


//     class DoctorData {
//         int id;
//         String name;
//         String specialization;
//         String photoPath;
      
//         boolean isOnline; 

//         DoctorData(int id, String name, String specialization, String photoPath) {
//             this.id = id;
//             this.name = name;
//             this.specialization = specialization;
//             this.photoPath = photoPath;
//             this.isOnline = new Random().nextBoolean(); 
//         }

//         @Override
//         public String toString() {
//             return "ID: " + id + ", Name: " + name + ", Specialization: " + specialization + ", Photo: " + (photoPath != null ? photoPath : "N/A");
//         }
//     }

//     private void refreshDoctorData() {
//         doctorMap.clear();
//         specializationMap.clear();
//         doctorByNameMap.clear();

//         String query = "SELECT id, name, specialization, photo_path FROM doctors";
//         try (PreparedStatement preparedStatement = connection.prepareStatement(query);
//              ResultSet resultSet = preparedStatement.executeQuery()) {

//             while (resultSet.next()) {
//                 int id = resultSet.getInt("id");
//                 String name = resultSet.getString("name");
//                 String specialization = resultSet.getString("specialization");
//                 String photoPath = resultSet.getString("photo_path");
//                 DoctorData doctor = new DoctorData(id, name, specialization, photoPath);

//                 doctorMap.put(id, doctor);
//                 specializationMap.computeIfAbsent(specialization, k -> new ArrayList<>()).add(doctor);
//                 doctorByNameMap.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(doctor);
//             }
//         } catch (SQLException e) {
//             System.err.println("Error refreshing doctor data: " + e.getMessage());
//             e.printStackTrace();
//             JOptionPane.showMessageDialog(null, "Database error: Could not load doctor data.", "Error", JOptionPane.ERROR_MESSAGE);
//         }
//     }

  

//    private JPanel createDoctorCard(DoctorData doctor, JPanel contentPanel){
//         JPanel cardPanel = new JPanel();
//         cardPanel.setLayout(new BorderLayout());
//         Dimension cardSize = new Dimension(220, 250); 
//          cardPanel.setPreferredSize(cardSize);
//     cardPanel.setMinimumSize(cardSize);
//     cardPanel.setMaximumSize(cardSize);
//         cardPanel.setBackground(Color.WHITE);
//         cardPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true)); 
//         cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
//         JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
//         imagePanel.setBackground(Color.WHITE);
//         JLabel photoLabel = new JLabel();
//         ImageIcon doctorPhoto = defaultDoctorIcon; 
//         if (doctor.photoPath != null && new File(doctor.photoPath).exists()) {
//             try {
//                 ImageIcon originalIcon = new ImageIcon(doctor.photoPath);
//                 Image image = originalIcon.getImage();
//                 Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH); 
//                 doctorPhoto = new ImageIcon(scaledImage);
//             } catch (Exception ex) {
//                 System.err.println("Error loading image for Doctor ID " + doctor.id + ": " + ex.getMessage());
//             }
//         }
//         photoLabel.setIcon(doctorPhoto);
//         imagePanel.add(photoLabel);
//         cardPanel.add(imagePanel, BorderLayout.NORTH);

//         JPanel textPanel = new JPanel();
//         textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
//         textPanel.setBackground(Color.WHITE);
//         textPanel.setBorder(new EmptyBorder(0, 15, 0, 15)); 

//         JLabel nameLabel = new JLabel(doctor.name);
//         nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
//         nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
//         textPanel.add(nameLabel);

//         JLabel specLabel = new JLabel(doctor.specialization);
//         specLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//         specLabel.setForeground(Color.GRAY);
//         specLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
//         textPanel.add(specLabel);

//         cardPanel.add(textPanel, BorderLayout.CENTER);

//         JPanel bottomPanel = new JPanel(new BorderLayout());
//         bottomPanel.setBackground(Color.WHITE);
//         bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15)); 

//         JLabel statusLabel = new JLabel();
//         statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
//         if (doctor.isOnline) {
//             statusLabel.setForeground(new Color(0, 180, 0)); 
//             statusLabel.setText("Online");
//             if (onlineIcon != null) {
//                 statusLabel.setIcon(onlineIcon);
//                 statusLabel.setHorizontalTextPosition(SwingConstants.RIGHT); 
//                 statusLabel.setIconTextGap(5); 
//             }
//         } else {
//             statusLabel.setForeground(Color.GRAY);
//             statusLabel.setText("Offline");
//         }
//         bottomPanel.add(statusLabel, BorderLayout.WEST);

//         JButton infoButton = new JButton();
//         infoButton.setOpaque(false); 
//         infoButton.setContentAreaFilled(false);
//         infoButton.setBorderPainted(false);
//         infoButton.setFocusPainted(false);
//         infoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

//         if (infoIcon != null) {
//             infoButton.setIcon(infoIcon);
//         } else {
//             infoButton.setText("..."); 
//             infoButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
//             infoButton.setForeground(new Color(60, 140, 255));
//         }

//         infoButton.addActionListener(e -> {
//             JOptionPane.showMessageDialog(contentPanel,
//                 "Doctor Details:\n" +
//                 "ID: " + doctor.id + "\n" +
//                 "Name: " + doctor.name + "\n" +
//                 "Specialization: " + doctor.specialization + "\n" +
//                 "Photo Path: " + (doctor.photoPath != null ? doctor.photoPath : "N/A") + "\n" +
//                 "Status: " + (doctor.isOnline ? "Online" : "Offline"),
//                 "Doctor Info: " + doctor.name,
//                 JOptionPane.INFORMATION_MESSAGE);
//         });
//         bottomPanel.add(infoButton, BorderLayout.EAST);

//         cardPanel.add(bottomPanel, BorderLayout.SOUTH);

//         cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
//             private Color originalBorderColor = Color.LIGHT_GRAY;
//             private Color originalBackgroundColor = Color.WHITE;

//             @Override
//             public void mouseEntered(java.awt.event.MouseEvent evt) {
//                 cardPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2, true));
//             }

//             @Override
//             public void mouseExited(java.awt.event.MouseEvent evt) {
               
//                 if (!cardPanel.getClientProperty("isSelected").equals(true)) {
//                     cardPanel.setBorder(BorderFactory.createLineBorder(originalBorderColor, 1, true));
//                 }
//             }

//             @Override
//             public void mouseClicked(java.awt.event.MouseEvent evt) {
                
//               for (Component comp : contentPanel.getComponents()) {
//  {
//                     if (comp instanceof JPanel && comp != cardPanel) { 
//                         JPanel otherCard = (JPanel) comp;
//                         otherCard.setBackground(originalBackgroundColor);
//                         otherCard.setBorder(BorderFactory.createLineBorder(originalBorderColor, 1, true));
//                         otherCard.putClientProperty("isSelected", false);
//                     }
//                 }
                
//                 cardPanel.setBackground(new Color(230, 235, 255)); 
//                 cardPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 140, 255), 2, true)); 
     
//                        }           cardPanel.putClientProperty("isSelected", true);
//             }
// });
//         cardPanel.putClientProperty("isSelected", false); 


//         return cardPanel;
//     }

//    public void viewDoctorsGUI() {
//     SwingUtilities.invokeLater(() -> {
//         JFrame frame = new JFrame("Doctor Directory");
//         frame.setSize(1000, 700);
//         frame.setLocationRelativeTo(null);
//         frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//         JPanel mainPanel = new JPanel();
//         mainPanel.setBackground(new Color(245, 250, 255));
//         mainPanel.setLayout(new BorderLayout(15, 15));
//         mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

//         JLabel title = new JLabel("Doctors Directory");
//         title.setFont(new Font("Segoe UI", Font.BOLD, 28));
//         title.setHorizontalAlignment(SwingConstants.CENTER);
//         title.setBorder(new EmptyBorder(0, 0, 15, 0));
//         mainPanel.add(title, BorderLayout.NORTH);

//         JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
//         controlPanel.setBackground(new Color(245, 250, 255));
//         JLabel filterLabel = new JLabel("Filter by Specialization:");
//         filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//         controlPanel.add(filterLabel);

//         Set<String> uniqueSpecializations = new TreeSet<>(specializationMap.keySet());
//         uniqueSpecializations.add("All");
//         String[] specializations = uniqueSpecializations.toArray(new String[0]);
//         JComboBox<String> specDropdown = new JComboBox<>(specializations);
//         specDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//         specDropdown.setPreferredSize(new Dimension(200, 30));
//         controlPanel.add(specDropdown);

//         mainPanel.add(controlPanel, BorderLayout.SOUTH);

//         // ✅ Use JPanel with GridLayout instead of JLayeredPane
//         JPanel contentPanel = new JPanel();
//         contentPanel.setLayout(new GridLayout(0, 4, 20, 20)); // 4 cards per row
//         contentPanel.setBackground(new Color(245, 250, 255));

//         JScrollPane scrollPane = new JScrollPane(contentPanel);
//         scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//         scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//         scrollPane.setBorder(BorderFactory.createEmptyBorder());
//         scrollPane.getVerticalScrollBar().setUnitIncrement(16);

//         mainPanel.add(scrollPane, BorderLayout.CENTER);

//         Runnable loadDoctorsIntoCards = () -> {
//             contentPanel.removeAll();
//             contentPanel.revalidate();
//             contentPanel.repaint();

//             String selectedSpec = specDropdown.getSelectedItem().toString();
//             List<DoctorData> doctorsToDisplay = new ArrayList<>();

//             if (selectedSpec.equals("All")) {
//                 doctorsToDisplay.addAll(doctorMap.values());
//             } else {
//                 List<DoctorData> doctorsBySpec = specializationMap.get(selectedSpec);
//                 if (doctorsBySpec != null) {
//                     doctorsBySpec.sort(Comparator.comparing(d -> d.name));
//                     doctorsToDisplay.addAll(doctorsBySpec);
//                 }
//             }

//             if (doctorsToDisplay.isEmpty()) {
//                 JLabel noDoctorsLabel = new JLabel("No doctors found for this filter.");
//                 noDoctorsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
//                 noDoctorsLabel.setHorizontalAlignment(SwingConstants.CENTER);

//                 JPanel centerWrapper = new JPanel(new GridBagLayout());
//                 centerWrapper.setOpaque(false);
//                 centerWrapper.add(noDoctorsLabel);
//                 contentPanel.setLayout(new BorderLayout()); // Center align message
//                 contentPanel.add(centerWrapper, BorderLayout.CENTER);
//             } else {
//                 contentPanel.setLayout(new GridLayout(0, 4, 20, 20)); // Reapply layout
//                 for (DoctorData doc : doctorsToDisplay) {
//                     JPanel doctorCard = createDoctorCard(doc, contentPanel);
//                     contentPanel.add(doctorCard);
//                 }
//             }

//             contentPanel.revalidate();
//             contentPanel.repaint();
//         };

//         refreshDoctorData();
//         loadDoctorsIntoCards.run();

//         specDropdown.addActionListener(e -> {
//             loadDoctorsIntoCards.run();
//         });

//         frame.add(mainPanel);
//         frame.setVisible(true);
//     });
// }

   

//     public boolean getDoctorById(int id) {
//         if (doctorMap.containsKey(id)) {
//             return true;
//         }

//         String query = "SELECT id FROM doctors WHERE id = ?";
//         try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//             preparedStatement.setInt(1, id);
//             ResultSet resultSet = preparedStatement.executeQuery();
//             boolean exists = resultSet.next();
//             resultSet.close();
//             return exists;
//         } catch (SQLException e) {
//             System.err.println("Error checking doctor by ID: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public void viewDoctors() {
//         refreshDoctorData();
//         System.out.println("Doctors: ");
//         System.out.println("+------------+--------------------+------------------+------------------------------+");
//         System.out.println("| Doctor Id  | Name               | Specialization   | Photo Path                   |");
//         System.out.println("+------------+--------------------+------------------+------------------------------+");

//         for (DoctorData doc : doctorMap.values()) {
//             System.out.printf("| %-10d | %-18s | %-16s | %-28s |\n", doc.id, doc.name, doc.specialization, doc.photoPath != null ? doc.photoPath : "N/A");
//             System.out.println("+------------+--------------------+------------------+------------------------------+");
//         }
//         if (doctorMap.isEmpty()) {
//             System.out.println("| No doctors found in the system.                                                              |");
//             System.out.println("+--------------------------------------------------------------------------------------------+");
//         }
//     }


//     public void searchDoctorGUI() {
       
//         SwingUtilities.invokeLater(() -> {
//             JFrame frame = new JFrame("Search Doctor");
//             frame.setSize(850, 550);
//             frame.setLocationRelativeTo(null);
//             frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//             JPanel panel = new JPanel(null);
//             panel.setBackground(new java.awt.Color(245, 250, 255));

//             JLabel title = new JLabel("Search Doctor");
//             title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
//             title.setBounds(330, 20, 250, 30);
//             panel.add(title);

//             JLabel searchLabel = new JLabel("Search By:");
//             searchLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
//             searchLabel.setBounds(40, 80, 100, 25);
//             panel.add(searchLabel);

//             String[] options = {"ID", "Name", "Specialization"};
//             JComboBox<String> criteriaBox = new JComboBox<>(options);
//             criteriaBox.setBounds(140, 80, 150, 25);
//             panel.add(criteriaBox);

//             JTextField searchField = new JTextField();
//             searchField.setBounds(310, 80, 200, 25);
//             panel.add(searchField);

//             JButton searchBtn = new JButton("Search");
//             searchBtn.setBounds(530, 80, 100, 25);
//             searchBtn.setBackground(new java.awt.Color(0, 123, 255));
//             searchBtn.setForeground(java.awt.Color.WHITE);
//             searchBtn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
//             panel.add(searchBtn);

//             String[] columns = {"Doctor ID", "Name", "Specialization", "Photo"};
//             javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
//                  @Override
//                 public Class<?> getColumnClass(int columnIndex) {
//                     if (columnIndex == 3) {
//                         return ImageIcon.class;
//                     }
//                     return Object.class;
//                 }
//                 @Override
//                 public boolean isCellEditable(int row, int column) {
//                     return false;
//                 }
//             };
//             JTable table = new JTable(model);
//             table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));
//             table.setRowHeight(80);
//             table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));

//             table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());
//             table.getColumnModel().getColumn(3).setPreferredWidth(100);

//             JScrollPane scrollPane = new JScrollPane(table);
//             scrollPane.setBounds(40, 130, 750, 330);
//             panel.add(scrollPane);

//             searchBtn.addActionListener(e -> {
//                 model.setRowCount(0);
//                 String criteria = criteriaBox.getSelectedItem().toString();
//                 String keyword = searchField.getText().trim();
//                 List<DoctorData> results = new ArrayList<>();

//                 if (keyword.isEmpty()) {
//                     JOptionPane.showMessageDialog(frame, "Please enter a value to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                     return;
//                 }

//                 switch (criteria) {
//                     case "ID":
//                         try {
//                             int id = Integer.parseInt(keyword);
//                             DoctorData doc = doctorMap.get(id);
//                             if (doc != null) {
//                                 results.add(doc);
//                             }
//                         } catch (NumberFormatException ex) {
//                             JOptionPane.showMessageDialog(frame, "Please enter a valid numeric ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
//                             return;
//                         }
//                         break;
//                     case "Name":
//                         String lowerCaseKeyword = keyword.toLowerCase();
//                         Set<DoctorData> tempNameResults = new HashSet<>();
//                         for (Map.Entry<String, List<DoctorData>> entry : doctorByNameMap.entrySet()) {
//                             if (entry.getKey().contains(lowerCaseKeyword)) {
//                                 tempNameResults.addAll(entry.getValue());
//                             }
//                         }
//                         results.addAll(tempNameResults);
//                         results.sort(Comparator.comparing(d -> d.name));
//                         break;
//                     case "Specialization":
//                         String lowerCaseSpecKeyword = keyword.toLowerCase();
//                         List<String> matchingSpecializations = new ArrayList<>();
//                         for(String spec : specializationMap.keySet()){
//                             if(spec.toLowerCase().contains(lowerCaseSpecKeyword)){
//                                 matchingSpecializations.add(spec);
//                             }
//                         }
//                         Set<DoctorData> tempSpecResults = new HashSet<>();
//                         for(String matchedSpec : matchingSpecializations){
//                             List<DoctorData> doctors = specializationMap.get(matchedSpec);
//                             if(doctors != null){
//                                 tempSpecResults.addAll(doctors);
//                             }
//                         }
//                         results.addAll(tempSpecResults);
//                         results.sort(Comparator.comparing(d -> d.name));
//                         break;
//                 }

//                 if (results.isEmpty()) {
//                     JOptionPane.showMessageDialog(frame, "No matching doctor found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
//                 } else {
//                     for (DoctorData doc : results) {
//                         ImageIcon doctorPhoto = null;
//                         if (doc.photoPath != null && new File(doc.photoPath).exists()) {
//                             try {
//                                 ImageIcon originalIcon = new ImageIcon(doc.photoPath);
//                                 Image image = originalIcon.getImage();
//                                 Image scaledImage = image.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
//                                 doctorPhoto = new ImageIcon(scaledImage);
//                             } catch (Exception ex) {
//                                 System.err.println("Error loading image for Doctor ID " + doc.id + ": " + ex.getMessage());
//                                 doctorPhoto = null;
//                             }
//                         }
//                         model.addRow(new Object[]{doc.id, doc.name, doc.specialization, doctorPhoto});
//                     }
//                 }
//             });

//             frame.add(panel);
//             frame.setVisible(true);
//         });
//     }

//     class ImageRenderer extends DefaultTableCellRenderer {
//         JLabel lbl = new JLabel();

//         @Override
//         public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//             if (value instanceof ImageIcon) {
//                 lbl.setIcon((ImageIcon) value);
//                 lbl.setHorizontalAlignment(SwingConstants.CENTER);
//             } else {
//                 lbl.setIcon(null);
//                 lbl.setText("No Image");
//                 lbl.setHorizontalAlignment(SwingConstants.CENTER);
//             }
//             return lbl;
//         }
//     }


//     public void addDoctorGUI() {
//         SwingUtilities.invokeLater(() -> {
//             JFrame frame = new JFrame("Add New Doctor");
//             frame.setSize(460, 430);
//             frame.setLocationRelativeTo(null);
//             frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//             JPanel panel = new JPanel();
//             panel.setLayout(null);
//             panel.setBackground(new java.awt.Color(245, 250, 255));

//             JLabel titleLabel = new JLabel("Add Doctor");
//             titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
//             titleLabel.setBounds(150, 20, 200, 30);
//             panel.add(titleLabel);

//             JLabel nameLabel = new JLabel("Name:");
//             nameLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
//             nameLabel.setBounds(50, 80, 100, 25);
//             panel.add(nameLabel);

//             JTextField nameField = new JTextField();
//             nameField.setBounds(160, 80, 220, 25);
//             panel.add(nameField);

//             JLabel specLabel = new JLabel("Specialization:");
//             specLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
//             specLabel.setBounds(50, 130, 100, 25);
//             panel.add(specLabel);

//             String[] specializations = {
//                 "Cardiologist", "Dermatologist", "Neurologist",
//                 "Pediatrician", "Orthopedic", "Psychiatrist", "General Physician"
//             };
//             JComboBox<String> specDropdown = new JComboBox<>(specializations);
//             specDropdown.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
//             specDropdown.setBounds(160, 130, 220, 25);
//             panel.add(specDropdown);

//             JLabel photoLabel = new JLabel("Photo:");
//             photoLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
//             photoLabel.setBounds(50, 180, 100, 25);
//             panel.add(photoLabel);

//             JButton uploadBtn = new JButton("Choose Photo");
//             uploadBtn.setBounds(160, 180, 220, 25);
//             panel.add(uploadBtn);

//             final String[] photoPath = {null};

//             uploadBtn.addActionListener(e -> {
//                 JFileChooser fileChooser = new JFileChooser();
//                 fileChooser.setDialogTitle("Select Doctor Photo");
//                 int result = fileChooser.showOpenDialog(frame);
//                 if (result == JFileChooser.APPROVE_OPTION) {
//                     photoPath[0] = fileChooser.getSelectedFile().getAbsolutePath();
//                     JOptionPane.showMessageDialog(frame, "Photo selected:\n" + photoPath[0]);
//                 }
//             });

//             JButton addButton = new JButton("Add Doctor");
//             addButton.setBounds(90, 260, 120, 35);
//             addButton.setBackground(new java.awt.Color(0, 123, 255));
//             addButton.setForeground(java.awt.Color.WHITE);
//             addButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
//             panel.add(addButton);

//             JButton resetButton = new JButton("Clear");
//             resetButton.setBounds(230, 260, 120, 35);
//             resetButton.setBackground(new java.awt.Color(220, 53, 69));
//             resetButton.setForeground(java.awt.Color.WHITE);
//             resetButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
//             panel.add(resetButton);

//             addButton.addActionListener(e -> {
//                 String name = nameField.getText().trim();
//                 String specialization = specDropdown.getSelectedItem().toString();

//                 if (name.isEmpty()) {
//                     JOptionPane.showMessageDialog(frame, "Please enter doctor's name.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                 } else if (photoPath[0] == null) {
//                     JOptionPane.showMessageDialog(frame, "Please choose a photo.", "Input Error", JOptionPane.WARNING_MESSAGE);
//                 } else {
//                     try {
//                         String query = "INSERT INTO doctors(name, specialization, photo_path) VALUES(?, ?, ?)";
//                         PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
//                         ps.setString(1, name);
//                         ps.setString(2, specialization);
//                         ps.setString(3, photoPath[0]);

//                         int rows = ps.executeUpdate();

//                         if (rows > 0) {
//                             try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
//                                 if (generatedKeys.next()) {
//                                     int newId = generatedKeys.getInt(1);
//                                     DoctorData newDoctor = new DoctorData(newId, name, specialization, photoPath[0]);
//                                     doctorMap.put(newId, newDoctor);
//                                     specializationMap.computeIfAbsent(specialization, k -> new ArrayList<>()).add(newDoctor);
//                                     doctorByNameMap.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(newDoctor);
//                                 }
//                             }

//                             JDialog successDialog = new JDialog(frame, "Success", true);
//                             successDialog.setSize(320, 90);
//                             successDialog.setLayout(new java.awt.BorderLayout());
//                             successDialog.setUndecorated(true);
//                             successDialog.setLocationRelativeTo(frame);

//                             JLabel message = new JLabel("Doctor added successfully!", SwingConstants.CENTER);
//                             message.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
//                             successDialog.add(message, java.awt.BorderLayout.CENTER);

//                             Timer timer = new Timer(2000, event -> {
//                                 successDialog.dispose();
//                                 frame.dispose();
//                             });
//                             timer.setRepeats(false);
//                             timer.start();

//                             successDialog.setVisible(true);
//                         } else {
//                             JOptionPane.showMessageDialog(frame, "Failed to add doctor to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
//                         }
//                     } catch (SQLException ex) {
//                         System.err.println("SQL Error adding doctor: " + ex.getMessage());
//                         ex.printStackTrace();
//                         JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                     }
//                 }
//             });

//             resetButton.addActionListener(e -> {
//                 nameField.setText("");
//                 specDropdown.setSelectedIndex(0);
//                 photoPath[0] = null;
//             });

//             frame.add(panel);
//             frame.setVisible(true);
//         });
//     }

// }



package HospitalManagementSystem;

import java.awt.*;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class Doctor {
    private Connection connection;
    private TreeMap<Integer, DoctorData> doctorMap = new TreeMap<>();
    private HashMap<String, List<DoctorData>> specializationMap = new HashMap<>();
    private HashMap<String, List<DoctorData>> doctorByNameMap = new HashMap<>();

    private ImageIcon defaultDoctorIcon;
    private ImageIcon onlineIcon;
    private ImageIcon infoIcon;

    public Doctor(Connection connection) {
        this.connection = connection;
        loadIcons();
        refreshDoctorData();
    }

    private void loadIcons() {
        try {
            defaultDoctorIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/default_doctor.png")).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
            onlineIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/online_dot.png")).getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
            infoIcon = new ImageIcon(new ImageIcon(getClass().getResource("/images/info_icon.png")).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Error loading icons: " + e.getMessage());
            defaultDoctorIcon = new ImageIcon(new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB)); // Placeholder
            onlineIcon = null;
            infoIcon = null;
        }
    }

    // Existing DoctorData class
    class DoctorData {
        int id;
        String name;
        String specialization;
        String photoPath;
        boolean isOnline;
        // Added DoctorSchedule to DoctorData
        private DoctorSchedule schedule;

        DoctorData(int id, String name, String specialization, String photoPath) {
            this.id = id;
            this.name = name;
            this.specialization = specialization;
            this.photoPath = photoPath;
            this.isOnline = new Random().nextBoolean();
            this.schedule = new DoctorSchedule(name); // Initialize schedule for each doctor
        }
          public String getName() {
            return name;
        }

        public DoctorSchedule getSchedule() {
            return schedule;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Name: " + name + ", Specialization: " + specialization + ", Photo: " + (photoPath != null ? photoPath : "N/A");
        }
    }

    // Merged DoctorSchedule class as an inner class
    class DoctorSchedule {
        private String doctorName; // Redundant if used within DoctorData, but kept for clarity
        private Map<LocalDate, Set<LocalTime>> availableSlots; // TreeMap for sorted dates, HashSet for unique times
        private List<Appointment> appointments; // To keep a history of this doctor's appointments

        public DoctorSchedule(String doctorName) {
            this.doctorName = doctorName;
            this.availableSlots = new TreeMap<>();
            this.appointments = new ArrayList<>();
        }

        public String getDoctorName() {
            return doctorName;
        }

        // Algorithm: Add a new available slot
        public void addAvailability(LocalDate date, LocalTime startTime, LocalTime endTime, int intervalMinutes) {
            Set<LocalTime> slots = availableSlots.computeIfAbsent(date, k -> new HashSet<>());
            LocalTime currentTime = startTime;
            while (currentTime.isBefore(endTime)) { // Changed to strictly before for inclusive end time logic
                slots.add(currentTime);
                currentTime = currentTime.plusMinutes(intervalMinutes);
            }
            // Add the end time if it's a valid slot boundary and not already added
            if (currentTime.equals(endTime) || (currentTime.isAfter(endTime) && currentTime.minusMinutes(intervalMinutes).isBefore(endTime))) {
                 slots.add(endTime.minusMinutes(intervalMinutes)); // Adjust if interval doesn't perfectly align
                 // The previous loop handles the exact end time if it aligns with the interval
            }
        }


        // Algorithm: Check if a slot is available
        public boolean isSlotAvailable(LocalDate date, LocalTime time) {
            Set<LocalTime> slotsOnDate = availableSlots.get(date);
            return slotsOnDate != null && slotsOnDate.contains(time);
        }

        // Algorithm: Book a slot (from your new code)
        public void bookSlot(LocalDate date, LocalTime time, Appointment appointment) {
            Set<LocalTime> slotsOnDate = availableSlots.get(date);
            if (slotsOnDate != null) {
                slotsOnDate.remove(time); // Remove the slot once it's booked
            }
            this.appointments.add(appointment);
        }

        public Map<LocalDate, Set<LocalTime>> getAvailableSlots() {
            return availableSlots;
        }

        public List<Appointment> getAppointments() {
            return appointments;
        }

        @Override
        public String toString() {
            return "Doctor Schedule for: " + doctorName; // Changed for clarity
        }
    }

    // Dummy Appointment class (You'll need to define this properly elsewhere)
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


    private void refreshDoctorData() {
        doctorMap.clear();
        specializationMap.clear();
        doctorByNameMap.clear();

        String query = "SELECT id, name, specialization, photo_path FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                String photoPath = resultSet.getString("photo_path");
                DoctorData doctor = new DoctorData(id, name, specialization, photoPath);

                doctorMap.put(id, doctor);
                specializationMap.computeIfAbsent(specialization, k -> new ArrayList<>()).add(doctor);
                doctorByNameMap.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(doctor);
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing doctor data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: Could not load doctor data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDoctorCard(DoctorData doctor, JPanel contentPanel){
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        Dimension cardSize = new Dimension(220, 250);
        cardPanel.setPreferredSize(cardSize);
        cardPanel.setMinimumSize(cardSize);
        cardPanel.setMaximumSize(cardSize);
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        imagePanel.setBackground(Color.WHITE);
        JLabel photoLabel = new JLabel();
        ImageIcon doctorPhoto = defaultDoctorIcon;
        if (doctor.photoPath != null && new File(doctor.photoPath).exists()) {
            try {
                ImageIcon originalIcon = new ImageIcon(doctor.photoPath);
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                doctorPhoto = new ImageIcon(scaledImage);
            } catch (Exception ex) {
                System.err.println("Error loading image for Doctor ID " + doctor.id + ": " + ex.getMessage());
            }
        }
        photoLabel.setIcon(doctorPhoto);
        imagePanel.add(photoLabel);
        cardPanel.add(imagePanel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 15));

        JLabel nameLabel = new JLabel(doctor.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(nameLabel);

        JLabel specLabel = new JLabel(doctor.specialization);
        specLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        specLabel.setForeground(Color.GRAY);
        specLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(specLabel);

        cardPanel.add(textPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (doctor.isOnline) {
            statusLabel.setForeground(new Color(0, 180, 0));
            statusLabel.setText("Online");
            if (onlineIcon != null) {
                statusLabel.setIcon(onlineIcon);
                statusLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
                statusLabel.setIconTextGap(5);
            }
        } else {
            statusLabel.setForeground(Color.GRAY);
            statusLabel.setText("Offline");
        }
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton infoButton = new JButton();
        infoButton.setOpaque(false);
        infoButton.setContentAreaFilled(false);
        infoButton.setBorderPainted(false);
        infoButton.setFocusPainted(false);
        infoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (infoIcon != null) {
            infoButton.setIcon(infoIcon);
        } else {
            infoButton.setText("...");
            infoButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
            infoButton.setForeground(new Color(60, 140, 255));
        }

        infoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(contentPanel,
                "Doctor Details:\n" +
                "ID: " + doctor.id + "\n" +
                "Name: " + doctor.name + "\n" +
                "Specialization: " + doctor.specialization + "\n" +
                "Photo Path: " + (doctor.photoPath != null ? doctor.photoPath : "N/A") + "\n" +
                "Status: " + (doctor.isOnline ? "Online" : "Offline"),
                "Doctor Info: " + doctor.name,
                JOptionPane.INFORMATION_MESSAGE);
        });
        bottomPanel.add(infoButton, BorderLayout.EAST);

        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalBorderColor = Color.LIGHT_GRAY;
            private Color originalBackgroundColor = Color.WHITE;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2, true));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!Boolean.TRUE.equals(cardPanel.getClientProperty("isSelected"))) {
                    cardPanel.setBorder(BorderFactory.createLineBorder(originalBorderColor, 1, true));
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                for (Component comp : contentPanel.getComponents()) {
                    if (comp instanceof JPanel && comp != cardPanel) {
                        JPanel otherCard = (JPanel) comp;
                        otherCard.setBackground(originalBackgroundColor);
                        otherCard.setBorder(BorderFactory.createLineBorder(originalBorderColor, 1, true));
                        otherCard.putClientProperty("isSelected", false);
                    }
                }
                cardPanel.setBackground(new Color(230, 235, 255));
                cardPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 140, 255), 2, true));
                cardPanel.putClientProperty("isSelected", true);
            }
        });
        cardPanel.putClientProperty("isSelected", false);

        return cardPanel;
    }

    public void viewDoctorsGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Doctor Directory");
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setBackground(new Color(245, 250, 255));
            mainPanel.setLayout(new BorderLayout(15, 15));
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel title = new JLabel("Doctors Directory");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setBorder(new EmptyBorder(0, 0, 15, 0));
            mainPanel.add(title, BorderLayout.NORTH);

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            controlPanel.setBackground(new Color(245, 250, 255));
            JLabel filterLabel = new JLabel("Filter by Specialization:");
            filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            controlPanel.add(filterLabel);

            Set<String> uniqueSpecializations = new TreeSet<>(specializationMap.keySet());
            uniqueSpecializations.add("All");
            String[] specializations = uniqueSpecializations.toArray(new String[0]);
            JComboBox<String> specDropdown = new JComboBox<>(specializations);
            specDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            specDropdown.setPreferredSize(new Dimension(200, 30));
            controlPanel.add(specDropdown);

            mainPanel.add(controlPanel, BorderLayout.SOUTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridLayout(0, 4, 20, 20));
            contentPanel.setBackground(new Color(245, 250, 255));

            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            mainPanel.add(scrollPane, BorderLayout.CENTER);

            Runnable loadDoctorsIntoCards = () -> {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();

                String selectedSpec = Objects.requireNonNull(specDropdown.getSelectedItem()).toString();
                List<DoctorData> doctorsToDisplay = new ArrayList<>();

                if (selectedSpec.equals("All")) {
                    doctorsToDisplay.addAll(doctorMap.values());
                } else {
                    List<DoctorData> doctorsBySpec = specializationMap.get(selectedSpec);
                    if (doctorsBySpec != null) {
                        doctorsBySpec.sort(Comparator.comparing(d -> d.name));
                        doctorsToDisplay.addAll(doctorsBySpec);
                    }
                }

                if (doctorsToDisplay.isEmpty()) {
                    JLabel noDoctorsLabel = new JLabel("No doctors found for this filter.");
                    noDoctorsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                    noDoctorsLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JPanel centerWrapper = new JPanel(new GridBagLayout());
                    centerWrapper.setOpaque(false);
                    centerWrapper.add(noDoctorsLabel);
                    contentPanel.setLayout(new BorderLayout());
                    contentPanel.add(centerWrapper, BorderLayout.CENTER);
                } else {
                    contentPanel.setLayout(new GridLayout(0, 4, 20, 20));
                    for (DoctorData doc : doctorsToDisplay) {
                        JPanel doctorCard = createDoctorCard(doc, contentPanel);
                        contentPanel.add(doctorCard);
                    }
                }

                contentPanel.revalidate();
                contentPanel.repaint();
            };

            refreshDoctorData();
            loadDoctorsIntoCards.run();

            specDropdown.addActionListener(e -> loadDoctorsIntoCards.run());

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    public boolean getDoctorById(int id) {
        if (doctorMap.containsKey(id)) {
            return true;
        }

        String query = "SELECT id FROM doctors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            return exists;
        } catch (SQLException e) {
            System.err.println("Error checking doctor by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void viewDoctors() {
        refreshDoctorData();
        System.out.println("Doctors: ");
        System.out.println("+------------+--------------------+------------------+------------------------------+");
        System.out.println("| Doctor Id  | Name                | Specialization   | Photo Path                   |");
        System.out.println("+------------+--------------------+------------------+------------------------------+");

        for (DoctorData doc : doctorMap.values()) {
            System.out.printf("| %-10d | %-18s | %-16s | %-28s |\n", doc.id, doc.name, doc.specialization, doc.photoPath != null ? doc.photoPath : "N/A");
            System.out.println("+------------+--------------------+------------------+------------------------------+");
        }
        if (doctorMap.isEmpty()) {
            System.out.println("| No doctors found in the system.                                                      |");
            System.out.println("+--------------------------------------------------------------------------------------------+");
        }
    }

    public void searchDoctorGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Search Doctor");
            frame.setSize(850, 550);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(null);
            panel.setBackground(new java.awt.Color(245, 250, 255));

            JLabel title = new JLabel("Search Doctor");
            title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
            title.setBounds(330, 20, 250, 30);
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

            String[] columns = {"Doctor ID", "Name", "Specialization", "Photo"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 3) {
                        return ImageIcon.class;
                    }
                    return Object.class;
                }
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable table = new JTable(model);
            table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));
            table.setRowHeight(80);
            table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));

            table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());
            table.getColumnModel().getColumn(3).setPreferredWidth(100);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(40, 130, 750, 330);
            panel.add(scrollPane);

            searchBtn.addActionListener(e -> {
                model.setRowCount(0);
                String criteria = Objects.requireNonNull(criteriaBox.getSelectedItem()).toString();
                String keyword = searchField.getText().trim();
                List<DoctorData> results = new ArrayList<>();

                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a value to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                switch (criteria) {
                    case "ID":
                        try {
                            int id = Integer.parseInt(keyword);
                            DoctorData doc = doctorMap.get(id);
                            if (doc != null) {
                                results.add(doc);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Please enter a valid numeric ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        break;
                    case "Name":
                        String lowerCaseKeyword = keyword.toLowerCase();
                        Set<DoctorData> tempNameResults = new HashSet<>();
                        for (Map.Entry<String, List<DoctorData>> entry : doctorByNameMap.entrySet()) {
                            if (entry.getKey().contains(lowerCaseKeyword)) {
                                tempNameResults.addAll(entry.getValue());
                            }
                        }
                        results.addAll(tempNameResults);
                        results.sort(Comparator.comparing(d -> d.name));
                        break;
                    case "Specialization":
                        String lowerCaseSpecKeyword = keyword.toLowerCase();
                        List<String> matchingSpecializations = new ArrayList<>();
                        for(String spec : specializationMap.keySet()){
                            if(spec.toLowerCase().contains(lowerCaseSpecKeyword)){
                                matchingSpecializations.add(spec);
                            }
                        }
                        Set<DoctorData> tempSpecResults = new HashSet<>();
                        for(String matchedSpec : matchingSpecializations){
                            List<DoctorData> doctors = specializationMap.get(matchedSpec);
                            if(doctors != null){
                                tempSpecResults.addAll(doctors);
                            }
                        }
                        results.addAll(tempSpecResults);
                        results.sort(Comparator.comparing(d -> d.name));
                        break;
                }

                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No matching doctor found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (DoctorData doc : results) {
                        ImageIcon doctorPhoto = null;
                        if (doc.photoPath != null && new File(doc.photoPath).exists()) {
                            try {
                                ImageIcon originalIcon = new ImageIcon(doc.photoPath);
                                Image image = originalIcon.getImage();
                                Image scaledImage = image.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                                doctorPhoto = new ImageIcon(scaledImage);
                            } catch (Exception ex) {
                                System.err.println("Error loading image for Doctor ID " + doc.id + ": " + ex.getMessage());
                                doctorPhoto = null;
                            }
                        }
                        model.addRow(new Object[]{doc.id, doc.name, doc.specialization, doctorPhoto});
                    }
                }
            });

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        JLabel lbl = new JLabel();

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                lbl.setIcon((ImageIcon) value);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                lbl.setIcon(null);
                lbl.setText("No Image");
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
            }
            return lbl;
        }
    }

    public void addDoctorGUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Add New Doctor");
            frame.setSize(460, 430);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(new java.awt.Color(245, 250, 255));

            JLabel titleLabel = new JLabel("Add Doctor");
            titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
            titleLabel.setBounds(150, 20, 200, 30);
            panel.add(titleLabel);

            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            nameLabel.setBounds(50, 80, 100, 25);
            panel.add(nameLabel);

            JTextField nameField = new JTextField();
            nameField.setBounds(160, 80, 220, 25);
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
            specDropdown.setBounds(160, 130, 220, 25);
            panel.add(specDropdown);

            JLabel photoLabel = new JLabel("Photo:");
            photoLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            photoLabel.setBounds(50, 180, 100, 25);
            panel.add(photoLabel);

            JButton uploadBtn = new JButton("Choose Photo");
            uploadBtn.setBounds(160, 180, 220, 25);
            panel.add(uploadBtn);

            final String[] photoPath = {null};

            uploadBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Doctor Photo");
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    photoPath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                    JOptionPane.showMessageDialog(frame, "Photo selected:\n" + photoPath[0]);
                }
            });

            JButton addButton = new JButton("Add Doctor");
            addButton.setBounds(90, 260, 120, 35);
            addButton.setBackground(new java.awt.Color(0, 123, 255));
            addButton.setForeground(java.awt.Color.WHITE);
            addButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            panel.add(addButton);

            JButton resetButton = new JButton("Clear");
            resetButton.setBounds(230, 260, 120, 35);
            resetButton.setBackground(new java.awt.Color(220, 53, 69));
            resetButton.setForeground(java.awt.Color.WHITE);
            resetButton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            panel.add(resetButton);

            addButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String specialization = Objects.requireNonNull(specDropdown.getSelectedItem()).toString();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter doctor's name.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else if (photoPath[0] == null) {
                    JOptionPane.showMessageDialog(frame, "Please choose a photo.", "Input Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        String query = "INSERT INTO doctors(name, specialization, photo_path) VALUES(?, ?, ?)";
                        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, name);
                        ps.setString(2, specialization);
                        ps.setString(3, photoPath[0]);

                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    int newId = generatedKeys.getInt(1);
                                    DoctorData newDoctor = new DoctorData(newId, name, specialization, photoPath[0]);
                                    doctorMap.put(newId, newDoctor);
                                    specializationMap.computeIfAbsent(specialization, k -> new ArrayList<>()).add(newDoctor);
                                    doctorByNameMap.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(newDoctor);
                                }
                            }

                            JDialog successDialog = new JDialog(frame, "Success", true);
                            successDialog.setSize(320, 90);
                            successDialog.setLayout(new java.awt.BorderLayout());
                            successDialog.setUndecorated(true);
                            successDialog.setLocationRelativeTo(frame);

                            JLabel message = new JLabel("Doctor added successfully!", SwingConstants.CENTER);
                            message.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
                            successDialog.add(message, java.awt.BorderLayout.CENTER);

                            Timer timer = new Timer(2000, event -> {
                                successDialog.dispose();
                                frame.dispose();
                            });
                            timer.setRepeats(false);
                            timer.start();

                            successDialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Failed to add doctor to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        System.err.println("SQL Error adding doctor: " + ex.getMessage());
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            resetButton.addActionListener(e -> {
                nameField.setText("");
                specDropdown.setSelectedIndex(0);
                photoPath[0] = null;
            });

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}