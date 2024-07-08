package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Game extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int FPS = 60;
    private static final long OPTIMAL_TIME = 1000000000 / FPS;

    private boolean running = false;
    private Thread gameThread;
    private List<Entity> entities;
    private Player player;
    private List<EnemyBase> enemies;
    public List<EnemyBase> getEnemies() {
        return enemies;
    }

    private int enemyCount = 3;

    private List<Projectile> projectiles;
    private Point mousePosition;

    private static Game instance;

    public static Game getInstance() {
        return instance;
    }

    private static final int SPAWN_X = 50;  // X-coordinate of spawn point
    private static final int SPAWN_Y = 50;  // Y-coordinate of spawn point

    private List<EnemyProjectile> enemyProjectiles;


    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        instance = this;

        entities = new ArrayList<>();
        enemies = new ArrayList<>();
        player = new Player(WIDTH / 2, HEIGHT / 2);
        entities.add(player);

        for (int i = 0; i < enemyCount; i++) {
            spawnEnemy();
        }

        projectiles = new ArrayList<>();
        mousePosition = new Point();
        addMouseListener(this);
        addMouseMotionListener(this);

        enemyProjectiles = new ArrayList<>();

    }

    private Random random = new Random();

    private void spawnEnemy() {
        EnemyBase enemy;
        int enemyType = random.nextInt(3);
        switch (enemyType) {
            case 0:
                enemy = new Grunt(SPAWN_X, SPAWN_Y);
                break;
            case 1:
                enemy = new Shooter(SPAWN_X, SPAWN_Y);
                break;
            case 2:
                enemy = new Tank(SPAWN_X, SPAWN_Y);
                break;
            default:
                enemy = new Grunt(SPAWN_X, SPAWN_Y);
        }
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
        for (Projectile projectile : projectiles) {
            projectile.update(delta);
        }
        for (EnemyBase enemy : enemies) {
            enemy.behave(player, delta);
        }
        checkCollisions();
        entities.removeIf(entity -> !entity.isActive());
        enemies.removeIf(enemy -> !enemy.isActive());
        projectiles.removeIf(projectile -> !projectile.isActive());
        while (enemies.size() < enemyCount) {
            spawnEnemy();
        }

        for (EnemyProjectile projectile : enemyProjectiles) {
            projectile.update(delta);
        }
        checkEnemyProjectileCollisions();
        enemyProjectiles.removeIf(projectile -> !projectile.isActive());
    }

    private void checkEnemyProjectileCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (EnemyProjectile projectile : enemyProjectiles) {
            if (projectile.getBounds().intersects(playerBounds)) {
                player.takeDamage(projectile.getDamage());
                projectile.setActive(false);
            }
        }
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (EnemyBase enemy : enemies) {
            if (playerBounds.intersects(enemy.getBounds())) {
                handleCollision(player, enemy);
            }
        }
        for (Projectile projectile : projectiles) {
            for (EnemyBase enemy : enemies) {
                if (projectile.getBounds().intersects(enemy.getBounds())) {
                    handleProjectileEnemyCollision(projectile, enemy);
                }
            }
        }
    }

    private void handleProjectileEnemyCollision(Projectile projectile, EnemyBase enemy) {
        projectile.setActive(false);
        enemy.takeDamage(100); // Assuming each projectile deals 10 damage
        if (!enemy.isActive()) {
            player.addScore(enemy.getScoreValue());
        }
    }

    private void handleEnemyEnemyCollision(EnemyBase enemy1, EnemyBase enemy2) {
        // Calculate the vector between the two enemies
        double dx = enemy2.x - enemy1.x;
        double dy = enemy2.y - enemy1.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) return; // Avoid division by zero

        // Normalize the vector
        dx /= distance;
        dy /= distance;

        // Move enemies apart
        double separationDistance = (enemy1.width + enemy2.width) / 2.0;
        double moveDistance = (separationDistance - distance) / 2.0;

        enemy1.x -= dx * moveDistance;
        enemy1.y -= dy * moveDistance;
        enemy2.x += dx * moveDistance;
        enemy2.y += dy * moveDistance;

        // Ensure enemies stay within bounds
        enemy1.checkBoundaries();
        enemy2.checkBoundaries();
    }

    private void handleCollision(Player player, EnemyBase enemy) {
        player.takeDamage(10);
        enemy.setActive(false);
        if (player.getHealth() <= 0) {
            System.out.println("Game Over! Score: " + player.getScore());
        } else {
            player.addScore(100);
        }

        // Check enemy-enemy collisions
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                EnemyBase enemy1 = enemies.get(i);
                EnemyBase enemy2 = enemies.get(j);
                if (enemy1.getBounds().intersects(enemy2.getBounds())) {
                    handleEnemyEnemyCollision(enemy1, enemy2);
                }
            }
        }
    }

    private void render() {
        repaint();
    }

    

    public void addEnemyProjectile(EnemyProjectile projectile) {
        enemyProjectiles.add(projectile);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Render entities
        for (Entity entity : entities) {
            entity.render(g);
        }
        
        // Render player projectiles
        for (Projectile projectile : projectiles) {
            projectile.render(g);
        }
        
        // Render enemy projectiles
        for (EnemyProjectile projectile : enemyProjectiles) {
            projectile.render(g);
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

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Projectile projectile = player.shoot(e.getX(), e.getY());
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition.setLocation(e.getX(), e.getY());
    }

    // Implement other MouseListener and MouseMotionListener methods as empty methods
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}

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