package game;

import java.awt.*;
import java.util.List;

public class Player extends Entity {
    private static final double PLAYER_SPEED = 10;
    private int health;
    private int score;
    private long lastShotTime;
    private static final long SHOT_COOLDOWN = 250; // milliseconds

    public Player(double x, double y) {
        super(x, y, 32, 32);
        this.health = 100;
        this.score = 0;
        this.lastShotTime = 0;
    }

    @Override
    public void update(double delta) {
        move(delta);
        x = Math.max(0, Math.min(x, Game.WIDTH - width));
        y = Math.max(0, Math.min(y, Game.HEIGHT - height));
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int)x, (int)y, width, height);
    }

    public void moveLeft() {
        setVelocityX(-PLAYER_SPEED);
    }

    public void moveRight() {
        setVelocityX(PLAYER_SPEED);
    }

    public void moveUp() {
        setVelocityY(-PLAYER_SPEED);
    }

    public void moveDown() {
        setVelocityY(PLAYER_SPEED);
    }

    public void stopHorizontal() {
        setVelocityX(0);
    }

    public void stopVertical() {
        setVelocityY(0);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            setActive(false);
        }
    }

    public void addScore(int points) {
        score += points;
    }

    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }

    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(double vy) {
        this.velocityY = vy;
    }

    public Projectile shoot(double targetX, double targetY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOT_COOLDOWN) {
            lastShotTime = currentTime;
            double centerX = x + width / 2;
            double centerY = y + height / 2;
            double directionX = targetX - centerX;
            double directionY = targetY - centerY;
            return new Projectile(centerX, centerY, directionX, directionY);
        }
        return null;
    }
}