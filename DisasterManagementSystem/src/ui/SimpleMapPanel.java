package ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import models.Disaster;
import ui.theme.AppTheme;

public class SimpleMapPanel extends JPanel {
    private List<Disaster> disasters;
    private String[] cities = {
            "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata",
            "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow"
    };
    private Map<String, int[]> cityCoordinates;

    public SimpleMapPanel() {
        setBackground(AppTheme.BG_DARK);
        setPreferredSize(new Dimension(860, 200));
        initializeCityCoordinates();
    }

    private void initializeCityCoordinates() {
        cityCoordinates = new HashMap<>();
        cityCoordinates.put("Mumbai", new int[]{100, 150});
        cityCoordinates.put("Delhi", new int[]{200, 50});
        cityCoordinates.put("Bangalore", new int[]{250, 160});
        cityCoordinates.put("Chennai", new int[]{300, 165});
        cityCoordinates.put("Kolkata", new int[]{320, 80});
        cityCoordinates.put("Hyderabad", new int[]{250, 120});
        cityCoordinates.put("Pune", new int[]{170, 140});
        cityCoordinates.put("Ahmedabad", new int[]{140, 100});
        cityCoordinates.put("Jaipur", new int[]{200, 90});
        cityCoordinates.put("Lucknow", new int[]{260, 70});
    }

    public void displayDisastersOnMap(List<Disaster> disasterList) {
        this.disasters = disasterList;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(25, 35, 65));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        drawGrid(g2d);
        g2d.setColor(AppTheme.TEXT_WHITE);
        g2d.setFont(AppTheme.FONT_LABEL);
        g2d.drawString("📍 India Disaster Map View", 20, 20);

        drawCities(g2d);
        drawDisasterMarkers(g2d);
        drawLegend(g2d);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(AppTheme.BORDER_DARK);
        g2d.setStroke(new BasicStroke(0.5f));
        for (int i = 0; i < getWidth(); i += 60) {
            g2d.drawLine(i, 30, i, getHeight());
        }
        for (int i = 30; i < getHeight(); i += 60) {
            g2d.drawLine(0, i, getWidth(), i);
        }
    }

    private void drawCities(Graphics2D g2d) {
        g2d.setColor(new Color(180, 180, 200));
        g2d.setFont(AppTheme.FONT_SMALL);

        for (String city : cities) {
            if (cityCoordinates.containsKey(city)) {
                int[] coords = cityCoordinates.get(city);
                int x = coords[0];
                int y = coords[1] + 30;
                g2d.fillOval(x - 3, y - 3, 6, 6);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(city, x - fm.stringWidth(city) / 2, y - 8);
            }
        }
    }

    private void drawDisasterMarkers(Graphics2D g2d) {
        if (disasters == null || disasters.isEmpty()) {
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(AppTheme.FONT_REGULAR);
            g2d.drawString("No active disasters", 350, 100);
            return;
        }

        for (Disaster d : disasters) {
            String cityName = d.getLocation().split(",")[0].trim();
            int[] coords = null;

            for (String city : cities) {
                if (cityName.toLowerCase().contains(city.toLowerCase())) {
                    coords = cityCoordinates.get(city);
                    break;
                }
            }

            if (coords != null) {
                int x = coords[0];
                int y = coords[1] + 30;
                Color markerColor = getSeverityColor(d.getSeverity());

                g2d.setColor(new Color(markerColor.getRed(), markerColor.getGreen(),
                        markerColor.getBlue(), 80));
                g2d.fillOval(x - 12, y - 12, 24, 24);

                g2d.setColor(markerColor);
                g2d.fillOval(x - 9, y - 9, 18, 18);

                g2d.setColor(AppTheme.TEXT_WHITE);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(x - 9, y - 9, 18, 18);

                g2d.setColor(AppTheme.TEXT_WHITE);
                g2d.setFont(AppTheme.FONT_SMALL);
                String severity = String.valueOf(d.getSeverity());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(severity, x - fm.stringWidth(severity) / 2, y + 3);
            }
        }
    }

    private void drawLegend(Graphics2D g2d) {
        int legendX = getWidth() - 180;
        int legendY = 35;

        g2d.setColor(AppTheme.BG_MEDIUM);
        g2d.fillRect(legendX - 8, legendY - 8, 170, 130);

        g2d.setColor(AppTheme.BORDER_LIGHT);
        g2d.drawRect(legendX - 8, legendY - 8, 170, 130);

        g2d.setColor(AppTheme.TEXT_WHITE);
        g2d.setFont(AppTheme.FONT_LABEL);
        g2d.drawString("Legend:", legendX, legendY + 8);

        drawLegendItem(g2d, legendX, legendY + 20, AppTheme.COLOR_RED, "Critical (8-10)");
        drawLegendItem(g2d, legendX, legendY + 45, AppTheme.COLOR_ORANGE, "High (5-7)");
        drawLegendItem(g2d, legendX, legendY + 70, AppTheme.COLOR_YELLOW, "Moderate (1-4)");
    }

    private void drawLegendItem(Graphics2D g2d, int x, int y, Color color, String label) {
        g2d.setColor(color);
        g2d.fillOval(x, y, 10, 10);
        g2d.setColor(AppTheme.TEXT_WHITE);
        g2d.setFont(AppTheme.FONT_SMALL);
        g2d.drawString(label, x + 18, y + 8);
    }

    private Color getSeverityColor(int severity) {
        if (severity >= 8) return AppTheme.COLOR_RED;
        if (severity >= 5) return AppTheme.COLOR_ORANGE;
        return AppTheme.COLOR_YELLOW;
    }
}