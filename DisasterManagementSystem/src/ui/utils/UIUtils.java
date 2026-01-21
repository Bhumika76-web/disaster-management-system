package ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import ui.theme.AppTheme;


public class UIUtils {


    public static void styleList(JList<?> list) {
        list.setBackground(AppTheme.BG_MEDIUM);
        list.setForeground(AppTheme.TEXT_SECONDARY);
        list.setFont(AppTheme.FONT_REGULAR);
    }


    public static void styleTextField(JTextField field) {
        field.setBackground(AppTheme.BG_MEDIUM);
        field.setForeground(AppTheme.TEXT_WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(AppTheme.FONT_REGULAR);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }


    public static void styleTextArea(JTextArea area) {
        area.setBackground(AppTheme.BG_MEDIUM);
        area.setForeground(AppTheme.TEXT_WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(AppTheme.FONT_REGULAR);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }


    public static void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(AppTheme.BG_MEDIUM);
        combo.setForeground(AppTheme.TEXT_WHITE);
        combo.setFont(AppTheme.FONT_REGULAR);
        combo.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_DARK, 1));
    }


    public static JScrollPane createThemedScrollPane(JComponent component) {
        JScrollPane pane = new JScrollPane(component);
        pane.setBackground(AppTheme.BG_DARK);
        pane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 2));
        pane.getVerticalScrollBar().setUnitIncrement(15);
        return pane;
    }


    public static void addFieldPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(AppTheme.TEXT_MUTED);


        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(AppTheme.TEXT_WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(AppTheme.TEXT_MUTED);
                }
            }
        });
    }


    public static void showInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }


    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.ERROR_MESSAGE);
    }


    public static void showWarning(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.WARNING_MESSAGE);
    }
}