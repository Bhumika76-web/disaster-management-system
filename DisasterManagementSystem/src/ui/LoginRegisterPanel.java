package ui;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import models.User;
import dao.UserDAO;
import ui.components.ComponentFactory;
import ui.theme.AppTheme;
import ui.utils.UIUtils;
import util.Logger;
import util.ValidationUtils;
import config.AppConstants;


public class LoginRegisterPanel extends JPanel {


    private static final String CLASS_NAME = "LoginRegisterPanel";


    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn, toggleBtn;


    private JTextField usernameField, registrationEmailField, phoneField, locationField;
    private JPasswordField registrationPasswordField, confirmPasswordField;
    private JComboBox<String> userTypeCombo;
    private JButton registerBtn;


    private boolean isLoginMode = true;
    private MainFrame mainFrame;
    private UserDAO userDAO;


    private JLabel emailErrorLabel, passwordErrorLabel;

    public LoginRegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();

        setLayout(null);
        setBackground(AppTheme.BG_DARK);

        Logger.info(CLASS_NAME, "Initializing LoginRegisterPanel");
        initLoginUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient background for aesthetic appeal
        GradientPaint gradient = new GradientPaint(
                0, 0, AppTheme.BG_DARK,
                getWidth(), getHeight(), AppTheme.BG_MEDIUM
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }


    private void initLoginUI() {
        removeAll();


        JLabel titleLabel = ComponentFactory.createTitleLabel(
                "🚨 " + AppConstants.APP_NAME, AppTheme.COLOR_RED);
        titleLabel.setBounds(100, 30, 800, 50);

        JLabel subtitleLabel = new JLabel(AppConstants.ORGANIZATION + " - Emergency Services");
        subtitleLabel.setFont(AppTheme.FONT_REGULAR);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        subtitleLabel.setBounds(200, 80, 600, 25);


        JLabel emailLabel = createInputLabel("📧 Email Address:");
        emailLabel.setBounds(150, 140, 200, 25);

        emailField = createInputField();
        emailField.setBounds(150, 170, 600, 45);
        UIUtils.addFieldPlaceholder(emailField, "Enter your email");


        emailErrorLabel = createErrorLabel("");
        emailErrorLabel.setBounds(150, 220, 600, 20);
        emailField.getDocument().addDocumentListener(createDocumentListener(
                emailField, emailErrorLabel, this::validateEmailInput));


        JLabel pwdLabel = createInputLabel("🔐 Password:");
        pwdLabel.setBounds(150, 250, 200, 25);

        passwordField = createPasswordField();
        passwordField.setBounds(150, 280, 600, 45);

        passwordErrorLabel = createErrorLabel("");
        passwordErrorLabel.setBounds(150, 330, 600, 20);


        loginBtn = ComponentFactory.createPrimaryButton("LOGIN", this::handleLogin);
        loginBtn.setBounds(150, 370, 600, 50);
        loginBtn.setFont(AppTheme.FONT_LABEL);


        toggleBtn = ComponentFactory.createWarningButton("Create New Account", this::toggleMode);
        toggleBtn.setBounds(150, 430, 600, 50);
        toggleBtn.setFont(AppTheme.FONT_LABEL);


        add(titleLabel);
        add(subtitleLabel);
        add(emailLabel);
        add(emailField);
        add(emailErrorLabel);
        add(pwdLabel);
        add(passwordField);
        add(passwordErrorLabel);
        add(loginBtn);
        add(toggleBtn);

        revalidate();
        repaint();
    }


    private void initRegisterUI() {
        removeAll();

        JLabel titleLabel = ComponentFactory.createTitleLabel(
                "CREATE ACCOUNT", AppTheme.COLOR_ORANGE);
        titleLabel.setBounds(200, 20, 700, 50);


        final int FIELD_WIDTH = 600;
        final int FIELD_HEIGHT = 40;
        final int LABEL_HEIGHT = 20;
        final int SPACING = 70;
        final int START_X = 150;

        int currentY = 80;


        JLabel usernameLabel = createInputLabel("👤 Username:");
        usernameLabel.setBounds(START_X, currentY, 200, LABEL_HEIGHT);
        usernameField = createInputField();
        usernameField.setBounds(START_X, currentY + 25, FIELD_WIDTH, FIELD_HEIGHT);
        currentY += SPACING;


        JLabel emailLabel = createInputLabel("📧 Email Address:");
        emailLabel.setBounds(START_X, currentY, 200, LABEL_HEIGHT);
        registrationEmailField = createInputField();
        registrationEmailField.setBounds(START_X, currentY + 25, FIELD_WIDTH, FIELD_HEIGHT);
        UIUtils.addFieldPlaceholder(registrationEmailField, "user@example.com");
        currentY += SPACING;


        JLabel pwdLabel = createInputLabel("🔐 Password:");
        pwdLabel.setBounds(START_X, currentY, 200, LABEL_HEIGHT);
        registrationPasswordField = createPasswordField();
        registrationPasswordField.setBounds(START_X, currentY + 25, FIELD_WIDTH, FIELD_HEIGHT);
        currentY += SPACING;


        JLabel phoneLabel = createInputLabel("📱 Phone Number:");
        phoneLabel.setBounds(START_X, currentY, 200, LABEL_HEIGHT);
        phoneField = createInputField();
        phoneField.setBounds(START_X, currentY + 25, FIELD_WIDTH, FIELD_HEIGHT);
        UIUtils.addFieldPlaceholder(phoneField, "10-digit number");
        currentY += SPACING;


        JLabel locationLabel = createInputLabel("📍 Location:");
        locationLabel.setBounds(START_X, currentY, 200, LABEL_HEIGHT);
        locationField = createInputField();
        locationField.setBounds(START_X, currentY + 25, FIELD_WIDTH, FIELD_HEIGHT);
        UIUtils.addFieldPlaceholder(locationField, "City, State");
        currentY += SPACING;


        JLabel typeLabel = createInputLabel("👥 Account Type:");
        typeLabel.setBounds(START_X, currentY, 200, LABEL_HEIGHT);
        userTypeCombo = new JComboBox<>(
                new String[]{AppConstants.USER_TYPE_CITIZEN,
                        AppConstants.USER_TYPE_RESPONDER,
                        AppConstants.USER_TYPE_ADMIN});
        UIUtils.styleComboBox(userTypeCombo);
        userTypeCombo.setBounds(START_X, currentY + 25, FIELD_WIDTH, FIELD_HEIGHT);
        currentY += SPACING;


        registerBtn = ComponentFactory.createSuccessButton("REGISTER", this::handleRegister);
        registerBtn.setBounds(START_X, currentY, FIELD_WIDTH, 50);
        registerBtn.setFont(AppTheme.FONT_LABEL);
        currentY += 65;


        toggleBtn = ComponentFactory.createDangerButton("Back to Login", this::toggleMode);
        toggleBtn.setBounds(START_X, currentY, FIELD_WIDTH, 50);
        toggleBtn.setFont(AppTheme.FONT_LABEL);


        add(titleLabel);
        add(usernameLabel);
        add(usernameField);
        add(emailLabel);
        add(registrationEmailField);
        add(phoneLabel);
        add(phoneField);
        add(locationLabel);
        add(locationField);
        add(typeLabel);
        add(userTypeCombo);
        add(pwdLabel);
        add(registrationPasswordField);
        add(registerBtn);
        add(toggleBtn);

        revalidate();
        repaint();
    }


    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            Logger.debug(CLASS_NAME, "Switched to login mode");
            initLoginUI();
        } else {
            Logger.debug(CLASS_NAME, "Switched to registration mode");
            initRegisterUI();
        }
    }


    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        Logger.info(CLASS_NAME, "Login attempt for: " + email);


        if (!ValidationUtils.isValidEmail(email)) {
            Logger.warn(CLASS_NAME, "Invalid email format: " + email);
            UIUtils.showWarning(this, "Validation Error", "Please enter a valid email address");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Logger.warn(CLASS_NAME, "Password field empty");
            UIUtils.showWarning(this, "Validation Error", "Password is required");
            passwordField.requestFocus();
            return;
        }

        if (password.length() < AppConstants.MIN_PASSWORD_LENGTH) {
            UIUtils.showWarning(this, "Validation Error",
                    "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        try {

            User user = userDAO.loginUser(email, password);

            if (user == null) {
                Logger.warn(CLASS_NAME, "Login failed - Invalid credentials: " + email);
                UIUtils.showError(this, "Login Failed",
                        "Invalid email or password. Please check and try again.");
                passwordField.setText(""); // Clear password on failure
                passwordField.requestFocus();
                return;
            }


            Logger.info(CLASS_NAME, "User logged in successfully: " + user.getUsername());
            UIUtils.showInfo(this, "Success",
                    "✅ Welcome " + user.getUsername() + "! Redirecting to dashboard...");
            mainFrame.showDashboard(user);

        } catch (Exception e) {
            Logger.error(CLASS_NAME, "Unexpected error during login", e);
            UIUtils.showError(this, "Connection Error",
                    "Unable to connect to server. Please try again later.");
        }
    }


    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = registrationEmailField.getText().trim();
        String password = new String(registrationPasswordField.getPassword());
        String phone = phoneField.getText().trim();
        String location = locationField.getText().trim();
        String userType = userTypeCombo.getSelectedItem().toString().toLowerCase();

        Logger.info(CLASS_NAME, "Registration attempt for username: " + username);


        String validationError = validateRegistrationInputs(username, email, password, phone, location);
        if (validationError != null) {
            Logger.warn(CLASS_NAME, "Registration validation failed: " + validationError);
            UIUtils.showWarning(this, "Validation Error", validationError);
            return;
        }

        try {

            User newUser = new User(username, email, password, phone, location, userType);

            if (userDAO.registerUser(newUser)) {
                Logger.info(CLASS_NAME, "New user registered: " + username);
                UIUtils.showInfo(this, "Success",
                        "✅ Registration Successful!\n\nYour account has been created.\nPlease login with your credentials.");
                toggleMode(); // Return to login

            } else {
                Logger.warn(CLASS_NAME, "Registration failed - User may already exist: " + username);
                UIUtils.showError(this, "Registration Failed",
                        "Username or email already exists in the system.\nPlease choose different credentials.");
                usernameField.requestFocus();
            }
        } catch (Exception e) {
            Logger.error(CLASS_NAME, "Unexpected error during registration", e);
            UIUtils.showError(this, "Error",
                    "Registration failed due to system error. Please try again later.");
        }
    }


    private String validateRegistrationInputs(String username, String email,
                                              String password, String phone, String location) {

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                phone.isEmpty() || location.isEmpty()) {
            return "All fields are required";
        }


        if (!ValidationUtils.isValidUsername(username)) {
            return "Username must be at least " + AppConstants.MIN_USERNAME_LENGTH + " characters";
        }


        if (!ValidationUtils.isValidEmail(email)) {
            return "Please enter a valid email address";
        }


        if (!ValidationUtils.isValidPassword(password)) {
            return "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters";
        }


        if (!ValidationUtils.isValidPhone(phone)) {
            return "Phone number must be exactly " + AppConstants.PHONE_LENGTH + " digits";
        }


        if (location.length() < 3) {
            return "Location must be at least 3 characters";
        }

        return null;
    }


    private String validateEmailInput() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            return "";
        }
        if (!ValidationUtils.isValidEmail(email)) {
            return "❌ Invalid email format";
        }
        return "✅ Valid email";
    }


    private DocumentListener createDocumentListener(JTextField field, JLabel errorLabel,
                                                    java.util.function.Supplier<String> validator) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateErrorLabel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateErrorLabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateErrorLabel();
            }

            private void updateErrorLabel() {
                String message = validator.get();
                errorLabel.setText(message);
                if (message.contains("Valid")) {
                    errorLabel.setForeground(AppTheme.COLOR_GREEN);
                } else if (!message.isEmpty()) {
                    errorLabel.setForeground(AppTheme.COLOR_RED);
                }
            }
        };
    }


    private JLabel createInputLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_LABEL);
        label.setForeground(AppTheme.TEXT_WHITE);
        return label;
    }


    private JLabel createErrorLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_SMALL);
        label.setForeground(AppTheme.COLOR_RED);
        return label;
    }


    private JTextField createInputField() {
        JTextField field = new JTextField();
        UIUtils.styleTextField(field);
        return field;
    }


    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(AppTheme.BG_MEDIUM);
        field.setForeground(AppTheme.TEXT_WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(AppTheme.FONT_REGULAR);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }
}