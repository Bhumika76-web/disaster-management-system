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
import dao.DisasterDAO;

public class RiskAssessmentPage extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private DisasterReportDAO disasterReportDAO = new DisasterReportDAO();
    private NotificationAcknowledgmentDAO notificationDAO = new NotificationAcknowledgmentDAO();
    private DisasterDAO disasterDAO = new DisasterDAO();
    private boolean isAdmin;
    private JPanel notificationsPanel;

    public RiskAssessmentPage(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.isAdmin = "admin".equals(user.getUserType());

        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
    }

    private void initUI() {
        int yPos = 15;

        JButton backBtn = createIconButton("←", new Color(80, 100, 140));
        backBtn.setBounds(20, yPos, 35, 35);
        backBtn.addActionListener(e -> mainFrame.showDashboard(user));

        JLabel headerLabel = new JLabel("Risk Assessment & Notifications");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
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
            JLabel myNotifTitle = new JLabel("🔔 My Notifications");
            myNotifTitle.setFont(new Font("Arial", Font.BOLD, 16));
            myNotifTitle.setForeground(new Color(100, 200, 255));
            myNotifTitle.setBounds(20, yPos, 300, 25);

            yPos += 30;

            notificationsPanel = new JPanel();
            notificationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            notificationsPanel.setBackground(new Color(20, 25, 47));
            notificationsPanel.setOpaque(false);

            JScrollPane scroll = new JScrollPane(notificationsPanel);
            scroll.setBounds(20, yPos, 860, 200);
            scroll.setBackground(new Color(20, 25, 47));
            scroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
            scroll.getVerticalScrollBar().setUnitIncrement(15);

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
                g2d.setColor(new Color(40, 60, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(100, 150, 255));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        panel.setOpaque(false);

        List<DisasterReport> highRiskAreas = disasterReportDAO.getHighRiskAreas();

        JLabel titleLabel = new JLabel("High-Risk Areas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(255, 150, 100));
        titleLabel.setBounds(20, 15, 200, 25);

        JLabel countLabel = new JLabel(String.valueOf(highRiskAreas.size()));
        countLabel.setFont(new Font("Arial", Font.BOLD, 32));
        countLabel.setForeground(new Color(255, 150, 100));
        countLabel.setBounds(30, 45, 100, 40);

        JLabel areasLabel = new JLabel("Areas +" + Math.max(0, highRiskAreas.size() - 1));
        areasLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        areasLabel.setForeground(new Color(150, 200, 255));
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
                            Math.max(areaRisk.getOrDefault(report.getLocation(), 0), report.getEstimatedSeverity()));
                }

                int index = 0;
                for (String area : areaRisk.keySet()) {
                    if (index >= 3) break;

                    int severity = areaRisk.get(area);
                    int barHeight = (int) ((severity / 10.0) * maxHeight);

                    g2d.setColor(new Color(100, 150, 100));
                    g2d.fillRect(chartX + (index * 100), chartY + maxHeight - barHeight, barWidth, barHeight);

                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
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
                g2d.setColor(new Color(40, 60, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(100, 150, 255));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Notification Insights");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(255, 150, 100));
        titleLabel.setBounds(20, 15, 250, 25);

        double percentage = notificationDAO.getAcknowledgmentPercentage();

        JLabel percentLabel = new JLabel(String.format("%.0f%%", percentage));
        percentLabel.setFont(new Font("Arial", Font.BOLD, 32));
        percentLabel.setForeground(new Color(100, 255, 150));
        percentLabel.setBounds(30, 45, 100, 40);

        JLabel engageLabel = new JLabel("Engagement -10%");
        engageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        engageLabel.setForeground(new Color(255, 100, 100));
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
                    g2d.setColor(new Color(100, 150, 100));
                    g2d.fillRect(chartX, chartY, acknowledgedWidth, 30);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                    g2d.drawString("Acknowledged (" + acknowledged + ")", chartX + 5, chartY + 48);

                    int ignoredWidth = (int) ((ignored * 1.0 / total) * chartWidth);
                    g2d.setColor(new Color(150, 100, 100));
                    g2d.fillRect(chartX, chartY + 50, ignoredWidth, 30);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("Ignored (" + ignored + ")", chartX + 5, chartY + 98);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.drawString("No notification data yet", chartX + 5, chartY + 40);
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
        notificationsPanel.removeAll();

        List<DisasterReport> reports = disasterReportDAO.getAllReports();

        if (reports.isEmpty()) {
            JLabel noNotifLabel = new JLabel("✅ No recent disaster reports");
            noNotifLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            noNotifLabel.setForeground(new Color(100, 200, 100));
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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = report.getEstimatedSeverity() >= 7 ?
                        new Color(100, 60, 60) : new Color(60, 80, 60);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor = report.getEstimatedSeverity() >= 7 ?
                        new Color(255, 100, 100) : new Color(100, 255, 150);
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel typeLabel = new JLabel(report.getDisasterType());
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setBounds(15, 8, 200, 20);

        JLabel descLabel = new JLabel(report.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(15, 32, 450, 18);

        JLabel locationLabel = new JLabel("📍 " + report.getLocation() + " | Severity: " + report.getEstimatedSeverity());
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        locationLabel.setForeground(new Color(150, 200, 255));
        locationLabel.setBounds(15, 54, 550, 15);

        JLabel timeLabel = new JLabel("Time: " + report.getTimestamp());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setBounds(15, 72, 400, 12);

        JButton acknowledgeBtn = createStyledButton("✅ ACKNOWLEDGE", new Color(100, 200, 100));
        acknowledgeBtn.setBounds(620, 25, 95, 35);
        acknowledgeBtn.setFont(new Font("Arial", Font.BOLD, 9));
        acknowledgeBtn.addActionListener(e -> {
            NotificationAcknowledgment ack = new NotificationAcknowledgment(
                    user.getId(), report.getId(), user.getUsername(),
                    report.getDisasterType(), "acknowledged"
            );
            if (notificationDAO.recordNotification(ack)) {
                JOptionPane.showMessageDialog(null,
                        "✅ Thank you for acknowledging!\n\nGraph will update automatically.",
                        "Acknowledged", JOptionPane.INFORMATION_MESSAGE);
                loadMyNotifications();
            }
        });

        JButton ignoreBtn = createStyledButton("❌ IGNORE", new Color(200, 100, 100));
        ignoreBtn.setBounds(725, 25, 85, 35);
        ignoreBtn.setFont(new Font("Arial", Font.BOLD, 9));
        ignoreBtn.addActionListener(e -> {
            NotificationAcknowledgment ack = new NotificationAcknowledgment(
                    user.getId(), report.getId(), user.getUsername(),
                    report.getDisasterType(), "ignored"
            );
            if (notificationDAO.recordNotification(ack)) {
                JOptionPane.showMessageDialog(null,
                        "❌ Alert ignored!\n\nGraph will update automatically.",
                        "Ignored", JOptionPane.INFORMATION_MESSAGE);
                loadMyNotifications();
            }
        });

        card.add(typeLabel);
        card.add(descLabel);
        card.add(locationLabel);
        card.add(timeLabel);
        card.add(acknowledgeBtn);
        card.add(ignoreBtn);

        return card;
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
}