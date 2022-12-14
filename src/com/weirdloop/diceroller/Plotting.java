package com.weirdloop.diceroller;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Created by Ioannis Panteleakis on 28/08/2022.
 * Relax Dice Simulation
 *
 * Plotting implementation based on https://stackoverflow.com/a/32448101/1899334
 */

public class Plotting extends JPanel {

    private final int width = 800;
    private final int height = 400;
    private final int padding = 25;
    private final int labelPadding = 25;
    private final Color lineColor = new Color(44, 102, 230, 180);
    private final Color pointColor = new Color(100, 100, 100, 180);
    private final Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private final int pointWidth = 4;
    private final int numberYDivisions = 10;
    private List<Double> scores;

    public Plotting(List<Double> scores) {
        this.scores = scores;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((getMaxScore() - scores.get(i)) * yScale + padding);
            if (scores.get(i) > 0f)
                graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y-axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            if (scores.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y0);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y0);
        }

        // and for x-axis
        for (int i = 0; i < scores.size(); i++) {
            if (scores.size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((scores.size() / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x0, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = i + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x0, y1);
            }
        }

        // create x and y axes
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (Point graphPoint : graphPoints) {
            int x = graphPoint.x - pointWidth / 2;
            int y = graphPoint.y - pointWidth / 2;
            g2.fillOval(x, y, pointWidth, pointWidth);
        }
    }

    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for (Double score : scores) {
            double tmpMin = Math.min(minScore, score);
            if (tmpMin > 0.0f)
                minScore = tmpMin;
        }
        return minScore;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for (Double score : scores) {
            maxScore = Math.max(maxScore, score);
        }
        return maxScore * 1.02;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
        invalidate();
        this.repaint();
    }

    public List<Double> getScores() {
        return scores;
    }

    //com.weirdloop.diceroller.Main changes underneath
    static class MainPanel extends JPanel {
        public MainPanel(List<Double> scores) {
            setLayout(new BorderLayout());

            JLabel title = new JLabel("Standard deviation over number of games");
            title.setFont(new Font("Arial", Font.BOLD, 25));
            title.setHorizontalAlignment(JLabel.CENTER);

            JPanel graphPanel = new Plotting(scores);

            VerticalPanel vertPanel = new VerticalPanel();

            HorizontalPanel horizPanel = new HorizontalPanel();

            add(title, BorderLayout.NORTH);
            add(horizPanel, BorderLayout.SOUTH);
            add(vertPanel, BorderLayout.WEST);
            add(graphPanel, BorderLayout.CENTER);
        }

        static class VerticalPanel extends JPanel {
            public VerticalPanel() {
                setPreferredSize(new Dimension(25, 0));
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D gg = (Graphics2D) g;
                gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Font font = new Font("Arial", Font.PLAIN, 15);

                String string = "Standard deviation";

                FontMetrics metrics = g.getFontMetrics(font);
                int width = metrics.stringWidth(string);
                int height = metrics.getHeight();

                gg.setFont(font);

                drawRotate(gg, getWidth(), (double) (getHeight() + width) / 2, 270, string);
            }

            public void drawRotate(Graphics2D gg, double x, double y, int angle, String text) {
                gg.translate((float) x, (float) y);
                gg.rotate(Math.toRadians(angle));
                gg.drawString(text, 0, 0);
                gg.rotate(-Math.toRadians(angle));
                gg.translate(-(float) x, -(float) y);
            }
        }

        static class HorizontalPanel extends JPanel {
            public HorizontalPanel() {
                setPreferredSize(new Dimension(0, 25));
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D gg = (Graphics2D) g;
                gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Font font = new Font("Arial", Font.PLAIN, 15);

                String string = "Number of games";

                FontMetrics metrics = g.getFontMetrics(font);
                int width = metrics.stringWidth(string);
                int height = metrics.getHeight();

                gg.setFont(font);

                gg.drawString(string, (getWidth() - width) / 2, 11);
            }
        }
    }

    public void createAndShowGui() {
        MainPanel mainPanel = new MainPanel(scores);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("Plotter - Dice game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
