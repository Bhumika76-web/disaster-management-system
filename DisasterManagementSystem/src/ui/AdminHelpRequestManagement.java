package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.User;
import models.HelpRequest;
import dao.HelpRequestDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;

public class AdminHelpRequestManagement extends JPanel {

    private static final String CLASS_NAME = "AdminHelpRequestManagement";

    private User admin;
    private MainFrame mainFrame;
    private HelpRequestDAO helpRequestDAO;
    private JPanel pendingPanel;
    private JPanel respondedPanel;
    private JPanel resolvedPanel;
    private JLabel pendingCountLabel;
    private JLabel respondedCountLabel;
    private JLabel resolvedCountLabel;

    public AdminHelpRequestManagement(User admin, MainFrame mainFrame) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        this.helpRequestDAO = new HelpRequestDAO();

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing AdminHelpRequestManagement");
        initUI();
        refreshAllRequests();
    }


    private void initUI() {
        int yPos = 15;

        JButton backBtn = ComponentFactory.createPrimaryButton("↶", () -> mainFrame.showDashboard(admin));
        backBtn.setBounds(20, yPos, 35, 35);

        JLabel headerLabel = ComponentFactory.createTitleLabel(
                "🛡️ ADMIN HELP REQUEST MANAGEMENT", AppTheme.COLOR_ORANGE);
        headerLabel.setBounds(70, yPos, 800, 40);
        yPos += 50;


        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 10));
        statsPanel.setBackground(AppTheme.BG_LIGHT);
        statsPanel.setBounds(20, yPos, 860, 60);
        statsPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));

        pendingCountLabel = new JLabel("Pending: 0");
        pendingCountLabel.setFont(AppTheme.FONT_LABEL);
        pendingCountLabel.setForeground(AppTheme.COLOR_ORANGE);

        respondedCountLabel = new JLabel("Responded: 0");
        respondedCountLabel.setFont(AppTheme.FONT_LABEL);
        respondedCountLabel.setForeground(AppTheme.COLOR_YELLOW);

        resolvedCountLabel = new JLabel("Resolved: 0");
        resolvedCountLabel.setFont(AppTheme.FONT_LABEL);
        resolvedCountLabel.setForeground(AppTheme.COLOR_GREEN);

        statsPanel.add(pendingCountLabel);
        statsPanel.add(respondedCountLabel);
        statsPanel.add(resolvedCountLabel);
        yPos += 75;


        JLabel pendingLabel = ComponentFactory.createHeaderLabel(
                "📋 Pending Help Requests", AppTheme.COLOR_ORANGE);
        pendingLabel.setBounds(20, yPos, 600, 25);
        yPos += 30;

        pendingPanel = new JPanel();
        pendingPanel.setLayout(new BoxLayout(pendingPanel, BoxLayout.Y_AXIS));
        pendingPanel.setBackground(AppTheme.BG_DARK);
        pendingPanel.setOpaque(false);
        pendingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane pendingScroll = UIUtils.createThemedScrollPane(pendingPanel);
        pendingScroll.setBounds(20, yPos, 860, 130);
        yPos += 145;


        JLabel respondedLabel = ComponentFactory.createHeaderLabel(
                "⚡ Responded Requests", AppTheme.COLOR_YELLOW);
        respondedLabel.setBounds(20, yPos, 600, 25);
        yPos += 30;

        respondedPanel = new JPanel();
        respondedPanel.setLayout(new BoxLayout(respondedPanel, BoxLayout.Y_AXIS));
        respondedPanel.setBackground(AppTheme.BG_DARK);
        respondedPanel.setOpaque(false);
        respondedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane respondedScroll = UIUtils.createThemedScrollPane(respondedPanel);
        respondedScroll.setBounds(20, yPos, 860, 130);
        yPos += 145;


        JLabel resolvedLabel = ComponentFactory.createHeaderLabel(
                "✅ Resolved Requests", AppTheme.COLOR_GREEN);
        resolvedLabel.setBounds(20, yPos, 600, 25);
        yPos += 30;

        resolvedPanel = new JPanel();
        resolvedPanel.setLayout(new BoxLayout(resolvedPanel, BoxLayout.Y_AXIS));
        resolvedPanel.setBackground(AppTheme.BG_DARK);
        resolvedPanel.setOpaque(false);
        resolvedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane resolvedScroll = UIUtils.createThemedScrollPane(resolvedPanel);
        resolvedScroll.setBounds(20, yPos, 860, 100);
        yPos += 115;


        JButton refreshBtn = ComponentFactory.createSuccessButton(
                "🔄 REFRESH", this::refreshAllRequests);
        refreshBtn.setBounds(20, yPos, 140, 40);

        JButton backDashBtn = ComponentFactory.createPrimaryButton(
                "↶ BACK", () -> mainFrame.showDashboard(admin));
        backDashBtn.setBounds(740, yPos, 140, 40);

        add(backBtn);
        add(headerLabel);
        add(statsPanel);
        add(pendingLabel);
        add(pendingScroll);
        add(respondedLabel);
        add(respondedScroll);
        add(resolvedLabel);
        add(resolvedScroll);
        add(refreshBtn);
        add(backDashBtn);
    }


    private void refreshAllRequests() {
        Logger.debug(CLASS_NAME, "Refreshing all help requests");
        List<HelpRequest> allRequests = helpRequestDAO.getAllHelpRequests();

        List<HelpRequest> pending = allRequests.stream()
                .filter(r -> "pending".equals(r.getStatus()))
                .toList();

        List<HelpRequest> responded = allRequests.stream()
                .filter(r -> "responded".equals(r.getStatus()) || "in_progress".equals(r.getStatus()))
                .toList();

        List<HelpRequest> resolved = allRequests.stream()
                .filter(r -> "resolved".equals(r.getStatus()))
                .toList();

        updateCounts(pending.size(), responded.size(), resolved.size());
        updatePanel(pendingPanel, pending, "pending");
        updatePanel(respondedPanel, responded, "responded");
        updatePanel(resolvedPanel, resolved, "resolved");

        Logger.info(CLASS_NAME, "Requests refreshed - Pending: " + pending.size());
    }

    private void updateCounts(int p, int r, int res) {
        pendingCountLabel.setText("Pending: " + p);
        respondedCountLabel.setText("Responded: " + r);
        resolvedCountLabel.setText("Resolved: " + res);
    }

    private void updatePanel(JPanel panel, List<HelpRequest> requests, String type) {
        panel.removeAll();
        if (requests.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No " + type + " requests");
            emptyLabel.setFont(AppTheme.FONT_REGULAR);
            emptyLabel.setForeground(AppTheme.COLOR_GREEN);
            panel.add(emptyLabel);
        } else {
            for (HelpRequest req : requests) {
                JPanel card = createRequestCard(req, type);
                card.setMaximumSize(new Dimension(840, 110));
                card.setPreferredSize(new Dimension(840, 110));
                panel.add(card);
                panel.add(Box.createVerticalStrut(10));
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createRequestCard(HelpRequest req, String type) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color borderColor = "pending".equals(type) ? AppTheme.COLOR_ORANGE :
                        "responded".equals(type) ? AppTheme.COLOR_YELLOW :
                                AppTheme.COLOR_GREEN;

                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel(getHelpTypeIcon(req.getRequestType()));
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setBounds(12, 15, 40, 40);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(AppTheme.FONT_LABEL);
        nameLabel.setForeground(AppTheme.TEXT_WHITE);
        nameLabel.setBounds(60, 10, 400, 20);

        JLabel descLabel = new JLabel(req.getDescription());
        descLabel.setFont(AppTheme.FONT_REGULAR);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(60, 35, 600, 20);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(AppTheme.FONT_SMALL);
        locLabel.setForeground(AppTheme.TEXT_SECONDARY);
        descLabel.setBounds(60, 60, 700, 15);

        if ("pending".equals(type)) {
            JButton assignBtn = ComponentFactory.createPrimaryButton("ASSIGN",
                    () -> handleAssign(req));
            assignBtn.setBounds(750, 30, 70, 35);
            card.add(assignBtn);
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

    private void handleAssign(HelpRequest req) {
        Logger.debug(CLASS_NAME, "Assigning request: " + req.getId());
        if (helpRequestDAO.updateRequestStatus(req.getId(), "in_progress")) {
            UIUtils.showInfo(this, "Success", "✅ Request assigned!");
            refreshAllRequests();
        }
    }
}