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

public class AlertsPage extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private DisasterDAO disasterDAO = new DisasterDAO();
    private HelpRequestDAO helpRequestDAO = new HelpRequestDAO();
    private JPanel activeAlertsPanel;
    private JPanel helpRequestsPanel;
    private boolean isAdmin;
    private boolean isResponder;

    public AlertsPage(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.isAdmin = "admin".equals(user.getUserType());
        this.isResponder = "responder".equals(user.getUserType());

        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
        refreshAlerts();
    }

    private void initUI() {
        int yPos = 15;

        JButton backBtn = createIconButton("←", new Color(80, 100, 140));
        backBtn.setBounds(20, yPos, 35, 35);
        backBtn.addActionListener(e -> mainFrame.showDashboard(user));

        JLabel headerLabel = new JLabel("Alerts");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(70, yPos, 300, 35);

        yPos += 50;

        if (isAdmin) {
            JLabel broadcastTitle = new JLabel("Broadcast Alert");
            broadcastTitle.setFont(new Font("Arial", Font.BOLD, 16));
            broadcastTitle.setForeground(new Color(100, 200, 255));
            broadcastTitle.setBounds(20, yPos, 200, 25);

            yPos += 30;

            JTextField titleField = createStyledTextField("Enter alert title");
            titleField.setBounds(20, yPos, 860, 40);
            yPos += 50;

            JTextArea descArea = createStyledTextArea("Enter alert description");
            descArea.setBounds(20, yPos, 860, 80);
            yPos += 90;

            JComboBox<String> zoneCombo = createStyledCombo(new String[]{
                    "Select Zone", "Coastal Area", "Mountain Region", "Urban Area",
                    "Rural Area", "Flood Zone", "Earthquake Zone"
            });
            zoneCombo.setBounds(20, yPos, 200, 35);

            yPos += 50;

            JButton broadcastBtn = createStyledButton("Broadcast Alert", new Color(50, 255, 100));
            broadcastBtn.setBounds(20, yPos, 200, 45);
            broadcastBtn.setFont(new Font("Arial", Font.BOLD, 14));
            broadcastBtn.addActionListener(e -> handleBroadcast(titleField, descArea, zoneCombo));

            add(broadcastTitle);
            add(titleField);
            add(descArea);
            add(zoneCombo);
            add(broadcastBtn);

            yPos += 60;
        }

        JLabel activeTitle = new JLabel("Active Alerts");
        activeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        activeTitle.setForeground(new Color(100, 200, 255));
        activeTitle.setBounds(20, yPos, 200, 25);

        yPos += 30;

        activeAlertsPanel = new JPanel();
        activeAlertsPanel.setLayout(new BoxLayout(activeAlertsPanel, BoxLayout.Y_AXIS));
        activeAlertsPanel.setBackground(new Color(20, 25, 47));
        activeAlertsPanel.setOpaque(false);

        JScrollPane activeScroll = new JScrollPane(activeAlertsPanel);
        activeScroll.setBounds(20, yPos, 860, 100);
        activeScroll.setBackground(new Color(20, 25, 47));
        activeScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120), 1));
        activeScroll.getVerticalScrollBar().setUnitIncrement(15);

        yPos += 120;

        if (!isAdmin && !isResponder) {
            JLabel helpFormTitle = new JLabel("Request Help");
            helpFormTitle.setFont(new Font("Arial", Font.BOLD, 16));
            helpFormTitle.setForeground(new Color(255, 150, 100));
            helpFormTitle.setBounds(20, yPos, 200, 25);

            yPos += 30;

            JTextField descField = createStyledTextField("Describe what help you need");
            descField.setBounds(20, yPos, 860, 35);
            yPos += 45;

            JComboBox<String> typeCombo = createStyledCombo(new String[]{
                    "Select Help Type", "Medical", "Shelter", "Food", "Missing Person", "Other"
            });
            typeCombo.setBounds(20, yPos, 200, 35);

            JTextField phoneField = createStyledTextField("Contact Number");
            phoneField.setBounds(230, yPos, 200, 35);

            JButton submitBtn = createStyledButton("Submit Request", new Color(255, 150, 100));
            submitBtn.setBounds(440, yPos, 150, 35);
            submitBtn.addActionListener(e -> handleHelpRequest(descField, typeCombo, phoneField));

            add(helpFormTitle);
            add(descField);
            add(typeCombo);
            add(phoneField);
            add(submitBtn);

            yPos += 50;
        }

        String helpTitle = "";
        if (isAdmin) {
            helpTitle = "Help Requests (All)";
        } else if (isResponder) {
            helpTitle = "Help Requests (Available to Accept)";
        } else {
            helpTitle = "My Help Requests";
        }

        JLabel helpTitleLabel = new JLabel(helpTitle);
        helpTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        helpTitleLabel.setForeground(new Color(100, 200, 255));
        helpTitleLabel.setBounds(20, yPos, 500, 25);

        yPos += 30;

        helpRequestsPanel = new JPanel();
        helpRequestsPanel.setLayout(new BoxLayout(helpRequestsPanel, BoxLayout.Y_AXIS));
        helpRequestsPanel.setBackground(new Color(20, 25, 47));
        helpRequestsPanel.setOpaque(false);

        JScrollPane helpScroll = new JScrollPane(helpRequestsPanel);
        helpScroll.setBounds(20, yPos, 860, 150);
        helpScroll.setBackground(new Color(20, 25, 47));
        helpScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120), 1));
        helpScroll.getVerticalScrollBar().setUnitIncrement(15);

        add(backBtn);
        add(headerLabel);
        add(activeTitle);
        add(activeScroll);
        add(helpTitleLabel);
        add(helpScroll);
    }

    private void refreshAlerts() {

        activeAlertsPanel.removeAll();
        List<Disaster> activeDisasters = disasterDAO.getActiveDisasters();

        if (activeDisasters.isEmpty()) {
            JLabel noAlertsLabel = new JLabel("✓ No active alerts");
            noAlertsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            noAlertsLabel.setForeground(new Color(100, 200, 100));
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
            JLabel noRequestsLabel = new JLabel("✓ No help requests");
            noRequestsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            noRequestsLabel.setForeground(new Color(100, 200, 100));
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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = new Color(40, 60, 100);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(new Color(100, 150, 255));
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
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(65, 8, 200, 20);

        JLabel zoneLabel = new JLabel("📍 " + d.getLocation());
        zoneLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        zoneLabel.setForeground(new Color(150, 200, 255));
        zoneLabel.setBounds(65, 30, 300, 18);

        JLabel timeLabel = new JLabel("🕒 " + d.getTimestamp());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = new Color(50, 70, 110);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor = isResponder ? new Color(100, 200, 255) :
                        isAdmin ? new Color(255, 150, 100) :
                                new Color(100, 255, 150);
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
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(75, 10, 380, 20);

        JLabel descLabel = new JLabel(req.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(75, 32, 450, 16);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(75, 52, 500, 15);

        if (isResponder) {
            JButton acceptBtn = createStyledButton("ACCEPT", new Color(100, 255, 100));
            acceptBtn.setBounds(720, 18, 100, 50);
            acceptBtn.setFont(new Font("Arial", Font.BOLD, 12));
            acceptBtn.addActionListener(e -> {
                helpRequestDAO.updateRequestStatus(req.getId(), "in_progress");
                JOptionPane.showMessageDialog(this,
                        "✓ Help Request Accepted!\nResponding to: " + req.getUsername(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAlerts();
            });
            card.add(acceptBtn);
        }

        else if (isAdmin) {
            JButton respondBtn = createStyledButton("Respond", new Color(100, 200, 255));
            respondBtn.setBounds(720, 18, 100, 50);
            respondBtn.setFont(new Font("Arial", Font.BOLD, 12));
            respondBtn.addActionListener(e -> {
                helpRequestDAO.updateRequestStatus(req.getId(), "responded");
                refreshAlerts();
            });
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
            case "medical" -> "🚑";
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
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Disaster alert = new Disaster(title, zone, 8, desc, java.time.LocalDateTime.now().toString());
        if (disasterDAO.addDisaster(alert)) {
            JOptionPane.showMessageDialog(this, "✓ Alert Broadcast Successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            titleField.setText("");
            descArea.setText("");
            zoneCombo.setSelectedIndex(0);
            refreshAlerts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to broadcast alert",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHelpRequest(JTextField descField, JComboBox<String> typeCombo, JTextField phoneField) {
        String desc = descField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String phone = phoneField.getText().trim();

        if (desc.isEmpty() || type.equals("Select Help Type") || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        HelpRequest request = new HelpRequest(user.getId(), user.getUsername(), desc,
                user.getLocation(), type, phone);

        if (helpRequestDAO.submitHelpRequest(request)) {
            JOptionPane.showMessageDialog(this, "✓ Help Request Submitted!\nResponders will be notified soon.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            descField.setText("");
            typeCombo.setSelectedIndex(0);
            phoneField.setText("");
            refreshAlerts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit help request",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBackground(new Color(40, 55, 85));
        field.setForeground(new Color(180, 180, 180));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(180, 180, 180));
                }
            }
        });
        return field;
    }

    private JTextArea createStyledTextArea(String placeholder) {
        JTextArea area = new JTextArea(placeholder);
        area.setFont(new Font("Arial", Font.PLAIN, 12));
        area.setBackground(new Color(40, 55, 85));
        area.setForeground(new Color(180, 180, 180));
        area.setCaretColor(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        area.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (area.getText().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(new Color(180, 180, 180));
                }
            }
        });
        return area;
    }

    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBackground(new Color(40, 55, 85));
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120), 1));
        return combo;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createIconButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}