package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import models.User;
import models.Disaster;
import models.HelpRequest;
import dao.DisasterDAO;
import dao.HelpRequestDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;
import config.AppConstants;


public class AlertsPage extends JPanel {

    private static final String CLASS_NAME = "AlertsPage";

    private User user;
    private MainFrame mainFrame;
    private DisasterDAO disasterDAO;
    private HelpRequestDAO helpRequestDAO;
    private JPanel activeAlertsPanel;
    private JPanel helpRequestsPanel;
    private boolean isAdmin;
    private boolean isResponder;

    public AlertsPage(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.disasterDAO = new DisasterDAO();
        this.helpRequestDAO = new HelpRequestDAO();
        this.isAdmin = AppConstants.USER_TYPE_ADMIN.equals(user.getUserType());
        this.isResponder = AppConstants.USER_TYPE_RESPONDER.equals(user.getUserType());

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing AlertsPage for: " + user.getUsername());
        initUI();
        refreshAlerts();
    }


    private void initUI() {
        int yPos = 15;

        JButton backBtn = ComponentFactory.createPrimaryButton("↶", () -> mainFrame.showDashboard(user));
        backBtn.setBounds(20, yPos, 35, 35);

        JLabel headerLabel = ComponentFactory.createTitleLabel(
                "🔔 Alerts", AppTheme.TEXT_WHITE);
        headerLabel.setBounds(70, yPos, 300, 35);
        yPos += 50;


        if (isAdmin) {
            JLabel broadcastTitle = ComponentFactory.createHeaderLabel(
                    "Broadcast Alert", AppTheme.COLOR_BLUE);
            broadcastTitle.setBounds(20, yPos, 200, 25);
            yPos += 30;

            JTextField titleField = new JTextField();
            UIUtils.styleTextField(titleField);
            UIUtils.addFieldPlaceholder(titleField, "Enter alert title");
            titleField.setBounds(20, yPos, 860, 40);
            yPos += 50;

            JTextArea descArea = new JTextArea();
            UIUtils.styleTextArea(descArea);
            descArea.setBounds(20, yPos, 860, 80);
            yPos += 90;

            JComboBox<String> zoneCombo = new JComboBox<>(new String[]{
                    "Select Zone", "Coastal Area", "Mountain Region", "Urban Area",
                    "Rural Area", "Flood Zone", "Earthquake Zone"
            });
            UIUtils.styleComboBox(zoneCombo);
            zoneCombo.setBounds(20, yPos, 200, 35);

            JButton broadcastBtn = ComponentFactory.createSuccessButton(
                    "Broadcast Alert", () -> handleBroadcast(titleField, descArea, zoneCombo));
            broadcastBtn.setBounds(20, yPos + 50, 200, 45);
            broadcastBtn.setFont(AppTheme.FONT_LABEL);

            add(broadcastTitle);
            add(titleField);
            add(descArea);
            add(zoneCombo);
            add(broadcastBtn);

            yPos += 110;
        }

        JLabel activeTitle = ComponentFactory.createHeaderLabel(
                "Active Alerts", AppTheme.COLOR_BLUE);
        activeTitle.setBounds(20, yPos, 200, 25);
        yPos += 30;

        activeAlertsPanel = new JPanel();
        activeAlertsPanel.setLayout(new BoxLayout(activeAlertsPanel, BoxLayout.Y_AXIS));
        activeAlertsPanel.setBackground(AppTheme.BG_DARK);
        activeAlertsPanel.setOpaque(false);

        JScrollPane activeScroll = UIUtils.createThemedScrollPane(activeAlertsPanel);
        activeScroll.setBounds(20, yPos, 860, 100);
        yPos += 120;


        if (!isAdmin && !isResponder) {
            JLabel helpFormTitle = ComponentFactory.createHeaderLabel(
                    "Request Help", AppTheme.COLOR_ORANGE);
            helpFormTitle.setBounds(20, yPos, 200, 25);
            yPos += 30;

            JTextField descField = new JTextField();
            UIUtils.styleTextField(descField);
            UIUtils.addFieldPlaceholder(descField, "Describe what help you need");
            descField.setBounds(20, yPos, 860, 35);
            yPos += 45;

            JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                    "Select Help Type", "Medical", "Shelter", "Food", "Missing Person", "Other"
            });
            UIUtils.styleComboBox(typeCombo);
            typeCombo.setBounds(20, yPos, 200, 35);

            JTextField phoneField = new JTextField();
            UIUtils.styleTextField(phoneField);
            UIUtils.addFieldPlaceholder(phoneField, "Contact Number");
            phoneField.setBounds(230, yPos, 200, 35);

            JButton submitBtn = ComponentFactory.createWarningButton(
                    "Submit Request", () -> handleHelpRequest(descField, typeCombo, phoneField));
            submitBtn.setBounds(440, yPos, 150, 35);

            add(helpFormTitle);
            add(descField);
            add(typeCombo);
            add(phoneField);
            add(submitBtn);

            yPos += 50;
        }

        String helpTitle = isAdmin ? "Help Requests (All)" :
                isResponder ? "Help Requests (Available to Accept)" :
                        "My Help Requests";

        JLabel helpTitleLabel = ComponentFactory.createHeaderLabel(
                helpTitle, AppTheme.COLOR_BLUE);
        helpTitleLabel.setBounds(20, yPos, 500, 25);
        yPos += 30;

        helpRequestsPanel = new JPanel();
        helpRequestsPanel.setLayout(new BoxLayout(helpRequestsPanel, BoxLayout.Y_AXIS));
        helpRequestsPanel.setBackground(AppTheme.BG_DARK);
        helpRequestsPanel.setOpaque(false);

        JScrollPane helpScroll = UIUtils.createThemedScrollPane(helpRequestsPanel);
        helpScroll.setBounds(20, yPos, 860, 150);

        add(backBtn);
        add(headerLabel);
        add(activeTitle);
        add(activeScroll);
        add(helpTitleLabel);
        add(helpScroll);
    }


    private void refreshAlerts() {
        Logger.debug(CLASS_NAME, "Refreshing alerts");


        activeAlertsPanel.removeAll();
        List<Disaster> activeDisasters = disasterDAO.getActiveDisasters();

        if (activeDisasters.isEmpty()) {
            JLabel noAlertsLabel = new JLabel("✅ No active alerts");
            noAlertsLabel.setFont(AppTheme.FONT_REGULAR);
            noAlertsLabel.setForeground(AppTheme.COLOR_GREEN);
            activeAlertsPanel.add(noAlertsLabel);
        } else {
            for (Disaster d : activeDisasters) {
                JPanel alertCard = createAlertCard(d);
                alertCard.setMaximumSize(new Dimension(840, 65));
                alertCard.setPreferredSize(new Dimension(840, 65));
                activeAlertsPanel.add(alertCard);
                activeAlertsPanel.add(Box.createVerticalStrut(8));
            }
        }
        activeAlertsPanel.revalidate();
        activeAlertsPanel.repaint();


        helpRequestsPanel.removeAll();
        loadHelpRequests();
        helpRequestsPanel.revalidate();
        helpRequestsPanel.repaint();

        Logger.info(CLASS_NAME, "Alerts refreshed");
    }


    private void loadHelpRequests() {
        List<HelpRequest> requests;

        if (isAdmin) {
            requests = helpRequestDAO.getPendingHelpRequests();
        } else if (isResponder) {
            requests = helpRequestDAO.getPendingHelpRequests();
        } else {
            requests = helpRequestDAO.getUserHelpRequests(user.getId());
        }

        if (requests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("✅ No help requests");
            noRequestsLabel.setFont(AppTheme.FONT_REGULAR);
            noRequestsLabel.setForeground(AppTheme.COLOR_GREEN);
            helpRequestsPanel.add(noRequestsLabel);
        } else {
            for (HelpRequest req : requests) {
                JPanel helpCard = createHelpRequestCard(req);
                helpCard.setMaximumSize(new Dimension(840, 85));
                helpCard.setPreferredSize(new Dimension(840, 85));
                helpRequestsPanel.add(helpCard);
                helpRequestsPanel.add(Box.createVerticalStrut(8));
            }
        }
    }


    private JPanel createAlertCard(Disaster d) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.COLOR_BLUE);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel("🔔");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 28));
        iconLabel.setBounds(12, 8, 45, 50);

        JLabel titleLabel = new JLabel(d.getType());
        titleLabel.setFont(AppTheme.FONT_LABEL);
        titleLabel.setForeground(AppTheme.TEXT_WHITE);
        titleLabel.setBounds(65, 8, 200, 20);

        JLabel zoneLabel = new JLabel("📍 " + d.getLocation());
        zoneLabel.setFont(AppTheme.FONT_REGULAR);
        zoneLabel.setForeground(AppTheme.TEXT_SECONDARY);
        zoneLabel.setBounds(65, 30, 300, 18);

        JLabel timeLabel = new JLabel("🕐 " + d.getTimestamp());
        timeLabel.setFont(AppTheme.FONT_SMALL);
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setBounds(65, 48, 250, 15);

        card.add(iconLabel);
        card.add(titleLabel);
        card.add(zoneLabel);
        card.add(timeLabel);

        return card;
    }


    private JPanel createHelpRequestCard(HelpRequest req) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor = isResponder ? AppTheme.COLOR_BLUE :
                        isAdmin ? AppTheme.COLOR_ORANGE :
                                AppTheme.COLOR_GREEN;
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel(getHelpTypeIcon(req.getRequestType()));
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setBounds(12, 12, 55, 55);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(AppTheme.FONT_LABEL);
        nameLabel.setForeground(AppTheme.TEXT_WHITE);
        nameLabel.setBounds(75, 10, 380, 20);

        JLabel descLabel = new JLabel(req.getDescription());
        descLabel.setFont(AppTheme.FONT_REGULAR);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(75, 32, 450, 16);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(AppTheme.FONT_SMALL);
        locLabel.setForeground(AppTheme.TEXT_SECONDARY);
        locLabel.setBounds(75, 52, 500, 15);

        if (isResponder) {
            JButton acceptBtn = ComponentFactory.createSuccessButton("ACCEPT",
                    () -> handleAcceptResponderRequest(req));
            acceptBtn.setBounds(720, 18, 100, 50);
            acceptBtn.setFont(AppTheme.FONT_REGULAR);
            card.add(acceptBtn);
        } else if (isAdmin) {
            JButton respondBtn = ComponentFactory.createPrimaryButton("Respond",
                    () -> handleAdminRespond(req));
            respondBtn.setBounds(720, 18, 100, 50);
            respondBtn.setFont(AppTheme.FONT_REGULAR);
            card.add(respondBtn);
        }

        card.add(iconLabel);
        card.add(nameLabel);
        card.add(descLabel);
        card.add(locLabel);

        return card;
    }


    private String getHelpTypeIcon(String type) {
        return switch (type.toLowerCase()) {
            case "medical" -> "🏥";
            case "shelter" -> "🏠";
            case "food" -> "🍽️";
            case "missing person" -> "👤";
            default -> "🆘";
        };
    }


    private void handleBroadcast(JTextField titleField, JTextArea descArea, JComboBox<String> zoneCombo) {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        String zone = (String) zoneCombo.getSelectedItem();

        if (title.isEmpty() || desc.isEmpty() || zone.equals("Select Zone")) {
            UIUtils.showWarning(this, "Validation Error", "Please fill all fields!");
            return;
        }

        Disaster alert = new Disaster(title, zone, 8, desc,
                java.time.LocalDateTime.now().toString());

        if (disasterDAO.addDisaster(alert)) {
            Logger.info(CLASS_NAME, "Alert broadcasted: " + title);
            UIUtils.showInfo(this, "Success", "✅ Alert Broadcast Successfully!");
            titleField.setText("");
            descArea.setText("");
            zoneCombo.setSelectedIndex(0);
            refreshAlerts();
        }
    }


    private void handleHelpRequest(JTextField descField, JComboBox<String> typeCombo,
                                   JTextField phoneField) {
        String desc = descField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String phone = phoneField.getText().trim();

        if (desc.isEmpty() || type.equals("Select Help Type") || phone.isEmpty()) {
            UIUtils.showWarning(this, "Validation Error", "Please fill all fields!");
            return;
        }

        HelpRequest request = new HelpRequest(user.getId(), user.getUsername(), desc,
                user.getLocation(), type, phone);

        if (helpRequestDAO.submitHelpRequest(request)) {
            Logger.info(CLASS_NAME, "Help request submitted by: " + user.getUsername());
            UIUtils.showInfo(this, "Success",
                    "✅ Help Request Submitted!\nResponders will be notified soon.");
            descField.setText("");
            typeCombo.setSelectedIndex(0);
            phoneField.setText("");
            refreshAlerts();
        }
    }


    private void handleAcceptResponderRequest(HelpRequest req) {
        Logger.debug(CLASS_NAME, "Responder accepting request: " + req.getId());
        if (helpRequestDAO.updateRequestStatus(req.getId(), "in_progress")) {
            UIUtils.showInfo(this, "Success",
                    "✅ Help Request Accepted!\nResponding to: " + req.getUsername());
            refreshAlerts();
        }
    }


    private void handleAdminRespond(HelpRequest req) {
        Logger.debug(CLASS_NAME, "Admin responding to request: " + req.getId());
        if (helpRequestDAO.updateRequestStatus(req.getId(), "responded")) {
            refreshAlerts();
        }
    }
}