package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import models.DisasterReport;
import models.Disaster;
import models.User;
import dao.DisasterReportDAO;
import dao.DisasterDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;


public class CitizenDashboard extends JPanel {

    private static final String CLASS_NAME = "CitizenDashboard";

    private User citizen;
    private MainFrame mainFrame;
    private DisasterDAO disasterDAO;
    private JPanel disasterCardsPanel;
    private JTextField searchField;
    private JButton searchBtn;
    private List<Disaster> currentDisasters;
    private JComboBox<String> typeFilterCombo;
    private JSlider severitySlider;
    private SimpleMapPanel mapPanel;

    public CitizenDashboard(User citizen, MainFrame mainFrame) {
        this.citizen = citizen;
        this.mainFrame = mainFrame;
        this.disasterDAO = new DisasterDAO();
        this.currentDisasters = new ArrayList<>();

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing CitizenDashboard for: " + citizen.getUsername());
        initUI();
        refreshAlerts();
    }


    private void initUI() {
        final int PADDING = 20;
        int yPos = PADDING;


        JLabel headerLabel = ComponentFactory.createTitleLabel(
                "🚨 CITIZEN ALERT CENTER", AppTheme.COLOR_ORANGE);
        headerLabel.setBounds(PADDING, yPos, 800, 40);
        yPos += 50;


        JLabel userLabel = new JLabel(
                "Hello, " + citizen.getUsername() + " from " + citizen.getLocation());
        userLabel.setFont(AppTheme.FONT_LABEL);
        userLabel.setForeground(AppTheme.TEXT_SECONDARY);
        userLabel.setBounds(PADDING, yPos, 600, 25);
        yPos += 40;


        JLabel infoLabel = new JLabel("📍 Live Disaster Alerts near your location:");
        infoLabel.setFont(AppTheme.FONT_LABEL);
        infoLabel.setForeground(AppTheme.TEXT_WHITE);
        infoLabel.setBounds(PADDING, yPos, 600, 25);
        yPos += 35;


        JPanel searchPanel = createSearchPanel();
        searchPanel.setBounds(PADDING, yPos, 860, 50);
        yPos += 60;


        JPanel filterPanel = createFilterPanel();
        filterPanel.setBounds(PADDING, yPos, 860, 60);
        yPos += 70;


        mapPanel = new SimpleMapPanel();
        mapPanel.setBounds(PADDING, yPos, 860, 220);
        List<Disaster> initialDisasters = disasterDAO.getActiveDisasters();
        mapPanel.displayDisastersOnMap(initialDisasters);
        yPos += 230;


        JLabel alertsLabel = ComponentFactory.createHeaderLabel(
                "📋 Active Alerts", AppTheme.COLOR_ORANGE);
        alertsLabel.setBounds(PADDING, yPos, 200, 25);
        yPos += 35;

        disasterCardsPanel = new JPanel();
        disasterCardsPanel.setLayout(new BoxLayout(disasterCardsPanel, BoxLayout.Y_AXIS));
        disasterCardsPanel.setBackground(AppTheme.BG_DARK);
        disasterCardsPanel.setOpaque(false);

        JScrollPane scrollPane = UIUtils.createThemedScrollPane(disasterCardsPanel);
        scrollPane.setBounds(PADDING, yPos, 860, 120);
        yPos += 135;


        JButton refreshBtn = ComponentFactory.createSuccessButton(
                "🔄 REFRESH", this::refreshAlerts);
        refreshBtn.setBounds(PADDING, yPos, 120, 40);

        JButton alertsBtn = ComponentFactory.createPrimaryButton(
                "🔔 ALERTS", () -> mainFrame.showAlertsPage(citizen));
        alertsBtn.setBounds(150, yPos, 120, 40);

        JButton notificationsBtn = ComponentFactory.createWarningButton(
                "📢 NOTIFICATIONS", () -> mainFrame.showRiskAssessment(citizen));
        notificationsBtn.setBounds(280, yPos, 140, 40);

        JButton reportBtn = ComponentFactory.createWarningButton(
                "🆘 REPORT", this::showReportDialog);
        reportBtn.setBounds(430, yPos, 120, 40);

        JButton riskBtn = ComponentFactory.createDangerButton(
                "📊 RISK", () -> mainFrame.showRiskAssessment(citizen));
        riskBtn.setBounds(560, yPos, 100, 40);

        JButton logoutBtn = ComponentFactory.createDangerButton(
                "🚪 LOGOUT", () -> mainFrame.logout());
        logoutBtn.setBounds(740, yPos, 120, 40);


        add(headerLabel);
        add(userLabel);
        add(infoLabel);
        add(searchPanel);
        add(filterPanel);
        add(mapPanel);
        add(alertsLabel);
        add(scrollPane);
        add(refreshBtn);
        add(alertsBtn);
        add(notificationsBtn);
        add(reportBtn);
        add(riskBtn);
        add(logoutBtn);
    }


    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(AppTheme.BG_DARK);

        JLabel searchLabel = new JLabel("📍 Search Location:");
        searchLabel.setForeground(AppTheme.TEXT_WHITE);
        searchLabel.setFont(AppTheme.FONT_LABEL);

        searchField = new JTextField(20);
        UIUtils.styleTextField(searchField);

        searchBtn = ComponentFactory.createPrimaryButton(
                "🔍 Search", this::performSearch);

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchBtn);

        return panel;
    }


    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(AppTheme.BG_DARK);

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setForeground(AppTheme.TEXT_WHITE);
        filterLabel.setFont(AppTheme.FONT_LABEL);

        typeFilterCombo = new JComboBox<>(new String[]{
                "All Types", "Flood", "Earthquake", "Fire",
                "Cyclone", "Landslide", "Tsunami", "Tornado"
        });
        UIUtils.styleComboBox(typeFilterCombo);
        typeFilterCombo.addActionListener(e -> performFilter());

        JLabel severityLabel = new JLabel("Min Severity:");
        severityLabel.setForeground(AppTheme.TEXT_WHITE);
        severityLabel.setFont(AppTheme.FONT_LABEL);

        severitySlider = new JSlider(1, 10, 1);
        severitySlider.setMajorTickSpacing(1);
        severitySlider.setPaintTicks(true);
        severitySlider.setPaintLabels(true);
        severitySlider.setBackground(AppTheme.BG_DARK);
        severitySlider.setForeground(AppTheme.TEXT_WHITE);
        severitySlider.setPreferredSize(new Dimension(150, 50));
        severitySlider.addChangeListener(e -> performFilter());

        JButton filterBtn = ComponentFactory.createSuccessButton(
                "Apply Filter", this::performFilter);

        panel.add(filterLabel);
        panel.add(typeFilterCombo);
        panel.add(severityLabel);
        panel.add(severitySlider);
        panel.add(filterBtn);

        return panel;
    }


    private void refreshAlerts() {
        Logger.debug(CLASS_NAME, "Refreshing alerts for user: " + citizen.getUsername());
        disasterCardsPanel.removeAll();
        List<Disaster> disasters = disasterDAO.getActiveDisasters();
        currentDisasters = disasters;

        if (disasters.isEmpty()) {
            JLabel noAlertsLabel = new JLabel("✅ No active disasters in your area. Stay Safe!");
            noAlertsLabel.setFont(AppTheme.FONT_LABEL);
            noAlertsLabel.setForeground(AppTheme.COLOR_GREEN);
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
        Logger.info(CLASS_NAME, "Alerts refreshed - Total: " + disasters.size());
    }


    private JPanel createDisasterCard(Disaster d) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color baseColor = d.getSeverity() >= 8 ? new Color(220, 60, 60) :
                        d.getSeverity() >= 5 ? new Color(230, 120, 50) :
                                new Color(100, 140, 200);

                Color darkColor = d.getSeverity() >= 8 ? new Color(180, 30, 30) :
                        d.getSeverity() >= 5 ? new Color(200, 90, 20) :
                                new Color(70, 110, 170);

                GradientPaint gradient = new GradientPaint(0, 0, baseColor,
                        getWidth(), getHeight(), darkColor);
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
        typeLabel.setFont(AppTheme.FONT_HEADER);
        typeLabel.setForeground(AppTheme.TEXT_WHITE);
        typeLabel.setBounds(75, 8, 180, 24);

        JLabel locationLabel = new JLabel("📍 " + d.getLocation());
        locationLabel.setFont(AppTheme.FONT_REGULAR);
        locationLabel.setForeground(new Color(255, 255, 255, 220));
        locationLabel.setBounds(75, 32, 250, 18);

        JLabel severityLabel = new JLabel("Lvl: " + d.getSeverity() + "/10");
        severityLabel.setFont(AppTheme.FONT_REGULAR);
        severityLabel.setForeground(AppTheme.TEXT_WHITE);
        severityLabel.setBackground(new Color(0, 0, 0, 100));
        severityLabel.setOpaque(true);
        severityLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        severityLabel.setBounds(330, 8, 75, 24);

        JLabel timeLabel = new JLabel("🕐 " + d.getTimestamp());
        timeLabel.setFont(AppTheme.FONT_SMALL);
        timeLabel.setForeground(new Color(200, 200, 200));
        timeLabel.setBounds(415, 8, 200, 18);

        String desc = d.getDescription().length() > 35 ?
                d.getDescription().substring(0, 35) + "..." : d.getDescription();
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(AppTheme.FONT_SMALL);
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
            case "earthquake" -> "🏚️";
            case "fire" -> "🔥";
            case "cyclone" -> "🌪️";
            case "landslide" -> "⛰️";
            case "tsunami" -> "🌊";
            case "tornado" -> "🌀";
            default -> "⚠️";
        };
    }


    private void showReportDialog() {
        Logger.debug(CLASS_NAME, "Opening disaster report dialog");

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(AppTheme.BG_MEDIUM);

        JTextField typeField = new JTextField("Flood");
        UIUtils.styleTextField(typeField);

        JTextField locationField = new JTextField(citizen.getLocation());
        UIUtils.styleTextField(locationField);

        JSpinner severitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        JTextArea descriptionArea = new JTextArea(3, 20);
        UIUtils.styleTextArea(descriptionArea);

        panel.add(new JLabel("Disaster Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Severity (1-10):"));
        panel.add(severitySpinner);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Report Disaster",
                JOptionPane.OK_CANCEL_OPTION);

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
                Logger.info(CLASS_NAME, "Disaster report submitted by: " + citizen.getUsername());
                UIUtils.showInfo(this, "Success",
                        "✅ Disaster Report Submitted!\nThank you for reporting.");
            } else {
                Logger.warn(CLASS_NAME, "Failed to submit disaster report");
                UIUtils.showError(this, "Error",
                        "Failed to submit report");
            }
        }
    }


    private void performSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            refreshAlerts();
            return;
        }

        Logger.debug(CLASS_NAME, "Searching disasters at location: " + searchText);
        List<Disaster> searchResults = disasterDAO.searchByLocation(searchText);
        disasterCardsPanel.removeAll();

        if (searchResults.isEmpty()) {
            JLabel noResultsLabel = new JLabel("❌ No disasters found in: " + searchText);
            noResultsLabel.setFont(AppTheme.FONT_LABEL);
            noResultsLabel.setForeground(AppTheme.COLOR_RED);
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
        Logger.info(CLASS_NAME, "Search results: " + searchResults.size());
    }


    private void performFilter() {
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        int minSeverity = severitySlider.getValue();

        Logger.debug(CLASS_NAME, "Filtering: Type=" + selectedType + ", MinSeverity=" + minSeverity);

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
            JLabel noResultsLabel = new JLabel("✅ No disasters matching filters. Stay Safe!");
            noResultsLabel.setFont(AppTheme.FONT_LABEL);
            noResultsLabel.setForeground(AppTheme.COLOR_GREEN);
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
        Logger.info(CLASS_NAME, "Filter results: " + finalResults.size());
    }
}