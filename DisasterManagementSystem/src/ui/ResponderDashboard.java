package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.User;
import models.HelpRequest;
import dao.HelpRequestDAO;

public class ResponderDashboard extends JPanel {
    private User responder;
    private MainFrame mainFrame;
    private HelpRequestDAO helpRequestDAO = new HelpRequestDAO();
    private JPanel pendingRequestsPanel;
    private JPanel inProgressPanel;
    private JPanel completedPanel;
    private JLabel pendingCountLabel;
    private JLabel inProgressCountLabel;
    private JLabel completedCountLabel;

    public ResponderDashboard(User responder, MainFrame mainFrame) {
        this.responder = responder;
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
        refreshAllRequests();
    }

    private void initUI() {
        int yPos = 15;

        JLabel headerLabel = new JLabel("🚨 RESPONDER DASHBOARD");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(255, 150, 100));
        headerLabel.setBounds(20, yPos, 800, 40);

        yPos += 50;

        JLabel userLabel = new JLabel("Welcome, " + responder.getUsername() + " (Responder) - Location: " + responder.getLocation());
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(150, 200, 255));
        userLabel.setBounds(20, yPos, 700, 25);

        yPos += 35;

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 10));
        statsPanel.setBackground(new Color(40, 60, 100));
        statsPanel.setBounds(20, yPos, 860, 60);
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));

        pendingCountLabel = new JLabel("Pending: 0");
        pendingCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pendingCountLabel.setForeground(new Color(255, 150, 100));

        inProgressCountLabel = new JLabel("In Progress: 0");
        inProgressCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inProgressCountLabel.setForeground(new Color(255, 200, 100));

        completedCountLabel = new JLabel("Completed: 0");
        completedCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        completedCountLabel.setForeground(new Color(100, 255, 100));

        statsPanel.add(pendingCountLabel);
        statsPanel.add(inProgressCountLabel);
        statsPanel.add(completedCountLabel);

        yPos += 75;

        JLabel pendingLabel = new JLabel("📋 Pending Help Requests (Click ACCEPT to respond)");
        pendingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pendingLabel.setForeground(new Color(255, 150, 100));
        pendingLabel.setBounds(20, yPos, 600, 25);

        yPos += 30;

        pendingRequestsPanel = new JPanel();
        pendingRequestsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pendingRequestsPanel.setBackground(new Color(20, 25, 47));
        pendingRequestsPanel.setOpaque(false);

        JScrollPane pendingScroll = new JScrollPane(pendingRequestsPanel);
        pendingScroll.setBounds(20, yPos, 860, 130);
        pendingScroll.setBackground(new Color(20, 25, 47));
        pendingScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        pendingScroll.getVerticalScrollBar().setUnitIncrement(20);

        yPos += 145;

        JLabel inProgressLabel = new JLabel("📊 In Progress (Click RESOLVE when done)");
        inProgressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inProgressLabel.setForeground(new Color(255, 200, 100));
        inProgressLabel.setBounds(20, yPos, 600, 25);

        yPos += 30;

        inProgressPanel = new JPanel();
        inProgressPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inProgressPanel.setBackground(new Color(20, 25, 47));
        inProgressPanel.setOpaque(false);

        JScrollPane inProgressScroll = new JScrollPane(inProgressPanel);
        inProgressScroll.setBounds(20, yPos, 860, 130);
        inProgressScroll.setBackground(new Color(20, 25, 47));
        inProgressScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        inProgressScroll.getVerticalScrollBar().setUnitIncrement(20);

        yPos += 145;

        JLabel completedLabel = new JLabel("✅ Completed Today");
        completedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        completedLabel.setForeground(new Color(100, 255, 150));
        completedLabel.setBounds(20, yPos, 600, 25);

        yPos += 30;

        completedPanel = new JPanel();
        completedPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        completedPanel.setBackground(new Color(20, 25, 47));
        completedPanel.setOpaque(false);

        JScrollPane completedScroll = new JScrollPane(completedPanel);
        completedScroll.setBounds(20, yPos, 860, 110);
        completedScroll.setBackground(new Color(20, 25, 47));
        completedScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        completedScroll.getVerticalScrollBar().setUnitIncrement(20);

        yPos += 125;

        JButton refreshBtn = createStyledButton("🔄 REFRESH", new Color(150, 255, 150));
        refreshBtn.setBounds(20, yPos, 140, 40);
        refreshBtn.addActionListener(e -> refreshAllRequests());

        JButton rescueBtn = createStyledButton("🚁 RESCUE OPS", new Color(100, 200, 255));
        rescueBtn.setBounds(170, yPos, 140, 40);
        rescueBtn.addActionListener(e -> mainFrame.showRescueOperations(responder));

        JButton logoutBtn = createStyledButton("LOGOUT", new Color(255, 100, 100));
        logoutBtn.setBounds(740, yPos, 140, 40);
        logoutBtn.addActionListener(e -> mainFrame.logout());

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

    private void refreshAllRequests() {
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
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
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
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
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
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
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
    }

    private JPanel createPendingCard(HelpRequest req) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 80, 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(255, 150, 100));
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
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(80, 12, 400, 22);

        JLabel descLabel = new JLabel(req.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(80, 38, 500, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(80, 60, 550, 18);

        JButton acceptBtn = createStyledButton("ACCEPT", new Color(100, 255, 100));
        acceptBtn.setBounds(730, 30, 80, 40);
        acceptBtn.setFont(new Font("Arial", Font.BOLD, 11));
        acceptBtn.addActionListener(e -> {
            boolean updated = helpRequestDAO.updateRequestStatus(req.getId(), "in_progress");
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "✅ ACCEPTED!\n\nResponding to: " + req.getUsername() +
                                "\nLocation: " + req.getLocation() +
                                "\nStatus: Now IN PROGRESS",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllRequests();
            }
        });

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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 80, 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(255, 200, 100));
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
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(80, 12, 400, 22);

        JLabel descLabel = new JLabel("Status: Currently Assisting...");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(80, 38, 500, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(80, 60, 550, 18);

        JButton resolveBtn = createStyledButton("RESOLVE", new Color(100, 255, 100));
        resolveBtn.setBounds(730, 30, 80, 40);
        resolveBtn.setFont(new Font("Arial", Font.BOLD, 11));
        resolveBtn.addActionListener(e -> {
            boolean updated = helpRequestDAO.updateRequestStatus(req.getId(), "resolved");
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "✅ COMPLETED!\n\nThank you for helping: " + req.getUsername() +
                                "\nRequest marked RESOLVED",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllRequests();
            }
        });

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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 80, 70));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(100, 255, 150));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel iconLabel = new JLabel("✔");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setForeground(new Color(100, 255, 150));
        iconLabel.setBounds(12, 15, 50, 50);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(70, 12, 400, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(70, 32, 400, 14);

        JLabel timeLabel = new JLabel("Time: " + req.getTimestamp());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 8));
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setBounds(70, 50, 400, 12);

        card.add(iconLabel);
        card.add(nameLabel);
        card.add(locLabel);
        card.add(timeLabel);

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

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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
}