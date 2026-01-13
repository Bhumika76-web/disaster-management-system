package ui;

import javax.swing.*;
import models.User;
import ui.AlertsPage;
import java.awt.GridLayout;
import database.DatabaseConnection;

public class MainFrame extends JFrame {
    private LoginRegisterPanel loginRegisterPanel;
    private AdminDashboard adminDashboard;
    private CitizenDashboard citizenDashboard;
    private AlertsPage alertsPage;
    private JPanel currentPanel;
    private ResponderDashboard responderDashboard;
    private RescueOperationsPage rescueOperationsPage;
    private RiskAssessmentPage riskAssessmentPage;
    private AdminHelpRequestManagement adminHelpRequestManagement;


    public MainFrame() {
        setTitle("Disaster Management & Alert System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setIconImage(new ImageIcon("src/resources/icon.png").getImage());

        DatabaseConnection.createTablesIfNotExist();

        loginRegisterPanel = new LoginRegisterPanel(this);
        currentPanel = loginRegisterPanel;
        add(loginRegisterPanel);

        setVisible(true);
    }

    public void showDashboard(User user) {

        if(currentPanel != null){
            remove(currentPanel);
        }

        if ("admin".equals(user.getUserType())) {
            adminDashboard = new AdminDashboard(user, this);
            currentPanel = adminDashboard;
            add(adminDashboard);
        }else if("responder".equals(user.getUserType())){
            responderDashboard = new ResponderDashboard(user, this);
            currentPanel = responderDashboard;
            add(responderDashboard);
        }else {
            citizenDashboard = new CitizenDashboard(user, this);
            currentPanel = citizenDashboard;
            add(citizenDashboard);
        }

        revalidate();
        repaint();
    }

    public void showAlertsPage(User user) {

        if(currentPanel != null){
            remove(currentPanel);
        }

        alertsPage = new AlertsPage(user, this);
        currentPanel = alertsPage;
        add(alertsPage);

        revalidate();
        repaint();
    }

    public void showRescueOperations(User user) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        rescueOperationsPage = new RescueOperationsPage(user, this);
        currentPanel = rescueOperationsPage;
        add(rescueOperationsPage);
        revalidate();
        repaint();
    }

    public void showRiskAssessment(User user) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        riskAssessmentPage = new RiskAssessmentPage(user, this);
        currentPanel = riskAssessmentPage;
        add(riskAssessmentPage);
        revalidate();
        repaint();
    }

    public void showAdminHelpManagement(User user) {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        adminHelpRequestManagement = new AdminHelpRequestManagement(user, this);
        currentPanel = adminHelpRequestManagement;
        add(adminHelpRequestManagement);

        revalidate();
        repaint();
    }

    public void logout() {

        if(currentPanel != null){
            remove(currentPanel);
        }

        loginRegisterPanel = new LoginRegisterPanel(this);
        currentPanel = loginRegisterPanel;
        add(loginRegisterPanel);

        revalidate();
        repaint();
        JOptionPane.showMessageDialog(this, "✓ Logged out successfully!",
                "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}