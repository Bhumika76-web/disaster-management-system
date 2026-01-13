package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import models.User;
import models.Disaster;
import dao.UserDAO;
import dao.DisasterDAO;

public class AdminDashboard extends JPanel {
    private User admin;
    private MainFrame mainFrame;
    private DisasterDAO disasterDAO = new DisasterDAO();
    private UserDAO userDAO = new UserDAO();
    private JList<String> disasterList;
    private JTextArea disasterDetailsArea;
    private DefaultListModel<String> listModel;

    public AdminDashboard(User admin, MainFrame mainFrame) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
        refreshDisasterList();
    }

    private void initUI() {

        JLabel headerLabel = new JLabel("🛡️ ADMIN DASHBOARD - DISASTER MANAGEMENT");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(100, 200, 255));
        headerLabel.setBounds(20, 20, 800, 40);

        JPanel userPanel = createRoundedPanel(new Color(40, 60, 100), 350, 80, 20, 70);
        JLabel userLabel = new JLabel("Welcome, " + admin.getUsername() + " (Admin)");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(30, 15, 300, 25);

        JLabel locationLabel = new JLabel("Location: " + admin.getLocation());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        locationLabel.setForeground(new Color(150, 200, 255));
        locationLabel.setBounds(30, 45, 300, 20);

        userPanel.add(userLabel);
        userPanel.add(locationLabel);

        JButton addDisasterBtn = createStyledButton("+ ADD DISASTER", new Color(255, 100, 100));
        addDisasterBtn.setBounds(20, 170, 180, 45);
        addDisasterBtn.addActionListener(e -> showAddDisasterDialog());

        JButton helpReqBtn = createStyledButton("🆘 HELP REQUESTS", new Color(255, 150, 100));
        helpReqBtn.setBounds(210, 170, 180, 45);
        helpReqBtn.addActionListener(e -> mainFrame.showAdminHelpManagement(admin));

        JButton viewUsersBtn = createStyledButton("👥 VIEW ALL USERS", new Color(100, 200, 255));
        viewUsersBtn.setBounds(400, 170, 180, 45);
        viewUsersBtn.addActionListener(e -> showAllUsers());

        JButton alertsBtn = createStyledButton("📢 ALERTS", new Color(100, 200, 255));
        alertsBtn.setBounds(590, 170, 130, 45);
        alertsBtn.addActionListener(e -> mainFrame.showAlertsPage(admin));

        JLabel disasterLabel = new JLabel("🔥 Active Disasters:");
        disasterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        disasterLabel.setForeground(new Color(255, 150, 100));
        disasterLabel.setBounds(20, 235, 300, 30);

        listModel = new DefaultListModel<>();
        disasterList = new JList<>(listModel);
        disasterList.setBackground(new Color(40, 50, 80));
        disasterList.setForeground(new Color(100, 200, 255));
        disasterList.setFont(new Font("Arial", Font.PLAIN, 12));
        disasterList.addListSelectionListener(e -> showDisasterDetails());

        JScrollPane scrollPane = new JScrollPane(disasterList);
        scrollPane.setBounds(20, 270, 400, 320);
        scrollPane.setBackground(new Color(30, 40, 70));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 2));

        JLabel detailsLabel = new JLabel("Details:");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsLabel.setForeground(Color.WHITE);
        detailsLabel.setBounds(440, 235, 300, 30);

        disasterDetailsArea = new JTextArea();
        disasterDetailsArea.setBackground(new Color(40, 50, 80));
        disasterDetailsArea.setForeground(new Color(150, 200, 255));
        disasterDetailsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        disasterDetailsArea.setEditable(false);
        disasterDetailsArea.setLineWrap(true);
        disasterDetailsArea.setWrapStyleWord(true);

        JScrollPane detailsPane = new JScrollPane(disasterDetailsArea);
        detailsPane.setBounds(440, 270, 400, 320);
        detailsPane.setBackground(new Color(30, 40, 70));
        detailsPane.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 2));

        JButton refreshBtn = createStyledButton("🔄 REFRESH", new Color(150, 255, 150));
        refreshBtn.setBounds(20, 610, 130, 40);
        refreshBtn.addActionListener(e -> refreshDisasterList());

        JButton rescueBtn = createStyledButton("🚁 RESCUE OPS", new Color(100, 200, 255));
        rescueBtn.setBounds(160, 610, 140, 40);
        rescueBtn.addActionListener(e -> mainFrame.showRescueOperations(admin));

        JButton riskBtn = createStyledButton("📊 RISK", new Color(255, 150, 100));
        riskBtn.setBounds(310, 610, 100, 40);
        riskBtn.addActionListener(e -> mainFrame.showRiskAssessment(admin));

        JButton logoutBtn = createStyledButton("LOGOUT", new Color(255, 100, 100));
        logoutBtn.setBounds(720, 610, 120, 40);
        logoutBtn.addActionListener(e -> mainFrame.logout());

        add(headerLabel);
        add(userPanel);
        add(addDisasterBtn);
        add(helpReqBtn);
        add(viewUsersBtn);
        add(alertsBtn);
        add(disasterLabel);
        add(scrollPane);
        add(detailsLabel);
        add(detailsPane);
        add(refreshBtn);
        add(rescueBtn);
        add(riskBtn);
        add(logoutBtn);
    }

    private void showAddDisasterDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBackground(new Color(40, 50, 80));

        JTextField typeField = new JTextField("Flood");
        JTextField locationField = new JTextField("City Name");
        JSpinner severitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        JTextArea descriptionArea = new JTextArea(3, 20);

        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Severity (1-10):"));
        panel.add(severitySpinner);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Disaster",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Disaster disaster = new Disaster(
                    typeField.getText(),
                    locationField.getText(),
                    (int) severitySpinner.getValue(),
                    descriptionArea.getText(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
            );

            if (disasterDAO.addDisaster(disaster)) {
                JOptionPane.showMessageDialog(this, "✅ Disaster Alert Added!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDisasterList();
            }
        }
    }

    private void showAllUsers() {
        List<User> users = userDAO.getAllUsers();
        StringBuilder sb = new StringBuilder("REGISTERED USERS:\n\n");

        for (User user : users) {
            sb.append("Username: ").append(user.getUsername()).append("\n");
            sb.append("Email: ").append(user.getEmail()).append("\n");
            sb.append("Type: ").append(user.getUserType()).append("\n");
            sb.append("Location: ").append(user.getLocation()).append("\n");
            sb.append("Phone: ").append(user.getPhone()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setBackground(new Color(40, 50, 80));
        textArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDisasterDetails() {
        int index = disasterList.getSelectedIndex();
        if (index >= 0) {
            List<Disaster> disasters = disasterDAO.getAllDisasters();
            if (index < disasters.size()) {
                Disaster d = disasters.get(index);
                StringBuilder details = new StringBuilder();
                details.append("Type: ").append(d.getType()).append("\n");
                details.append("Location: ").append(d.getLocation()).append("\n");
                details.append("Severity: ").append(d.getSeverity()).append("/10").append("\n");
                details.append("Status: ").append(d.getStatus()).append("\n");
                details.append("Time: ").append(d.getTimestamp()).append("\n");
                details.append("Description:\n").append(d.getDescription());
                disasterDetailsArea.setText(details.toString());
            }
        }
    }

    private void refreshDisasterList() {
        listModel.clear();
        List<Disaster> disasters = disasterDAO.getAllDisasters();

        for (Disaster d : disasters) {
            String severity = getSeverityColor(d.getSeverity());
            listModel.addElement(d.getType() + " - " + d.getLocation() +
                    " [" + severity + "] (" + d.getStatus() + ")");
        }
    }

    private String getSeverityColor(int severity) {
        if (severity >= 8) return "🔴 CRITICAL";
        if (severity >= 5) return "🟠 HIGH";
        return "🟡 MODERATE";
    }

    private JPanel createRoundedPanel(Color color, int width, int height, int x, int y) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);
        panel.setBounds(x, y, width, height);
        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}