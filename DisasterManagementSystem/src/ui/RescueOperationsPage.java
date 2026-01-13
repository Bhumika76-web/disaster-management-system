package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.User;
import models.ResponderTask;
import dao.ResponderTaskDAO;
import dao.UserDAO;

public class RescueOperationsPage extends JPanel {
    private User user;
    private MainFrame mainFrame;
    private ResponderTaskDAO responderTaskDAO = new ResponderTaskDAO();
    private UserDAO userDAO = new UserDAO();
    private JPanel activeTasksPanel;
    private JPanel mapPanel;
    private boolean isAdmin;
    private boolean isResponder;
    private JTabbedPane tabbedPane;

    public RescueOperationsPage(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.isAdmin = "admin".equals(user.getUserType());
        this.isResponder = "responder".equals(user.getUserType());

        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initUI();
        refreshOperations();
    }

    private void initUI() {
        int yPos = 15;

        JButton backBtn = createIconButton("←", new Color(80, 100, 140));
        backBtn.setBounds(20, yPos, 35, 35);
        backBtn.addActionListener(e -> mainFrame.showDashboard(user));

        JLabel headerLabel = new JLabel("Rescue Operations");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(70, yPos, 400, 35);

        yPos += 50;

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(20, 25, 47));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setBounds(20, yPos, 860, 650);

        if (isAdmin) {
            JPanel taskAssignmentTab = createTaskAssignmentTab();
            tabbedPane.addTab("Task Assignment", taskAssignmentTab);
        }

        JPanel activeOpsTab = createActiveOperationsTab();
        tabbedPane.addTab("Active Operations", activeOpsTab);

        add(backBtn);
        add(headerLabel);
        add(tabbedPane);
    }

    private JPanel createTaskAssignmentTab() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(20, 25, 47));

        int yPos = 15;

        JLabel assignTitle = new JLabel("Assign Rescue Tasks");
        assignTitle.setFont(new Font("Arial", Font.BOLD, 16));
        assignTitle.setForeground(new Color(100, 200, 255));
        assignTitle.setBounds(20, yPos, 300, 25);

        yPos += 35;

        JLabel zoneLabel = new JLabel("Select Zone:");
        zoneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        zoneLabel.setForeground(Color.WHITE);
        zoneLabel.setBounds(20, yPos, 150, 20);

        JComboBox<String> zoneCombo = createStyledCombo(new String[]{
                "Select Zone", "Coastal Area", "Mountain Region", "Urban Area",
                "Rural Area", "Flood Zone", "Earthquake Zone"
        });
        zoneCombo.setBounds(20, yPos + 25, 400, 40);

        yPos += 75;

        JLabel responderLabel = new JLabel("Select Responder:");
        responderLabel.setFont(new Font("Arial", Font.BOLD, 12));
        responderLabel.setForeground(Color.WHITE);
        responderLabel.setBounds(20, yPos, 150, 20);

        List<User> responders = userDAO.getAllResponders();
        String[] responderOptions = new String[responders.size() + 1];
        responderOptions[0] = "Select Responder";
        for (int i = 0; i < responders.size(); i++) {
            responderOptions[i + 1] = responders.get(i).getUsername() + " (ID: " + responders.get(i).getId() + ")";
        }

        JComboBox<String> responderCombo = createStyledCombo(responderOptions);
        responderCombo.setBounds(20, yPos + 25, 400, 40);

        yPos += 75;

        JLabel taskLabel = new JLabel("Task Description:");
        taskLabel.setFont(new Font("Arial", Font.BOLD, 12));
        taskLabel.setForeground(Color.WHITE);
        taskLabel.setBounds(20, yPos, 150, 20);

        JTextArea taskDesc = new JTextArea();
        taskDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        taskDesc.setBackground(new Color(40, 55, 85));
        taskDesc.setForeground(Color.WHITE);
        taskDesc.setLineWrap(true);
        taskDesc.setWrapStyleWord(true);
        taskDesc.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 120), 1));
        taskDesc.setBounds(20, yPos + 25, 400, 100);

        yPos += 135;

        JButton assignBtn = createStyledButton("Assign Task", new Color(100, 255, 100));
        assignBtn.setBounds(20, yPos, 400, 50);
        assignBtn.setFont(new Font("Arial", Font.BOLD, 14));
        assignBtn.addActionListener(e -> {
            String zone = (String) zoneCombo.getSelectedItem();
            String responderStr = (String) responderCombo.getSelectedItem();
            String task = taskDesc.getText().trim();

            if (zone.equals("Select Zone") || responderStr.equals("Select Responder") || task.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int responderId = Integer.parseInt(responderStr.split("ID: ")[1].replace(")", ""));

            ResponderTask respTask = new ResponderTask(responderId, zone, task);
            if (responderTaskDAO.assignTask(respTask)) {
                JOptionPane.showMessageDialog(this,
                        "✅ Task Assigned Successfully!\n\nZone: " + zone + "\nTask: " + task,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                zoneCombo.setSelectedIndex(0);
                responderCombo.setSelectedIndex(0);
                taskDesc.setText("");
                refreshOperations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign task",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(assignTitle);
        panel.add(zoneLabel);
        panel.add(zoneCombo);
        panel.add(responderLabel);
        panel.add(responderCombo);
        panel.add(taskLabel);
        panel.add(taskDesc);
        panel.add(assignBtn);

        return panel;
    }

    private JPanel createActiveOperationsTab() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(20, 25, 47));

        int yPos = 15;

        JLabel opsTitle = new JLabel("Active Rescue Operations");
        opsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        opsTitle.setForeground(new Color(100, 200, 255));
        opsTitle.setBounds(20, yPos, 400, 25);

        yPos += 35;

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(40, 60, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(new Color(100, 150, 200));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("📍 Rescue Operations Map", 20, 30);

                List<String> activeZones = responderTaskDAO.getActiveZones();
                int yPos = 60;

                if (activeZones.isEmpty()) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.setColor(new Color(100, 200, 100));
                    g2d.drawString("No active rescue operations assigned", 20, yPos);
                } else {
                    for (String zone : activeZones) {
                        int responderCount = responderTaskDAO.getResponderCountByZone(zone);
                        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                        g2d.setColor(new Color(150, 200, 255));
                        g2d.drawString(zone + ": " + responderCount + " responder(s) active", 20, yPos);
                        yPos += 30;
                    }
                }
            }
        };
        mapPanel.setBounds(20, yPos, 820, 180);
        mapPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));

        yPos += 200;

        JLabel tasksTitle = new JLabel("📋 Tasks in Progress");
        tasksTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tasksTitle.setForeground(new Color(255, 200, 100));
        tasksTitle.setBounds(20, yPos, 300, 25);

        yPos += 30;

        activeTasksPanel = new JPanel();
        activeTasksPanel.setLayout(new BoxLayout(activeTasksPanel, BoxLayout.Y_AXIS));
        activeTasksPanel.setBackground(new Color(20, 25, 47));
        activeTasksPanel.setOpaque(false);
        activeTasksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(activeTasksPanel);
        scroll.setBounds(20, yPos, 820, 150);
        scroll.setBackground(new Color(20, 25, 47));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
        scroll.getVerticalScrollBar().setUnitIncrement(15);

        panel.add(opsTitle);
        panel.add(mapPanel);
        panel.add(tasksTitle);
        panel.add(scroll);

        return panel;
    }

    private void refreshOperations() {

        if (mapPanel != null) {
            mapPanel.repaint();
        }

        activeTasksPanel.removeAll();
        List<ResponderTask> activeTasks = responderTaskDAO.getActiveZones().stream()
                .flatMap(zone -> responderTaskDAO.getTasksByZone(zone).stream())
                .toList();

        if (activeTasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No active rescue tasks");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(100, 200, 100));
            activeTasksPanel.add(emptyLabel);
        } else {
            for (ResponderTask task : activeTasks) {
                JPanel taskCard = createTaskCard(task);
                taskCard.setMaximumSize(new Dimension(800, 85));
                taskCard.setPreferredSize(new Dimension(800, 85));
                activeTasksPanel.add(taskCard);
                activeTasksPanel.add(Box.createVerticalStrut(8));
            }
        }

        activeTasksPanel.revalidate();
        activeTasksPanel.repaint();
    }


    private JPanel createTaskCard(ResponderTask task) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = "active".equals(task.getStatus()) ?
                        new Color(60, 80, 120) :
                        "in_progress".equals(task.getStatus()) ?
                                new Color(80, 90, 120) :
                                new Color(70, 100, 100);

                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor = "active".equals(task.getStatus()) ?
                        new Color(255, 200, 100) :
                        "in_progress".equals(task.getStatus()) ?
                                new Color(100, 200, 255) :
                                new Color(100, 255, 150);

                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel zoneLabel = new JLabel(task.getZone());
        zoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        zoneLabel.setForeground(Color.WHITE);
        zoneLabel.setBounds(15, 10, 150, 20);

        JLabel taskLabel = new JLabel(task.getTaskDescription());
        taskLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        taskLabel.setForeground(new Color(200, 200, 200));
        taskLabel.setBounds(15, 32, 400, 18);

        JLabel statusLabel = new JLabel("Status: " + task.getStatus().toUpperCase());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusLabel.setForeground(new Color(255, 200, 100));
        statusLabel.setBounds(15, 52, 200, 15);

        JLabel timeLabel = new JLabel("Assigned: " + task.getAssignedAt());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        timeLabel.setForeground(new Color(150, 150, 150));
        timeLabel.setBounds(220, 52, 300, 15);

        card.add(zoneLabel);
        card.add(taskLabel);
        card.add(statusLabel);
        card.add(timeLabel);

        if (isAdmin || isResponder) {
            if ("active".equals(task.getStatus())) {

                JButton startBtn = createStyledButton("START", new Color(100, 200, 255));
                startBtn.setBounds(670, 20, 100, 45);
                startBtn.setFont(new Font("Arial", Font.BOLD, 11));
                startBtn.addActionListener(e -> {
                    if (responderTaskDAO.updateTaskStatus(task.getId(), "in_progress")) {
                        JOptionPane.showMessageDialog(this, "✅ Task started!\nStatus: In Progress",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshOperations();
                    }
                });
                card.add(startBtn);
            } else if ("in_progress".equals(task.getStatus())) {

                JButton completeBtn = createStyledButton("COMPLETE", new Color(100, 255, 100));
                completeBtn.setBounds(670, 20, 100, 45);
                completeBtn.setFont(new Font("Arial", Font.BOLD, 11));
                completeBtn.addActionListener(e -> {
                    if (responderTaskDAO.updateTaskStatus(task.getId(), "completed")) {
                        JOptionPane.showMessageDialog(this, "✅ Task completed!\n\nGreat work!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshOperations();
                    }
                });
                card.add(completeBtn);
            }
        }

        return card;
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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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