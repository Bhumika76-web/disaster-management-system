package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import models.DisasterReport;
import dao.DisasterReportDAO;
import models.User;
import models.Disaster;
import dao.DisasterDAO;

public class CitizenDashboard extends JPanel {
    private User citizen;
    private MainFrame mainFrame;
    private DisasterDAO disasterDAO = new DisasterDAO();
    private JPanel disasterCardsPanel;
    private JTextField searchField;
    private JButton searchBtn;
    private List<Disaster> currentDisasters = new ArrayList<>();
    private JComboBox<String> typeFilterCombo;
    private JSlider severitySlider;
    private JButton filterBtn;
    private SimpleMapPanel mapPanel;

    public CitizenDashboard(User citizen, MainFrame mainFrame) {
        this.citizen = citizen;
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
        refreshAlerts();
    }

    private void initUI() {

        JLabel headerLabel = new JLabel("🚨 CITIZEN ALERT CENTER");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(255, 150, 100));
        headerLabel.setBounds(20, 20, 800, 40);

        JLabel userLabel = new JLabel("Hello, " + citizen.getUsername() + " from " + citizen.getLocation());
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(150, 200, 255));
        userLabel.setBounds(20, 70, 600, 25);

        JLabel infoLabel = new JLabel("📍 Live Disaster Alerts near your location:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBounds(20, 110, 600, 25);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(20, 25, 47));
        searchPanel.setBounds(20, 140, 800, 50);

        JLabel searchLabel = new JLabel("🔍 Search Location:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchField.setBackground(new Color(50, 60, 90));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);

        searchBtn = createStyledButton("🔍 Search", new Color(100, 200, 255));
        searchBtn.addActionListener(e -> performSearch());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new Color(20, 25, 47));
        filterPanel.setBounds(20, 200, 800, 60);

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));

        typeFilterCombo = new JComboBox<>(new String[]{
                "All Types", "Flood", "Earthquake", "Fire",
                "Cyclone", "Landslide", "Tsunami", "Tornado"
        });
        typeFilterCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        typeFilterCombo.setBackground(new Color(50, 60, 90));
        typeFilterCombo.setForeground(Color.WHITE);
        typeFilterCombo.addActionListener(e -> performFilter());

        JLabel severityLabel = new JLabel("Min Severity:");
        severityLabel.setForeground(Color.WHITE);
        severityLabel.setFont(new Font("Arial", Font.BOLD, 11));

        severitySlider = new JSlider(1, 10, 1);
        severitySlider.setMajorTickSpacing(1);
        severitySlider.setPaintTicks(true);
        severitySlider.setPaintLabels(true);
        severitySlider.setBackground(new Color(20, 25, 47));
        severitySlider.setForeground(Color.WHITE);
        severitySlider.setPreferredSize(new Dimension(150, 50));
        severitySlider.addChangeListener(e -> performFilter());

        filterBtn = createStyledButton("Apply Filter", new Color(150, 200, 100));
        filterBtn.addActionListener(e -> performFilter());

        filterPanel.add(filterLabel);
        filterPanel.add(typeFilterCombo);
        filterPanel.add(severityLabel);
        filterPanel.add(severitySlider);
        filterPanel.add(filterBtn);

        mapPanel = new SimpleMapPanel();
        mapPanel.setBounds(20, 275, 860, 220);
        List<Disaster> initialDisasters = disasterDAO.getActiveDisasters();
        mapPanel.displayDisastersOnMap(initialDisasters);

        JLabel alertsLabel = new JLabel("📌 Active Alerts");
        alertsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        alertsLabel.setForeground(new Color(255, 150, 100));
        alertsLabel.setBounds(20, 505, 200, 25);

        disasterCardsPanel = new JPanel();
        disasterCardsPanel.setLayout(new BoxLayout(disasterCardsPanel, BoxLayout.Y_AXIS));
        disasterCardsPanel.setBackground(new Color(20, 25, 47));
        disasterCardsPanel.setOpaque(false);
        disasterCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(disasterCardsPanel);
        scrollPane.setBounds(20, 535, 860, 120);
        scrollPane.setBackground(new Color(20, 25, 47));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBackground(new Color(40, 50, 80));

        JButton refreshBtn = createStyledButton("🔄 REFRESH", new Color(150, 255, 150));
        refreshBtn.setBounds(20, 665, 120, 40);
        refreshBtn.addActionListener(e -> refreshAlerts());

        JButton alertsBtn = createStyledButton("📢 ALERTS", new Color(100, 200, 255));
        alertsBtn.setBounds(150, 665, 120, 40);
        alertsBtn.addActionListener(e -> mainFrame.showAlertsPage(citizen));

        JButton notificationsBtn = createStyledButton("🔔 NOTIFICATIONS", new Color(255, 200, 100));
        notificationsBtn.setBounds(280, 665, 140, 40);
        notificationsBtn.addActionListener(e -> mainFrame.showRiskAssessment(citizen));

        JButton reportBtn = createStyledButton("🆘 REPORT", new Color(255, 150, 100));
        reportBtn.setBounds(430, 665, 120, 40);
        reportBtn.addActionListener(e -> showReportDialog());

        JButton riskBtn = createStyledButton("📊 RISK", new Color(255, 150, 100));
        riskBtn.setBounds(560, 665, 100, 40);
        riskBtn.addActionListener(e -> mainFrame.showRiskAssessment(citizen));

        JButton logoutBtn = createStyledButton("LOGOUT", new Color(255, 100, 100));
        logoutBtn.setBounds(740, 665, 120, 40);
        logoutBtn.addActionListener(e -> mainFrame.logout());

        add(headerLabel);
        add(userLabel);
        add(infoLabel);
        add(searchPanel);
        add(filterPanel);
        add(notificationsBtn);
        add(mapPanel);
        add(alertsLabel);
        add(riskBtn);
        add(scrollPane);
        add(refreshBtn);
        add(alertsBtn);
        add(reportBtn);
        add(logoutBtn);
    }

    private void refreshAlerts() {
        disasterCardsPanel.removeAll();
        List<Disaster> disasters = disasterDAO.getActiveDisasters();
        currentDisasters = disasters;

        if (disasters.isEmpty()) {
            JLabel noAlertsLabel = new JLabel("✓ No active disasters in your area. Stay Safe!");
            noAlertsLabel.setFont(new Font("Arial", Font.BOLD, 16));
            noAlertsLabel.setForeground(new Color(100, 255, 150));
            disasterCardsPanel.add(noAlertsLabel);
        } else {
            for (Disaster d : disasters) {
                JPanel card = createDisasterCard(d);
                card.setMaximumSize(new Dimension(840, 75));
                card.setPreferredSize(new Dimension(840, 75));
                disasterCardsPanel.add(card);
                disasterCardsPanel.add(Box.createVerticalStrut(10));
            }
        }
        disasterCardsPanel.revalidate();
        disasterCardsPanel.repaint();

        mapPanel.displayDisastersOnMap(disasters);
    }

    private JPanel createDisasterCard(Disaster d) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color baseColor = d.getSeverity() >= 8 ? new Color(220, 60, 60) :
                        d.getSeverity() >= 5 ? new Color(230, 120, 50) :
                                new Color(100, 140, 200);

                Color darkColor = d.getSeverity() >= 8 ? new Color(180, 30, 30) :
                        d.getSeverity() >= 5 ? new Color(200, 90, 20) :
                                new Color(70, 110, 170);

                GradientPaint gradient = new GradientPaint(0, 0, baseColor, getWidth(), getHeight(), darkColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel(getDisasterIcon(d.getType()));
        iconLabel.setFont(new Font("Arial", Font.BOLD, 36));
        iconLabel.setBounds(12, 10, 55, 55);

        JLabel typeLabel = new JLabel(d.getType().toUpperCase());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setBounds(75, 8, 180, 24);

        JLabel locationLabel = new JLabel("📍 " + d.getLocation());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        locationLabel.setForeground(new Color(255, 255, 255, 220));
        locationLabel.setBounds(75, 32, 250, 18);

        JLabel severityLabel = new JLabel("Lvl: " + d.getSeverity() + "/10");
        severityLabel.setFont(new Font("Arial", Font.BOLD, 11));
        severityLabel.setForeground(Color.WHITE);
        severityLabel.setBackground(new Color(0, 0, 0, 100));
        severityLabel.setOpaque(true);
        severityLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        severityLabel.setBounds(330, 8, 75, 24);

        JLabel timeLabel = new JLabel("🕒 " + d.getTimestamp());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(200, 200, 200));
        timeLabel.setBounds(415, 8, 200, 18);

        String desc = d.getDescription().length() > 35 ?
                d.getDescription().substring(0, 35) + "..." : d.getDescription();
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(240, 240, 240));
        descLabel.setBounds(415, 32, 320, 18);

        card.add(iconLabel);
        card.add(typeLabel);
        card.add(locationLabel);
        card.add(severityLabel);
        card.add(timeLabel);
        card.add(descLabel);

        return card;
    }

    private String getDisasterIcon(String type) {
        return switch (type.toLowerCase()) {
            case "flood" -> "🌊";
            case "earthquake" -> "🏔️";
            case "fire" -> "🔥";
            case "cyclone" -> "🌪️";
            case "landslide" -> "⛰️";
            case "tsunami" -> "🌊";
            case "tornado" -> "🌀";
            default -> "⚠️";
        };
    }

    private void showReportDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(new Color(40, 50, 80));

        JTextField typeField = new JTextField("Flood");
        typeField.setBackground(new Color(50, 60, 90));
        typeField.setForeground(Color.WHITE);

        JTextField locationField = new JTextField(citizen.getLocation());
        locationField.setBackground(new Color(50, 60, 90));
        locationField.setForeground(Color.WHITE);

        JSpinner severitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setBackground(new Color(50, 60, 90));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setLineWrap(true);

        panel.add(new JLabel("Disaster Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Severity (1-10):"));
        panel.add(severitySpinner);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Report Disaster", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            DisasterReport report = new DisasterReport(
                    citizen.getId(),
                    citizen.getUsername(),
                    typeField.getText(),
                    locationField.getText(),
                    descriptionArea.getText(),
                    (int) severitySpinner.getValue()
            );

            DisasterReportDAO reportDAO = new DisasterReportDAO();
            if (reportDAO.submitReport(report)) {
                JOptionPane.showMessageDialog(this, "✓ Disaster Report Submitted!\nThank you for reporting.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit report", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    private void performSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            refreshAlerts();
            return;
        }

        List<Disaster> searchResults = disasterDAO.searchByLocation(searchText);
        disasterCardsPanel.removeAll();

        if (searchResults.isEmpty()) {
            JLabel noResultsLabel = new JLabel("❌ No disasters found in: " + searchText);
            noResultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
            noResultsLabel.setForeground(new Color(255, 100, 100));
            disasterCardsPanel.add(noResultsLabel);
        } else {
            for (Disaster d : searchResults) {
                JPanel card = createDisasterCard(d);
                card.setMaximumSize(new Dimension(840, 75));
                card.setPreferredSize(new Dimension(840, 75));
                disasterCardsPanel.add(card);
                disasterCardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        disasterCardsPanel.revalidate();
        disasterCardsPanel.repaint();

        mapPanel.displayDisastersOnMap(searchResults);
    }

    private void performFilter() {
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        int minSeverity = severitySlider.getValue();

        List<Disaster> filteredDisasters = new ArrayList<>();

        if (selectedType.equals("All Types")) {
            filteredDisasters = disasterDAO.getDisastersByLocation(citizen.getLocation());
        } else {
            filteredDisasters = disasterDAO.filterDisastersByTypeAndLocation(
                    selectedType,
                    citizen.getLocation()
            );
        }

        List<Disaster> finalResults = new ArrayList<>();
        for (Disaster d : filteredDisasters) {
            if (d.getSeverity() >= minSeverity && d.getStatus().equals("active")) {
                finalResults.add(d);
            }
        }

        currentDisasters = finalResults;
        disasterCardsPanel.removeAll();

        if (finalResults.isEmpty()) {
            JLabel noResultsLabel = new JLabel("✓ No disasters matching filters. Stay Safe!");
            noResultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
            noResultsLabel.setForeground(new Color(100, 255, 150));
            disasterCardsPanel.add(noResultsLabel);
        } else {
            for (Disaster d : finalResults) {
                JPanel card = createDisasterCard(d);
                card.setMaximumSize(new Dimension(840, 75));
                card.setPreferredSize(new Dimension(840, 75));
                disasterCardsPanel.add(card);
                disasterCardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        disasterCardsPanel.revalidate();
        disasterCardsPanel.repaint();

        mapPanel.displayDisastersOnMap(finalResults);
    }
}