package com.javawithai.snake;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class SnakeGame {
    private SnakeGame() {
    }

    public static void start() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new SnakePanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
