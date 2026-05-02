package com.javawithai.snake;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

final class SnakePanel extends JPanel {
    private static final int TILE_SIZE = 24;
    private static final int COLUMNS = 25;
    private static final int ROWS = 20;
    private static final int WIDTH = COLUMNS * TILE_SIZE;
    private static final int HEIGHT = ROWS * TILE_SIZE;
    private static final int START_DELAY_MS = 250;
    private static final int SPEED_UP_EVERY_POINTS = 5;
    private static final int SPEED_STEP_MS = 10;
    private static final int MIN_DELAY_MS = 70;

    private final Deque<Point> snake = new ArrayDeque<>();
    private final Random random = new Random();
    private final Timer timer;

    private Direction direction = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private Point apple;
    private boolean running;
    private int score;

    SnakePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(20, 24, 28));
        setFocusable(true);
        addKeyListener(new SnakeControls());

        timer = new Timer(START_DELAY_MS, event -> tick());
        resetGame();
    }

    private void resetGame() {
        snake.clear();
        snake.addFirst(new Point(7, 10));
        snake.addLast(new Point(6, 10));
        snake.addLast(new Point(5, 10));
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        score = 0;
        running = true;
        timer.setDelay(START_DELAY_MS);
        placeApple();
        timer.start();
        repaint();
    }

    private void tick() {
        if (!running) {
            return;
        }

        direction = nextDirection;
        Point head = snake.peekFirst();
        Point nextHead = new Point(head);

        switch (direction) {
            case UP -> nextHead.y--;
            case DOWN -> nextHead.y++;
            case LEFT -> nextHead.x--;
            case RIGHT -> nextHead.x++;
        }

        wrapAroundBoard(nextHead);
        boolean ateApple = nextHead.equals(apple);
        if (isCollision(nextHead, ateApple)) {
            running = false;
            timer.stop();
            repaint();
            return;
        }

        snake.addFirst(nextHead);
        if (ateApple) {
            score++;
            placeApple();
            speedUpIfNeeded();
        } else {
            snake.removeLast();
        }

        repaint();
    }

    private void speedUpIfNeeded() {
        if (score % SPEED_UP_EVERY_POINTS == 0) {
            int fasterDelay = Math.max(MIN_DELAY_MS, timer.getDelay() - SPEED_STEP_MS);
            timer.setDelay(fasterDelay);
        }
    }

    private boolean isCollision(Point point, boolean growing) {
        Point tail = snake.peekLast();
        if (!growing && point.equals(tail)) {
            return false;
        }
        return snake.contains(point);
    }

    private void placeApple() {
        Point candidate;
        do {
            candidate = new Point(random.nextInt(COLUMNS), random.nextInt(ROWS));
        } while (snake.contains(candidate));
        apple = candidate;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g);
        drawApple(g);
        drawSnake(g);
        drawScore(g);

        if (!running) {
            drawGameOver(g);
        }

        g.dispose();
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(31, 37, 43));
        for (int x = 0; x <= WIDTH; x += TILE_SIZE) {
            g.drawLine(x, 0, x, HEIGHT);
        }
        for (int y = 0; y <= HEIGHT; y += TILE_SIZE) {
            g.drawLine(0, y, WIDTH, y);
        }
    }

    private void wrapAroundBoard(Point nextHead) {
        if (nextHead.x < 0) {
            nextHead.x = COLUMNS - 1;
        } else if (nextHead.x >= COLUMNS) {
            nextHead.x = 0;
        }

        if (nextHead.y < 0) {
            nextHead.y = ROWS - 1;
        } else if (nextHead.y >= ROWS) {
            nextHead.y = 0;
        }

    }

    private void drawApple(Graphics2D g) {
        g.setColor(new Color(230, 72, 72));
        g.fillOval(
                apple.x * TILE_SIZE + 4,
                apple.y * TILE_SIZE + 4,
                TILE_SIZE - 8,
                TILE_SIZE - 8);
    }

    private void drawSnake(Graphics2D g) {
        boolean isHead = true;
        for (Point segment : snake) {
            g.setColor(isHead ? new Color(89, 209, 140) : new Color(47, 157, 95));
            g.fillRoundRect(
                    segment.x * TILE_SIZE + 2,
                    segment.y * TILE_SIZE + 2,
                    TILE_SIZE - 4,
                    TILE_SIZE - 4,
                    8,
                    8);
            isHead = false;
        }
    }

    private void drawScore(Graphics2D g) {
        g.setColor(new Color(230, 236, 241));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.drawString("Score: " + score, 12, 24);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        drawCenteredText(g, "Game Over", HEIGHT / 2 - 20);

        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        drawCenteredText(g, "Press Space to play again", HEIGHT / 2 + 22);
    }

    private void drawCenteredText(Graphics2D g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private final class SnakeControls extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> turn(Direction.UP);
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> turn(Direction.DOWN);
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> turn(Direction.LEFT);
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> turn(Direction.RIGHT);
                case KeyEvent.VK_SPACE -> {
                    if (!running) {
                        resetGame();
                    }
                }
                default -> {
                }
            }
        }

        private void turn(Direction newDirection) {
            if ((direction == Direction.UP && newDirection != Direction.DOWN)
                    || (direction == Direction.DOWN && newDirection != Direction.UP)
                    || (direction == Direction.LEFT && newDirection != Direction.RIGHT)
                    || (direction == Direction.RIGHT && newDirection != Direction.LEFT)) {
                nextDirection = newDirection;
            }
        }
    }
}
