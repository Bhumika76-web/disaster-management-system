package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.User;
import models.ResponderTask;
import dao.ResponderTaskDAO;
import dao.UserDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;
import config.AppConstants;


public class RescueOperationsPage extends JPanel {

    private static final String CLASS_NAME = "RescueOperationsPage";

    private User user;
    private MainFrame mainFrame;
    private ResponderTaskDAO responderTaskDAO;
    private UserDAO userDAO;
    private JPanel activeTasksPanel;
    private boolean isAdmin;
    private boolean isResponder;
    private JTabbedPane tabbedPane;

    public RescueOperationsPage(User user, MainFrame mainFrame) {
        this.user = user;
        this.mainFrame = mainFrame;
        this.responderTaskDAO = new ResponderTaskDAO();
        this.userDAO = new UserDAO();
        this.isAdmin = AppConstants.USER_TYPE_ADMIN.equals(user.getUserType());
        this.isResponder = AppConstants.USER_TYPE_RESPONDER.equals(user.getUserType());

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing RescueOperationsPage");
        initUI();
        refreshOperations();
    }


    private void initUI() {
        int yPos = 15;

        JButton backBtn = ComponentFactory.createPrimaryButton("↶", () -> mainFrame.showDashboard(user));
        backBtn.setBounds(20, yPos, 35, 35);

        JLabel headerLabel = ComponentFactory.createTitleLabel(
                "🚑 Rescue Operations", AppTheme.TEXT_WHITE);
        headerLabel.setBounds(70, yPos, 400, 35);
        yPos += 50;

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(AppTheme.BG_DARK);
        tabbedPane.setForeground(AppTheme.TEXT_WHITE);
        tabbedPane.setFont(AppTheme.FONT_LABEL);
        tabbedPane.setBounds(20, yPos, 860, 700);

        if (isAdmin) {
            tabbedPane.addTab("Task Assignment", createTaskAssignmentTab());
        }

        tabbedPane.addTab("Active Operations", createActiveOperationsTab());

        add(backBtn);
        add(headerLabel);
        add(tabbedPane);
    }


    private JPanel createTaskAssignmentTab() {
        JPanel panel = new JPanel(null);
        panel.setBackground(AppTheme.BG_DARK);

        int yPos = 15;
        JLabel assignTitle = ComponentFactory.createHeaderLabel(
                "Assign Rescue Tasks", AppTheme.COLOR_BLUE);
        assignTitle.setBounds(20, yPos, 300, 25);
        yPos += 35;

        JLabel zoneLabel = new JLabel("Select Zone:");
        zoneLabel.setFont(AppTheme.FONT_LABEL);
        zoneLabel.setForeground(AppTheme.TEXT_WHITE);
        zoneLabel.setBounds(20, yPos, 150, 20);

        JComboBox<String> zoneCombo = new JComboBox<>(new String[]{
                "Select Zone", "Coastal Area", "Mountain Region", "Urban Area",
                "Rural Area", "Flood Zone", "Earthquake Zone"
        });
        UIUtils.styleComboBox(zoneCombo);
        zoneCombo.setBounds(20, yPos + 25, 400, 40);
        yPos += 75;

        JLabel responderLabel = new JLabel("Select Responder:");
        responderLabel.setFont(AppTheme.FONT_LABEL);
        responderLabel.setForeground(AppTheme.TEXT_WHITE);
        responderLabel.setBounds(20, yPos, 150, 20);

        List<User> responders = userDAO.getAllResponders();
        String[] responderOptions = new String[responders.size() + 1];
        responderOptions[0] = "Select Responder";
        for (int i = 0; i < responders.size(); i++) {
            responderOptions[i + 1] = responders.get(i).getUsername() + " (ID: " + responders.get(i).getId() + ")";
        }

        JComboBox<String> responderCombo = new JComboBox<>(responderOptions);
        UIUtils.styleComboBox(responderCombo);
        responderCombo.setBounds(20, yPos + 25, 400, 40);
        yPos += 75;

        JLabel taskLabel = new JLabel("Task Description:");
        taskLabel.setFont(AppTheme.FONT_LABEL);
        taskLabel.setForeground(AppTheme.TEXT_WHITE);
        taskLabel.setBounds(20, yPos, 150, 20);

        JTextArea taskDesc = new JTextArea();
        UIUtils.styleTextArea(taskDesc);
        taskDesc.setBounds(20, yPos + 25, 400, 100);
        yPos += 135;

        JButton assignBtn = ComponentFactory.createSuccessButton("Assign Task", () ->
                handleTaskAssignment(zoneCombo, responderCombo, taskDesc));
        assignBtn.setBounds(20, yPos, 400, 50);
        assignBtn.setFont(AppTheme.FONT_LABEL);

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
        panel.setBackground(AppTheme.BG_DARK);

        int yPos = 15;
        JLabel opsTitle = ComponentFactory.createHeaderLabel(
                "Active Rescue Operations", AppTheme.COLOR_BLUE);
        opsTitle.setBounds(20, yPos, 400, 25);
        yPos += 35;

        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(AppTheme.COLOR_BLUE);
                g2d.setFont(AppTheme.FONT_LABEL);
                g2d.drawString("📍 Rescue Operations Map", 20, 30);

                List<String> activeZones = responderTaskDAO.getActiveZones();
                int yp = 60;

                if (activeZones.isEmpty()) {
                    g2d.setFont(AppTheme.FONT_REGULAR);
                    g2d.setColor(AppTheme.COLOR_GREEN);
                    g2d.drawString("No active rescue operations", 20, yp);
                } else {
                    for (String zone : activeZones) {
                        int count = responderTaskDAO.getResponderCountByZone(zone);
                        g2d.setFont(AppTheme.FONT_REGULAR);
                        g2d.setColor(AppTheme.TEXT_SECONDARY);
                        g2d.drawString(zone + ": " + count + " responder(s)", 20, yp);
                        yp += 30;
                    }
                }
            }
        };

        mapPanel.setBounds(20, yPos, 820, 150);
        mapPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        yPos += 170;

        JLabel tasksTitle = ComponentFactory.createHeaderLabel(
                "📋 Tasks in Progress", AppTheme.COLOR_ORANGE);
        tasksTitle.setBounds(20, yPos, 300, 25);
        yPos += 30;

        activeTasksPanel = new JPanel();
        activeTasksPanel.setLayout(new BoxLayout(activeTasksPanel, BoxLayout.Y_AXIS));
        activeTasksPanel.setBackground(AppTheme.BG_DARK);
        activeTasksPanel.setOpaque(false);
        activeTasksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JScrollPane scroll = UIUtils.createThemedScrollPane(activeTasksPanel);
        scroll.setBounds(20, yPos, 820, 250);

        panel.add(opsTitle);
        panel.add(mapPanel);
        panel.add(tasksTitle);
        panel.add(scroll);

        return panel;
    }


    private void refreshOperations() {
        Logger.debug(CLASS_NAME, "Refreshing operations");
        activeTasksPanel.removeAll();

        List<ResponderTask> activeTasks = responderTaskDAO.getActiveZones().stream()
                .flatMap(zone -> responderTaskDAO.getTasksByZone(zone).stream())
                .toList();

        if (activeTasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("✅ No active rescue tasks");
            emptyLabel.setFont(AppTheme.FONT_REGULAR);
            emptyLabel.setForeground(AppTheme.COLOR_GREEN);
            activeTasksPanel.add(emptyLabel);
        } else {
            for (ResponderTask task : activeTasks) {
                JPanel taskCard = createTaskCard(task);
                taskCard.setMaximumSize(new Dimension(800, 100));
                taskCard.setPreferredSize(new Dimension(800, 100));
                activeTasksPanel.add(taskCard);
                activeTasksPanel.add(Box.createVerticalStrut(10));
            }
        }

        activeTasksPanel.revalidate();
        activeTasksPanel.repaint();
        Logger.info(CLASS_NAME, "Operations refreshed - Total tasks: " + activeTasks.size());
    }


    private JPanel createTaskCard(ResponderTask task) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(AppTheme.BG_LIGHT);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                Color borderColor = AppTheme.COLOR_ORANGE;
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        card.setLayout(null);
        card.setOpaque(false);

        JLabel zoneLabel = new JLabel(task.getZone());
        zoneLabel.setFont(AppTheme.FONT_LABEL);
        zoneLabel.setForeground(AppTheme.TEXT_WHITE);
        zoneLabel.setBounds(15, 10, 150, 20);

        JLabel taskLabel = new JLabel(task.getTaskDescription());
        taskLabel.setFont(AppTheme.FONT_REGULAR);
        taskLabel.setForeground(new Color(200, 200, 200));
        taskLabel.setBounds(15, 35, 500, 20);

        JLabel statusLabel = new JLabel("Status: " + task.getStatus().toUpperCase());
        statusLabel.setFont(AppTheme.FONT_REGULAR);
        statusLabel.setForeground(AppTheme.COLOR_YELLOW);
        statusLabel.setBounds(15, 60, 200, 15);

        JLabel timeLabel = new JLabel("Assigned: " + task.getAssignedAt());
        timeLabel.setFont(AppTheme.FONT_SMALL);
        timeLabel.setForeground(new Color(150, 150, 150));
        timeLabel.setBounds(220, 60, 300, 15);

        if (isAdmin || isResponder) {
            if ("active".equals(task.getStatus())) {
                JButton startBtn = ComponentFactory.createPrimaryButton("START",
                        () -> handleStartTask(task));
                startBtn.setBounds(670, 25, 100, 45);
                card.add(startBtn);
            } else if ("in_progress".equals(task.getStatus())) {
                JButton completeBtn = ComponentFactory.createSuccessButton("COMPLETE",
                        () -> handleCompleteTask(task));
                completeBtn.setBounds(670, 25, 100, 45);
                card.add(completeBtn);
            }
        }

        card.add(zoneLabel);
        card.add(taskLabel);
        card.add(statusLabel);
        card.add(timeLabel);

        return card;
    }

    private void handleStartTask(ResponderTask task) {
        if (responderTaskDAO.updateTaskStatus(task.getId(), "in_progress")) {
            Logger.info(CLASS_NAME, "Task started: " + task.getId());
            UIUtils.showInfo(this, "Success", "✅ Task started!");
            refreshOperations();
        }
    }

    private void handleCompleteTask(ResponderTask task) {
        if (responderTaskDAO.updateTaskStatus(task.getId(), "completed")) {
            Logger.info(CLASS_NAME, "Task completed: " + task.getId());
            UIUtils.showInfo(this, "Success", "✅ Task completed!");
            refreshOperations();
        }
    }

    private void handleTaskAssignment(JComboBox<String> zoneCombo,
                                      JComboBox<String> responderCombo, JTextArea taskDesc) {
        String zone = (String) zoneCombo.getSelectedItem();
        String responderStr = (String) responderCombo.getSelectedItem();
        String task = taskDesc.getText().trim();

        if (zone.equals("Select Zone") || responderStr.equals("Select Responder") || task.isEmpty()) {
            UIUtils.showWarning(this, "Validation Error", "Please fill all fields!");
            return;
        }

        int responderId = Integer.parseInt(responderStr.split("ID: ")[1].replace(")", ""));
        ResponderTask respTask = new ResponderTask(responderId, zone, task);

        if (responderTaskDAO.assignTask(respTask)) {
            Logger.info(CLASS_NAME, "Task assigned to zone: " + zone);
            UIUtils.showInfo(this, "Success", "✅ Task Assigned Successfully!");
            zoneCombo.setSelectedIndex(0);
            responderCombo.setSelectedIndex(0);
            taskDesc.setText("");
            refreshOperations();
        }
    }
}