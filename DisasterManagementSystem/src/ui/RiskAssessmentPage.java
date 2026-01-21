package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import models.User;
import models.DisasterReport;
import models.NotificationAcknowledgment;
import dao.DisasterReportDAO;
import dao.NotificationAcknowledgmentDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;
import config.AppConstants;


public class RiskAssessmentPage extends JPanel {

    private static final String CLASS_NAME = "RiskAssessmentPage";

    private User user;
    private MainFrame mainFrame;
    private DisasterReportDAO disasterReportDAO;
    private NotificationAcknowledgmentDAO notificationDAO;
    private boolean isAdmin;
    private JPanel notificationsPanel;

    public RiskAssessmentPage(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.disasterReportDAO = new DisasterReportDAO();
        this.notificationDAO = new NotificationAcknowledgmentDAO();
        this.isAdmin = AppConstants.USER_TYPE_ADMIN.equals(user.getUserType());

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing RiskAssessmentPage");
        initUI();
    }


    private void initUI() {
        int yPos = 15;

        JButton backBtn = ComponentFactory.createPrimaryButton("↶", () -> mainFrame.showDashboard(user));
        backBtn.setBounds(20, yPos, 35, 35);

        JLabel headerLabel = ComponentFactory.createTitleLabel(
                "🔍 Risk Assessment & Notifications", AppTheme.TEXT_WHITE);
        headerLabel.setBounds(70, yPos, 800, 35);
        yPos += 50;

        if (isAdmin) {
            JPanel highRiskPanel = createHighRiskPanel();
            highRiskPanel.setBounds(20, yPos, 860, 240);
            add(highRiskPanel);
            yPos += 260;
        }

        JPanel notificationPanel = createNotificationPanel();
        notificationPanel.setBounds(20, yPos, 860, 220);
        add(notificationPanel);
        yPos += 240;

        if (!isAdmin) {
            JLabel myNotifTitle = ComponentFactory.createHeaderLabel(
                    "🔔 My Notifications", AppTheme.COLOR_BLUE);
            myNotifTitle.setBounds(20, yPos, 300, 25);
            yPos += 30;

            notificationsPanel = new JPanel();
            notificationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            notificationsPanel.setBackground(AppTheme.BG_DARK);
            notificationsPanel.setOpaque(false);

            JScrollPane scroll = UIUtils.createThemedScrollPane(notificationsPanel);
            scroll.setBounds(20, yPos, 860, 200);

            loadMyNotifications();
            add(scroll);
        }

        add(backBtn);
        add(headerLabel);
    }


    private JPanel createHighRiskPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.BORDER_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        panel.setOpaque(false);

        List<DisasterReport> highRiskAreas = disasterReportDAO.getHighRiskAreas();

        JLabel titleLabel = ComponentFactory.createHeaderLabel(
                "High-Risk Areas", AppTheme.COLOR_ORANGE);
        titleLabel.setBounds(20, 15, 200, 25);

        JLabel countLabel = new JLabel(String.valueOf(highRiskAreas.size()));
        countLabel.setFont(AppTheme.FONT_TITLE);
        countLabel.setForeground(AppTheme.COLOR_ORANGE);
        countLabel.setBounds(30, 45, 100, 40);

        JLabel areasLabel = new JLabel("Areas +" + Math.max(0, highRiskAreas.size() - 1));
        areasLabel.setFont(AppTheme.FONT_REGULAR);
        areasLabel.setForeground(AppTheme.TEXT_SECONDARY);
        areasLabel.setBounds(30, 90, 100, 20);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int barWidth = 50;
                int chartX = 30;
                int chartY = 30;
                int maxHeight = 80;

                Map<String, Integer> areaRisk = new HashMap<>();
                for (DisasterReport report : highRiskAreas) {
                    areaRisk.put(report.getLocation(),
                            Math.max(areaRisk.getOrDefault(report.getLocation(), 0),
                                    report.getEstimatedSeverity()));
                }

                int index = 0;
                for (String area : areaRisk.keySet()) {
                    if (index >= 3) break;
                    int severity = areaRisk.get(area);
                    int barHeight = (int) ((severity / 10.0) * maxHeight);
                    g2d.setColor(AppTheme.COLOR_GREEN);
                    g2d.fillRect(chartX + (index * 100), chartY + maxHeight - barHeight,
                            barWidth, barHeight);
                    g2d.setColor(AppTheme.TEXT_WHITE);
                    g2d.setFont(AppTheme.FONT_SMALL);
                    String areaName = area.length() > 8 ? area.substring(0, 8) : area;
                    g2d.drawString(areaName, chartX + (index * 100) - 5, chartY + maxHeight + 15);
                    index++;
                }
            }
        };
        chartPanel.setBounds(250, 25, 550, 130);

        panel.add(titleLabel);
        panel.add(countLabel);
        panel.add(areasLabel);
        panel.add(chartPanel);

        return panel;
    }


    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.BORDER_LIGHT);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        panel.setOpaque(false);

        JLabel titleLabel = ComponentFactory.createHeaderLabel(
                "📊 Notification Insights", AppTheme.COLOR_ORANGE);
        titleLabel.setBounds(20, 15, 250, 25);

        double percentage = notificationDAO.getAcknowledgmentPercentage();
        JLabel percentLabel = new JLabel(String.format("%.0f%%", percentage));
        percentLabel.setFont(AppTheme.FONT_TITLE);
        percentLabel.setForeground(AppTheme.COLOR_GREEN);
        percentLabel.setBounds(30, 45, 100, 40);

        JLabel engageLabel = new JLabel("Engagement Rate");
        engageLabel.setFont(AppTheme.FONT_REGULAR);
        engageLabel.setForeground(AppTheme.COLOR_RED);
        engageLabel.setBounds(30, 90, 150, 20);

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                int chartX = 30;
                int chartY = 20;
                int chartWidth = 500;
                int chartHeight = 80;

                int acknowledged = notificationDAO.getAcknowledgedCount();
                int ignored = notificationDAO.getIgnoredCount();
                int total = acknowledged + ignored;

                if (total > 0) {
                    int acknowledgedWidth = (int) ((acknowledged * 1.0 / total) * chartWidth);
                    g2d.setColor(AppTheme.COLOR_GREEN);
                    g2d.fillRect(chartX, chartY, acknowledgedWidth, 30);
                    g2d.setColor(AppTheme.TEXT_WHITE);
                    g2d.setFont(AppTheme.FONT_SMALL);
                    g2d.drawString("Acknowledged (" + acknowledged + ")", chartX + 5, chartY + 48);

                    int ignoredWidth = (int) ((ignored * 1.0 / total) * chartWidth);
                    g2d.setColor(AppTheme.COLOR_RED);
                    g2d.fillRect(chartX, chartY + 50, ignoredWidth, 30);
                    g2d.setColor(AppTheme.TEXT_WHITE);
                    g2d.drawString("Ignored (" + ignored + ")", chartX + 5, chartY + 98);
                }
            }
        };
        chartPanel.setBounds(250, 25, 550, 140);

        panel.add(titleLabel);
        panel.add(percentLabel);
        panel.add(engageLabel);
        panel.add(chartPanel);

        return panel;
    }


    private void loadMyNotifications() {
        Logger.debug(CLASS_NAME, "Loading notifications for user: " + user.getId());
        notificationsPanel.removeAll();

        List<DisasterReport> reports = disasterReportDAO.getAllReports();

        if (reports.isEmpty()) {
            JLabel noNotifLabel = new JLabel("✅ No recent disaster reports");
            noNotifLabel.setFont(AppTheme.FONT_REGULAR);
            noNotifLabel.setForeground(AppTheme.COLOR_GREEN);
            notificationsPanel.add(noNotifLabel);
        } else {
            for (DisasterReport report : reports) {
                JPanel notifCard = createNotificationCard(report);
                notifCard.setPreferredSize(new Dimension(830, 115));
                notificationsPanel.add(notifCard);
            }
        }

        notificationsPanel.revalidate();
        notificationsPanel.repaint();
    }


    private JPanel createNotificationCard(DisasterReport report) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = report.getEstimatedSeverity() >= 7 ?
                        new Color(100, 60, 60) : new Color(60, 80, 60);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor = report.getEstimatedSeverity() >= 7 ?
                        AppTheme.COLOR_RED : AppTheme.COLOR_GREEN;
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel typeLabel = new JLabel(report.getDisasterType());
        typeLabel.setFont(AppTheme.FONT_LABEL);
        typeLabel.setForeground(AppTheme.TEXT_WHITE);
        typeLabel.setBounds(15, 8, 200, 20);

        JLabel descLabel = new JLabel(report.getDescription());
        descLabel.setFont(AppTheme.FONT_REGULAR);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(15, 32, 450, 18);

        JLabel locationLabel = new JLabel("📍 " + report.getLocation() +
                " | Severity: " + report.getEstimatedSeverity());
        locationLabel.setFont(AppTheme.FONT_SMALL);
        locationLabel.setForeground(AppTheme.TEXT_SECONDARY);
        locationLabel.setBounds(15, 54, 550, 15);

        JLabel timeLabel = new JLabel("Time: " + report.getTimestamp());
        timeLabel.setFont(AppTheme.FONT_SMALL);
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setBounds(15, 72, 400, 12);

        JButton acknowledgeBtn = ComponentFactory.createSuccessButton(
                "✅ ACKNOWLEDGE", () -> handleAcknowledge(report));
        acknowledgeBtn.setBounds(620, 25, 95, 35);
        acknowledgeBtn.setFont(AppTheme.FONT_SMALL);

        JButton ignoreBtn = ComponentFactory.createDangerButton(
                "❌ IGNORE", () -> handleIgnore(report));
        ignoreBtn.setBounds(725, 25, 85, 35);
        ignoreBtn.setFont(AppTheme.FONT_SMALL);

        card.add(typeLabel);
        card.add(descLabel);
        card.add(locationLabel);
        card.add(timeLabel);
        card.add(acknowledgeBtn);
        card.add(ignoreBtn);

        return card;
    }


    private void handleAcknowledge(DisasterReport report) {
        Logger.debug(CLASS_NAME, "User acknowledged report: " + report.getId());
        NotificationAcknowledgment ack = new NotificationAcknowledgment(
                user.getId(), report.getId(), user.getUsername(),
                report.getDisasterType(), "acknowledged"
        );
        if (notificationDAO.recordNotification(ack)) {
            UIUtils.showInfo(this, "Acknowledged",
                    "✅ Thank you for acknowledging!");
            loadMyNotifications();
        }
    }


    private void handleIgnore(DisasterReport report) {
        Logger.debug(CLASS_NAME, "User ignored report: " + report.getId());
        NotificationAcknowledgment ack = new NotificationAcknowledgment(
                user.getId(), report.getId(), user.getUsername(),
                report.getDisasterType(), "ignored"
        );
        if (notificationDAO.recordNotification(ack)) {
            UIUtils.showInfo(this, "Ignored",
                    "❌ Alert ignored!");
            loadMyNotifications();
        }
    }
}