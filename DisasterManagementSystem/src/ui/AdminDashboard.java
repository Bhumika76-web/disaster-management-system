package ui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import models.User;
import models.Disaster;
import dao.UserDAO;
import dao.DisasterDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;
import config.AppConstants;

public class AdminDashboard extends JPanel {

    private static final String CLASS_NAME = "AdminDashboard";
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 710;

    private User admin;
    private MainFrame mainFrame;
    private DisasterDAO disasterDAO;
    private UserDAO userDAO;
    private JList<String> disasterList;
    private JTextArea disasterDetailsArea;
    private DefaultListModel<String> listModel;

    public AdminDashboard(User admin, MainFrame mainFrame) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        this.disasterDAO = new DisasterDAO();
        this.userDAO = new UserDAO();

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing AdminDashboard for: " + admin.getUsername());
        initUI();
        refreshDisasterList();
    }


    private void initUI() {

        JLabel headerLabel = ComponentFactory.createHeaderLabel(
                "🛡️ ADMIN DASHBOARD - DISASTER MANAGEMENT", AppTheme.COLOR_BLUE);
        headerLabel.setFont(AppTheme.FONT_TITLE);
        headerLabel.setForeground(AppTheme.COLOR_BLUE);
        headerLabel.setBounds(20, 20, 800, 40);


        JPanel userPanel = createUserInfoPanel();
        userPanel.setBounds(20, 70, 350, 80);


        JButton addDisasterBtn = ComponentFactory.createDangerButton(
                "➕ ADD DISASTER", this::showAddDisasterDialog);
        addDisasterBtn.setBounds(20, 170, 180, 45);

        JButton helpReqBtn = ComponentFactory.createWarningButton(
                "🆘 HELP REQUESTS", () -> mainFrame.showAdminHelpManagement(admin));
        helpReqBtn.setBounds(210, 170, 180, 45);

        JButton viewUsersBtn = ComponentFactory.createPrimaryButton(
                "👥 VIEW ALL USERS", this::showAllUsers);
        viewUsersBtn.setBounds(400, 170, 180, 45);

        JButton alertsBtn = ComponentFactory.createPrimaryButton(
                "🔔 ALERTS", () -> mainFrame.showAlertsPage(admin));
        alertsBtn.setBounds(590, 170, 130, 45);


        JLabel disasterLabel = ComponentFactory.createHeaderLabel(
                "🚨 Active Disasters:", AppTheme.COLOR_ORANGE);
        disasterLabel.setBounds(20, 235, 300, 30);

        listModel = new DefaultListModel<>();
        disasterList = new JList<>(listModel);
        UIUtils.styleList(disasterList);
        disasterList.addListSelectionListener(e -> showDisasterDetails());

        JScrollPane scrollPane = UIUtils.createThemedScrollPane(disasterList);
        scrollPane.setBounds(20, 270, 400, 320);


        JLabel detailsLabel = ComponentFactory.createHeaderLabel(
                "📋 Details:", AppTheme.TEXT_WHITE);
        detailsLabel.setBounds(440, 235, 300, 30);

        disasterDetailsArea = new JTextArea();
        UIUtils.styleTextArea(disasterDetailsArea);
        disasterDetailsArea.setEditable(false);

        JScrollPane detailsPane = UIUtils.createThemedScrollPane(disasterDetailsArea);
        detailsPane.setBounds(440, 270, 400, 320);


        JButton refreshBtn = ComponentFactory.createSuccessButton(
                "🔄 REFRESH", this::refreshDisasterList);
        refreshBtn.setBounds(20, 610, 130, 40);

        JButton rescueBtn = ComponentFactory.createPrimaryButton(
                "🚑 RESCUE OPS", () -> mainFrame.showRescueOperations(admin));
        rescueBtn.setBounds(160, 610, 140, 40);

        JButton riskBtn = ComponentFactory.createWarningButton(
                "📊 RISK", () -> mainFrame.showRiskAssessment(admin));
        riskBtn.setBounds(310, 610, 100, 40);

        JButton logoutBtn = ComponentFactory.createDangerButton(
                "🚪 LOGOUT", () -> mainFrame.logout());
        logoutBtn.setBounds(720, 610, 120, 40);


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


    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome, " + admin.getUsername() + " (Admin)");
        userLabel.setFont(AppTheme.FONT_LABEL);
        userLabel.setForeground(AppTheme.TEXT_WHITE);
        userLabel.setBounds(30, 15, 300, 25);

        JLabel locationLabel = new JLabel("Location: " + admin.getLocation());
        locationLabel.setFont(AppTheme.FONT_REGULAR);
        locationLabel.setForeground(AppTheme.TEXT_SECONDARY);
        locationLabel.setBounds(30, 45, 300, 20);

        panel.add(userLabel);
        panel.add(locationLabel);

        return panel;
    }


    private void showAddDisasterDialog() {
        Logger.debug(CLASS_NAME, "Opening add disaster dialog");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBackground(AppTheme.BG_MEDIUM);

        JTextField typeField = new JTextField("Flood");
        UIUtils.styleTextField(typeField);

        JTextField locationField = new JTextField("City Name");
        UIUtils.styleTextField(locationField);

        JSpinner severitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        JTextArea descriptionArea = new JTextArea(3, 20);
        UIUtils.styleTextArea(descriptionArea);

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
                Logger.info(CLASS_NAME, "Disaster added: " + disaster.getType());
                UIUtils.showInfo(this, "Success",
                        "✅ Disaster Alert Added!");
                refreshDisasterList();
            } else {
                Logger.warn(CLASS_NAME, "Failed to add disaster");
                UIUtils.showError(this, "Error",
                        "Failed to add disaster alert");
            }
        }
    }


    private void showAllUsers() {
        Logger.debug(CLASS_NAME, "Fetching all users");
        List<User> users = userDAO.getAllUsers();

        StringBuilder sb = new StringBuilder("REGISTERED USERS:\n");
        sb.append("Total: ").append(users.size()).append("\n\n");

        for (User user : users) {
            sb.append("Username: ").append(user.getUsername()).append("\n");
            sb.append("Email: ").append(user.getEmail()).append("\n");
            sb.append("Type: ").append(user.getUserType()).append("\n");
            sb.append("Location: ").append(user.getLocation()).append("\n");
            sb.append("Phone: ").append(user.getPhone()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        UIUtils.styleTextArea(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        UIUtils.showInfo(this, "All Users", "User list displayed in dialog");
        JOptionPane.showMessageDialog(this, scrollPane, "All Users",
                JOptionPane.INFORMATION_MESSAGE);
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
                details.append("\nDescription:\n").append(d.getDescription());
                disasterDetailsArea.setText(details.toString());

                Logger.debug(CLASS_NAME, "Showing details for disaster: " + d.getType());
            }
        }
    }


    private void refreshDisasterList() {
        Logger.debug(CLASS_NAME, "Refreshing disaster list");
        listModel.clear();
        List<Disaster> disasters = disasterDAO.getAllDisasters();

        for (Disaster d : disasters) {
            String severity = getSeverityEmoji(d.getSeverity());
            String displayText = String.format("%s - %s [%s] (%s)",
                    d.getType(), d.getLocation(), severity, d.getStatus());
            listModel.addElement(displayText);
        }

        Logger.info(CLASS_NAME, "Disaster list refreshed: " + disasters.size() + " disasters");
    }


    private String getSeverityEmoji(int severity) {
        if (severity >= 8) return "🔴 CRITICAL";
        if (severity >= 5) return "🟠 HIGH";
        return "🟡 MODERATE";
    }
}