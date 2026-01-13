package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.User;
import models.HelpRequest;
import dao.HelpRequestDAO;

public class AdminHelpRequestManagement extends JPanel {
    private User admin;
    private MainFrame mainFrame;
    private HelpRequestDAO helpRequestDAO = new HelpRequestDAO();
    private JPanel pendingPanel;
    private JPanel respondedPanel;
    private JPanel resolvedPanel;
    private JLabel pendingCountLabel;
    private JLabel respondedCountLabel;
    private JLabel resolvedCountLabel;

    public AdminHelpRequestManagement(User admin, MainFrame mainFrame) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
        refreshAllRequests();
    }

    private void initUI() {
        int yPos = 15;

        JButton backBtn = createIconButton("←", new Color(80, 100, 140));
        backBtn.setBounds(20, yPos, 35, 35);
        backBtn.addActionListener(e -> mainFrame.showDashboard(admin));

        JLabel headerLabel = new JLabel("🛡️ ADMIN HELP REQUEST MANAGEMENT");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(255, 150, 100));
        headerLabel.setBounds(70, yPos, 800, 40);

        yPos += 50;

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 10));
        statsPanel.setBackground(new Color(40, 60, 100));
        statsPanel.setBounds(20, yPos, 860, 60);
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));

        pendingCountLabel = new JLabel("Pending: 0");
        pendingCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pendingCountLabel.setForeground(new Color(255, 150, 100));

        respondedCountLabel = new JLabel("Responded: 0");
        respondedCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        respondedCountLabel.setForeground(new Color(255, 200, 100));

        resolvedCountLabel = new JLabel("Resolved: 0");
        resolvedCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resolvedCountLabel.setForeground(new Color(100, 255, 100));

        statsPanel.add(pendingCountLabel);
        statsPanel.add(respondedCountLabel);
        statsPanel.add(resolvedCountLabel);

        yPos += 75;

        JLabel pendingLabel = new JLabel("📋 Pending Help Requests (Waiting for response)");
        pendingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pendingLabel.setForeground(new Color(255, 150, 100));
        pendingLabel.setBounds(20, yPos, 600, 25);

        yPos += 30;

        pendingPanel = new JPanel();
        pendingPanel.setLayout(new BoxLayout(pendingPanel, BoxLayout.Y_AXIS));
        pendingPanel.setBackground(new Color(20, 25, 47));
        pendingPanel.setOpaque(false);
        pendingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane pendingScroll = new JScrollPane(pendingPanel);
        pendingScroll.setBounds(20, yPos, 860, 110);
        pendingScroll.setBackground(new Color(20, 25, 47));
        pendingScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        pendingScroll.getVerticalScrollBar().setUnitIncrement(20);

        yPos += 125;

        JLabel respondedLabel = new JLabel("📊 Responded Requests (Being handled)");
        respondedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        respondedLabel.setForeground(new Color(255, 200, 100));
        respondedLabel.setBounds(20, yPos, 600, 25);

        yPos += 30;

        respondedPanel = new JPanel();
        respondedPanel.setLayout(new BoxLayout(respondedPanel, BoxLayout.Y_AXIS));
        respondedPanel.setBackground(new Color(20, 25, 47));
        respondedPanel.setOpaque(false);
        respondedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane respondedScroll = new JScrollPane(respondedPanel);
        respondedScroll.setBounds(20, yPos, 860, 110);
        respondedScroll.setBackground(new Color(20, 25, 47));
        respondedScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        respondedScroll.getVerticalScrollBar().setUnitIncrement(20);

        yPos += 125;

        JLabel resolvedLabel = new JLabel("✅ Resolved Requests (Completed)");
        resolvedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resolvedLabel.setForeground(new Color(100, 255, 150));
        resolvedLabel.setBounds(20, yPos, 600, 25);

        yPos += 30;

        resolvedPanel = new JPanel();
        resolvedPanel.setLayout(new BoxLayout(resolvedPanel, BoxLayout.Y_AXIS));
        resolvedPanel.setBackground(new Color(20, 25, 47));
        resolvedPanel.setOpaque(false);
        resolvedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane resolvedScroll = new JScrollPane(resolvedPanel);
        resolvedScroll.setBounds(20, yPos, 860, 80);
        resolvedScroll.setBackground(new Color(20, 25, 47));
        resolvedScroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        resolvedScroll.getVerticalScrollBar().setUnitIncrement(20);

        yPos += 95;

        JButton refreshBtn = createStyledButton("🔄 REFRESH", new Color(150, 255, 150));
        refreshBtn.setBounds(20, yPos, 140, 40);
        refreshBtn.addActionListener(e -> refreshAllRequests());

        JButton backDashBtn = createStyledButton("← BACK", new Color(100, 200, 255));
        backDashBtn.setBounds(740, yPos, 140, 40);
        backDashBtn.addActionListener(e -> mainFrame.showDashboard(admin));

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

        pendingCountLabel.setText("Pending: " + pending.size());
        respondedCountLabel.setText("Responded: " + responded.size());
        resolvedCountLabel.setText("Resolved: " + resolved.size());

        pendingPanel.removeAll();
        if (pending.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No pending requests");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
            pendingPanel.add(emptyLabel);
        } else {
            for (HelpRequest req : pending) {
                JPanel card = createPendingCard(req);
                card.setMaximumSize(new Dimension(840, 90));
                card.setPreferredSize(new Dimension(840, 90));
                pendingPanel.add(card);
                pendingPanel.add(Box.createVerticalStrut(8));
            }
        }
        pendingPanel.revalidate();
        pendingPanel.repaint();

        respondedPanel.removeAll();
        if (responded.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No responded requests");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
            respondedPanel.add(emptyLabel);
        } else {
            for (HelpRequest req : responded) {
                JPanel card = createRespondedCard(req);
                card.setMaximumSize(new Dimension(840, 90));
                card.setPreferredSize(new Dimension(840, 90));
                respondedPanel.add(card);
                respondedPanel.add(Box.createVerticalStrut(8));
            }
        }
        respondedPanel.revalidate();
        respondedPanel.repaint();

        resolvedPanel.removeAll();
        if (resolved.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No resolved requests yet");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
            resolvedPanel.add(emptyLabel);
        } else {
            for (HelpRequest req : resolved) {
                JPanel card = createResolvedCard(req);
                card.setMaximumSize(new Dimension(840, 80));
                card.setPreferredSize(new Dimension(840, 80));
                resolvedPanel.add(card);
                resolvedPanel.add(Box.createVerticalStrut(6));
            }
        }
        resolvedPanel.revalidate();
        resolvedPanel.repaint();
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
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setBounds(12, 15, 55, 55);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(75, 10, 380, 20);

        JLabel descLabel = new JLabel(req.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(75, 32, 450, 15);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(75, 50, 500, 15);


        JButton assignBtn = createStyledButton("ASSIGN", new Color(100, 200, 255));
        assignBtn.setBounds(750, 25, 70, 35);
        assignBtn.setFont(new Font("Arial", Font.BOLD, 10));
        assignBtn.addActionListener(e -> {

            boolean updated = helpRequestDAO.updateRequestStatus(req.getId(), "in_progress");
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "✅ Request assigned to responders!\nStatus: In Progress",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllRequests();
            }
        });

        card.add(iconLabel);
        card.add(nameLabel);
        card.add(descLabel);
        card.add(locLabel);
        card.add(assignBtn);

        return card;
    }


    private JPanel createRespondedCard(HelpRequest req) {
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
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setBounds(12, 15, 55, 55);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(75, 10, 380, 20);

        JLabel descLabel = new JLabel("Status: Being handled by responder...");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setBounds(75, 32, 450, 15);

        JLabel locLabel = new JLabel("📍 " + req.getLocation() + " | 📞 " + req.getContactNumber());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(75, 50, 500, 15);

        JButton resolveBtn = createStyledButton("RESOLVE", new Color(100, 255, 100));
        resolveBtn.setBounds(750, 25, 70, 35);
        resolveBtn.setFont(new Font("Arial", Font.BOLD, 10));
        resolveBtn.addActionListener(e -> {
            boolean updated = helpRequestDAO.updateRequestStatus(req.getId(), "resolved");
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "✅ Request marked as resolved!",
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


    private JPanel createResolvedCard(HelpRequest req) {
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
        iconLabel.setFont(new Font("Arial", Font.BOLD, 28));
        iconLabel.setForeground(new Color(100, 255, 150));
        iconLabel.setBounds(12, 12, 45, 45);

        JLabel nameLabel = new JLabel(req.getUsername() + " - " + req.getRequestType());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(65, 10, 350, 18);

        JLabel locLabel = new JLabel("📍 " + req.getLocation());
        locLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        locLabel.setForeground(new Color(150, 200, 255));
        locLabel.setBounds(65, 30, 400, 14);

        JLabel timeLabel = new JLabel("Time: " + req.getTimestamp());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 8));
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setBounds(65, 46, 400, 12);

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