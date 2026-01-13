package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import models.User;
import dao.UserDAO;

public class LoginRegisterPanel extends JPanel {
    private JTextField usernameField, emailField, phoneField, locationField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> userTypeCombo;
    private JButton loginBtn, registerBtn, toggleBtn;
    private boolean isLoginMode = true;
    private MainFrame mainFrame;
    private UserDAO userDAO = new UserDAO();
    private float alpha = 0f;

    public LoginRegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(new Color(20, 25, 47));

        initLoginUI();
        setupAnimationTimer();
    }

    private void setupAnimationTimer() {
        Timer timer = new Timer(30, e -> {
            alpha += 0.02f;
            if (alpha > 1f) alpha = 1f;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(20, 25, 47),
                getWidth(), getHeight(), new Color(40, 50, 80)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(255, 100, 100, 30));
        g2d.fillOval((int)(Math.sin(System.currentTimeMillis() / 2000.0) * 50) + 50, 50, 150, 150);

        g2d.setColor(new Color(100, 150, 255, 30));
        g2d.fillOval(getWidth() - 200, (int)(Math.cos(System.currentTimeMillis() / 2000.0) * 50) + 100, 150, 150);
    }

    private void initLoginUI() {

        JLabel titleLabel = new JLabel("🚨 DISASTER ALERT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 100, 100));
        titleLabel.setBounds(150, 30, 700, 50);

        JLabel subtitleLabel = new JLabel("Emergency Management Platform");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(150, 200, 255));
        subtitleLabel.setBounds(250, 80, 400, 25);

        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(150, 140, 200, 25);

        emailField = createStyledTextField();
        emailField.setBounds(150, 170, 600, 45);

        JLabel pwdLabel = new JLabel("Password:");
        pwdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pwdLabel.setForeground(Color.WHITE);
        pwdLabel.setBounds(150, 230, 200, 25);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 260, 600, 45);
        stylePasswordField(passwordField);

        loginBtn = createStyledButton("LOGIN", new Color(100, 200, 255));
        loginBtn.setBounds(150, 340, 600, 50);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.addActionListener(e -> handleLogin());

        toggleBtn = createStyledButton("CREATE ACCOUNT", new Color(255, 150, 100));
        toggleBtn.setBounds(150, 410, 600, 50);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16));
        toggleBtn.addActionListener(e -> toggleMode());

        add(titleLabel);
        add(subtitleLabel);
        add(emailLabel);
        add(emailField);
        add(pwdLabel);
        add(passwordField);
        add(loginBtn);
        add(toggleBtn);
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        removeAll();
        if (isLoginMode) {
            initLoginUI();
        } else {
            initRegisterUI();
        }
        revalidate();
        repaint();
    }

    private void initRegisterUI() {
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 150, 100));
        titleLabel.setBounds(200, 20, 700, 50);

        int startY = 80;
        int fieldHeight = 40;
        int spacing = 70;
        int x = 150;
        int width = 600;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(x, startY, 200, 20);

        usernameField = createStyledTextField();
        usernameField.setBounds(x, startY + 25, width, fieldHeight);
        startY += spacing;

        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(x, startY, 200, 20);

        emailField = createStyledTextField();
        emailField.setBounds(x, startY + 25, width, fieldHeight);
        startY += spacing;

        JLabel pwdLabel = new JLabel("Password:");
        pwdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pwdLabel.setForeground(Color.WHITE);
        pwdLabel.setBounds(x, startY, 200, 20);

        passwordField = new JPasswordField();
        passwordField.setBounds(x, startY + 25, width, fieldHeight);
        stylePasswordField(passwordField);
        startY += spacing;

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setBounds(x, startY, 200, 20);

        phoneField = createStyledTextField();
        phoneField.setBounds(x, startY + 25, width, fieldHeight);
        startY += spacing;

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(new Font("Arial", Font.BOLD, 12));
        locationLabel.setForeground(Color.WHITE);
        locationLabel.setBounds(x, startY, 200, 20);

        locationField = createStyledTextField();
        locationField.setBounds(x, startY + 25, width, fieldHeight);
        startY += spacing;

        JLabel typeLabel = new JLabel("Account Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setBounds(x, startY, 200, 20);

        userTypeCombo = new JComboBox<>(new String[]{"Citizen","Responder", "Admin"});
        userTypeCombo.setBounds(x, startY + 25, width, fieldHeight);
        styleComboBox(userTypeCombo);
        startY += spacing;

        registerBtn = createStyledButton("REGISTER", new Color(100, 255, 150));
        registerBtn.setBounds(x, startY, width, 50);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        registerBtn.addActionListener(e -> handleRegister());
        startY += 65;

        toggleBtn = createStyledButton("BACK TO LOGIN", new Color(200, 200, 200));
        toggleBtn.setBounds(x, startY, width, 50);
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 16));
        toggleBtn.addActionListener(e -> toggleMode());

        add(titleLabel);
        add(usernameLabel);
        add(usernameField);
        add(emailLabel);
        add(emailField);
        add(phoneLabel);
        add(phoneField);
        add(locationLabel);
        add(locationField);
        add(typeLabel);
        add(userTypeCombo);
        add(pwdLabel);
        add(passwordField);
        add(registerBtn);
        add(toggleBtn);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.loginUser(email, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "✓ Login Successful!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "✗ Invalid email or password!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String location = locationField.getText().trim();
        String userType = userTypeCombo.getSelectedItem().toString().toLowerCase();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                phone.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User newUser = new User(username, email, password, phone, location, userType);

        if (userDAO.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this, "✓ Registration Successful! Please Login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            toggleMode();
        } else {
            JOptionPane.showMessageDialog(this, "✗ Registration Failed! Username/Email might be taken.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(new Color(50, 60, 90));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }

    private void stylePasswordField(JPasswordField field) {
        field.setBackground(new Color(50, 60, 90));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setFont(new Font("Arial", Font.BOLD, 15));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setFont(new Font("Arial", Font.BOLD, 14));
            }
        });
        return btn;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(new Color(50, 60, 90));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private JPanel createRoundedPanel(Color color, int width, int height, int x, int y) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);
        panel.setBounds(x, y, width, height);
        return panel;
    }
}