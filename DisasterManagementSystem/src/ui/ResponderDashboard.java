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


public class ResponderDashboard extends JPanel {

    private static final String CLASS_NAME = "ResponderDashboard";

    private User responder;
    private MainFrame mainFrame;
    private HelpRequestDAO helpRequestDAO;
    private JPanel pendingRequestsPanel;
    private JPanel inProgressPanel;
    private JPanel completedPanel;
    private JLabel pendingCountLabel;
    private JLabel inProgressCountLabel;
    private JLabel completedCountLabel;

    public ResponderDashboard(User responder, MainFrame mainFrame) {
        this.responder = responder;
        this.mainFrame = mainFrame;
        this.helpRequestDAO = new HelpRequestDAO();

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing ResponderDashboard for: " + responder.getUsername());
        initUI();
        refreshAllRequests();
    }


    private void initUI() {
        int yPos = 15;


        JLabel headerLabel = ComponentFactory.createTitleLabel(
                "🚨 RESPONDER DASHBOARD", AppTheme.COLOR_ORANGE);
        headerLabel.setBounds(20, yPos, 800, 40);
        yPos += 50;


        JLabel userLabel = new JLabel(
                "Welcome, " + responder.getUsername() + " (Responder) - Location: " + responder.getLocation());
        userLabel.setFont(AppTheme.FONT_LABEL);
        userLabel.setForeground(AppTheme.TEXT_SECONDARY);
        userLabel.setBounds(20, yPos, 700, 25);
        yPos += 35;


        JPanel statsPanel = createStatsPanel();
        statsPanel.setBounds(20, yPos, 860, 60);
        yPos += 75;


        JLabel pendingLabel = ComponentFactory.createHeaderLabel(
                "📋 Pending Help Requests (Click ACCEPT to respond)", AppTheme.COLOR_ORANGE);
        pendingLabel.setBounds(20, yPos, 600, 25);
        yPos += 30;

        pendingRequestsPanel = new JPanel();
        pendingRequestsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pendingRequestsPanel.setBackground(AppTheme.BG_DARK);
        pendingRequestsPanel.setOpaque(false);

        JScrollPane pendingScroll = UIUtils.createThemedScrollPane(pendingRequestsPanel);
        pendingScroll.setBounds(20, yPos, 860, 130);
        yPos += 145;


        JLabel inProgressLabel = ComponentFactory.createHeaderLabel(
                "⚡ In Progress (Click RESOLVE when done)", AppTheme.COLOR_YELLOW);
        inProgressLabel.setBounds(20, yPos, 600, 25);
        yPos += 30;

        inProgressPanel = new JPanel();
        inProgressPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inProgressPanel.setBackground(AppTheme.BG_DARK);
        inProgressPanel.setOpaque(false);

        JScrollPane inProgressScroll = UIUtils.createThemedScrollPane(inProgressPanel);
        inProgressScroll.setBounds(20, yPos, 860, 130);
        yPos += 145;


        JLabel completedLabel = ComponentFactory.createHeaderLabel(
                "✅ Completed Today", AppTheme.COLOR_GREEN);
        completedLabel.setBounds(20, yPos, 600, 25);
        yPos += 30;

        completedPanel = new JPanel();
        completedPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        completedPanel.setBackground(AppTheme.BG_DARK);
        completedPanel.setOpaque(false);

        JScrollPane completedScroll = UIUtils.createThemedScrollPane(completedPanel);
        completedScroll.setBounds(20, yPos, 860, 110);
        yPos += 125;


        JButton refreshBtn = ComponentFactory.createSuccessButton(
                "🔄 REFRESH", this::refreshAllRequests);
        refreshBtn.setBounds(20, yPos, 140, 40);

        JButton rescueBtn = ComponentFactory.createPrimaryButton(
                "🚑 RESCUE OPS", () -> mainFrame.showRescueOperations(responder));
        rescueBtn.setBounds(170, yPos, 140, 40);

        JButton logoutBtn = ComponentFactory.createDangerButton(
                "🚪 LOGOUT", () -> mainFrame.logout());
        logoutBtn.setBounds(740, yPos, 140, 40);


        add(headerLabel);
        add(userLabel);
        add(statsPanel);
        add(pendingLabel);
        add(pendingScroll);
        add(inProgressLabel);
        add(inProgressScroll);
        add(completedLabel);
        add(completedScroll);
        add(refreshBtn);
        add(rescueBtn);
        add(logoutBtn);
    }


    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 10));
        panel.setBackground(AppTheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));

        pendingCountLabel = new JLabel("Pending: 0");
        pendingCountLabel.setFont(AppTheme.FONT_LABEL);
        pendingCountLabel.setForeground(AppTheme.COLOR_ORANGE);

        inProgressCountLabel = new JLabel("In Progress: 0");
        inProgressCountLabel.setFont(AppTheme.FONT_LABEL);
        inProgressCountLabel.setForeground(AppTheme.COLOR_YELLOW);

        completedCountLabel = new JLabel("Completed: 0");
        completedCountLabel.setFont(AppTheme.FONT_LABEL);
        completedCountLabel.setForeground(AppTheme.COLOR_GREEN);

        panel.add(pendingCountLabel);
        panel.add(inProgressCountLabel);
        panel.add(completedCountLabel);

        return panel;
    }


    private void refreshAllRequests() {
        Logger.debug(CLASS_NAME, "Refreshing all help requests");
        List<HelpRequest> allRequests = helpRequestDAO.getAllHelpRequests();

        List<HelpRequest> pending = allRequests.stream()
                .filter(r -> "pending".equals(r.getStatus()))
                .toList();

        List<HelpRequest> inProgress = allRequests.stream()
                .filter(r -> "in_progress".equals(r.getStatus()))
                .toList();

        List<HelpRequest> resolved = allRequests.stream()
                .filter(r -> "resolved".equals(r.getStatus()))
                .toList();


        pendingCountLabel.setText("Pending: " + pending.size());
        inProgressCountLabel.setText("In Progress: " + inProgress.size());
        completedCountLabel.setText("Completed: " + resolved.size());


        pendingRequestsPanel.removeAll();
        if (pending.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No pending requests");
            emptyLabel.setFont(AppTheme.FONT_REGULAR);
            emptyLabel.setForeground(AppTheme.COLOR_GREEN);
            pendingRequestsPanel.add(emptyLabel);
        } else {
            for (HelpRequest req : pending) {
                JPanel card = createPendingCard(req);
                card.setPreferredSize(new Dimension(830, 110));
                pendingRequestsPanel.add(card);
            }
        }
        pendingRequestsPanel.revalidate();
        pendingRequestsPanel.repaint();


        inProgressPanel.removeAll();
        if (inProgress.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No in-progress requests");
            emptyLabel.setFont(AppTheme.FONT_REGULAR);
            emptyLabel.setForeground(AppTheme.COLOR_GREEN);
            inProgressPanel.add(emptyLabel);
        } else {
            for (HelpRequest req : inProgress) {
                JPanel card = createInProgressCard(req);
                card.setPreferredSize(new Dimension(830, 110));
                inProgressPanel.add(card);
            }
        }
        inProgressPanel.revalidate();
        inProgressPanel.repaint();


        completedPanel.removeAll();
        if (resolved.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No completed requests yet");
            emptyLabel.setFont(AppTheme.FONT_REGULAR);
            emptyLabel.setForeground(AppTheme.COLOR_GREEN);
            completedPanel.add(emptyLabel);
        } else {
            for (HelpRequest req : resolved) {
                JPanel card = createCompletedCard(req);
                card.setPreferredSize(new Dimension(830, 90));
                completedPanel.add(card);
            }
        }
        completedPanel.revalidate();
        completedPanel.repaint();

        Logger.info(CLASS_NAME, "Requests refreshed - Pending: " + pending.size() +
                ", InProgress: " + inProgress.size() + ", Resolved: " + resolved.size());
    }


    private JPanel createPendingCard(HelpRequest req) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.COLOR_ORANGE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel(getHelpTypeIcon(req.getRequestType()));
        iconLabel.setFont(new Font("Arial", Font.BOLD, 40));
        iconLabel.setBounds(12, 22, 60, 60);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(AppTheme.FONT_LABEL);
        nameLabel.setForeground(AppTheme.TEXT_WHITE);
        nameLabel.setBounds(80, 12, 400, 22);

        JLabel descLabel = new JLabel(req.getDescription());
        descLabel.setFont(AppTheme.FONT_REGULAR);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(80, 38, 500, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(AppTheme.FONT_SMALL);
        locLabel.setForeground(AppTheme.TEXT_SECONDARY);
        locLabel.setBounds(80, 60, 550, 18);

        JButton acceptBtn = ComponentFactory.createSuccessButton(
                "ACCEPT", () -> handleAcceptRequest(req));
        acceptBtn.setBounds(730, 30, 80, 40);
        acceptBtn.setFont(AppTheme.FONT_REGULAR);

        card.add(iconLabel);
        card.add(nameLabel);
        card.add(descLabel);
        card.add(locLabel);
        card.add(acceptBtn);

        return card;
    }


    private JPanel createInProgressCard(HelpRequest req) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.COLOR_YELLOW);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel("⚡");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 40));
        iconLabel.setBounds(12, 22, 60, 60);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(AppTheme.FONT_LABEL);
        nameLabel.setForeground(AppTheme.TEXT_WHITE);
        nameLabel.setBounds(80, 12, 400, 22);

        JLabel descLabel = new JLabel("Status: Currently Assisting...");
        descLabel.setFont(AppTheme.FONT_REGULAR);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(80, 38, 500, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(AppTheme.FONT_SMALL);
        locLabel.setForeground(AppTheme.TEXT_SECONDARY);
        locLabel.setBounds(80, 60, 550, 18);

        JButton resolveBtn = ComponentFactory.createSuccessButton(
                "RESOLVE", () -> handleResolveRequest(req));
        resolveBtn.setBounds(730, 30, 80, 40);
        resolveBtn.setFont(AppTheme.FONT_REGULAR);

        card.add(iconLabel);
        card.add(nameLabel);
        card.add(descLabel);
        card.add(locLabel);
        card.add(resolveBtn);

        return card;
    }


    private JPanel createCompletedCard(HelpRequest req) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 80, 70));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.COLOR_GREEN);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel("✔️");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setForeground(AppTheme.COLOR_GREEN);
        iconLabel.setBounds(12, 15, 50, 50);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(AppTheme.FONT_REGULAR);
        nameLabel.setForeground(AppTheme.TEXT_WHITE);
        nameLabel.setBounds(70, 12, 400, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation());
        locLabel.setFont(AppTheme.FONT_SMALL);
        locLabel.setForeground(AppTheme.TEXT_SECONDARY);
        locLabel.setBounds(70, 32, 400, 14);

        JLabel timeLabel = new JLabel("Time: " + req.getTimestamp());
        timeLabel.setFont(AppTheme.FONT_SMALL);
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setBounds(70, 50, 400, 12);

        card.add(iconLabel);
        card.add(nameLabel);
        card.add(locLabel);
        card.add(timeLabel);

        return card;
    }


    private void handleAcceptRequest(HelpRequest req) {
        Logger.debug(CLASS_NAME, "Accepting request: " + req.getId());
        if (helpRequestDAO.updateRequestStatus(req.getId(), "in_progress")) {
            Logger.info(CLASS_NAME, "Request accepted - ID: " + req.getId());
            UIUtils.showInfo(this, "Success",
                    "✅ ACCEPTED!\n\nResponding to: " + req.getUsername() +
                            "\nLocation: " + req.getLocation() +
                            "\nStatus: Now IN PROGRESS");
            refreshAllRequests();
        }
    }


    private void handleResolveRequest(HelpRequest req) {
        Logger.debug(CLASS_NAME, "Resolving request: " + req.getId());
        if (helpRequestDAO.updateRequestStatus(req.getId(), "resolved")) {
            Logger.info(CLASS_NAME, "Request resolved - ID: " + req.getId());
            UIUtils.showInfo(this, "Success",
                    "✅ COMPLETED!\n\nThank you for helping: " + req.getUsername() +
                            "\nRequest marked RESOLVED");
            refreshAllRequests();
        }
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
}