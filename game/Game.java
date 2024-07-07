package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Game extends JPanel implements Runnable, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / FPS;

    private boolean running = false;
    private Thread gameThread;
    private List<Entity> entities;
    private Player player;
    private List<Enemy> enemies;
    private int enemyCount = 5;

    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        entities = new ArrayList<>();
        enemies = new ArrayList<>();
        player = new Player(WIDTH / 2, HEIGHT / 2);
        entities.add(player);

        for (int i = 0; i < enemyCount; i++) {
            spawnEnemy();
        }
    }

    private void spawnEnemy() {
        double x = Math.random() * (WIDTH - 30);
        double y = Math.random() * (HEIGHT - 30);
        Enemy enemy = new Enemy(x, y);
        enemies.add(enemy);
        entities.add(enemy);
    }

    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastUpdateTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long updateLength = now - lastUpdateTime;
            lastUpdateTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            update(delta);
            render();

            try {
                long gameTime = System.nanoTime() - lastUpdateTime;
                long sleepTime = (OPTIMAL_TIME - gameTime) / 1000000;
                Thread.sleep(sleepTime >= 0 ? sleepTime : 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(double delta) {
        for (Entity entity : entities) {
            entity.update(delta);
        }
        checkCollisions();
        entities.removeIf(entity -> !entity.isActive());
        enemies.removeIf(enemy -> !enemy.isActive());
        while (enemies.size() < enemyCount) {
            spawnEnemy();
        }
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (Enemy enemy : enemies) {
            if (playerBounds.intersects(enemy.getBounds())) {
                handleCollision(player, enemy);
            }
        }
    }

    private void handleCollision(Player player, Enemy enemy) {
        player.takeDamage(10);
        enemy.setActive(false);
        if (player.getHealth() <= 0) {
            System.out.println("Game Over! Score: " + player.getScore());
        } else {
            player.addScore(100);
        }
    }

    private void render() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Entity entity : entities) {
            entity.render(g);
        }
        // Draw UI elements
        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.getHealth(), 10, 20);
        g.drawString("Score: " + player.getScore(), 10, 40);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: player.moveUp(); break;
            case KeyEvent.VK_S: player.moveDown(); break;
            case KeyEvent.VK_A: player.moveLeft(); break;
            case KeyEvent.VK_D: player.moveRight(); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_S: player.stopVertical(); break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_D: player.stopHorizontal(); break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cyber Blitz");
        Game game = new Game();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        game.start();
    }
}