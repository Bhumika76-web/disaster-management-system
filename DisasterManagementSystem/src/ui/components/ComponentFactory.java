package ui.components;

import javax.swing.*;
import java.awt.*;
import ui.theme.AppTheme;

public class ComponentFactory {

    public static JButton createPrimaryButton(String text, Runnable action) {
        return createStyledButton(text, AppTheme.COLOR_BLUE, action);
    }

    public static JButton createSuccessButton(String text, Runnable action) {
        return createStyledButton(text, AppTheme.COLOR_GREEN, action);
    }

    public static JButton createDangerButton(String text, Runnable action) {
        return createStyledButton(text, AppTheme.COLOR_RED, action);
    }

    public static JButton createWarningButton(String text, Runnable action) {
        return createStyledButton(text, AppTheme.COLOR_ORANGE, action);
    }

    private static JButton createStyledButton(String text, Color color, Runnable action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };

        btn.setFont(AppTheme.FONT_LABEL);
        btn.setForeground(AppTheme.TEXT_WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (action != null) {
            btn.addActionListener(e -> action.run());
        }

        return btn;
    }

    public static JLabel createTitleLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_TITLE);
        label.setForeground(color);
        return label;
    }

    public static JLabel createHeaderLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_HEADER);
        label.setForeground(color);
        return label;
    }
}
