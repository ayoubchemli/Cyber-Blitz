package game;

import java.awt.*;
import java.util.List;

public class Shooter extends EnemyBase {
    private static final double SHOOTER_SPEED = 10;
    private static final int SHOOTER_SIZE = 25;
    private static final int SHOOTER_HEALTH = 30;
    private static final int SHOOTER_SCORE = 150;
    private static final double SHOOT_RANGE = 300;
    private static final long SHOOT_COOLDOWN = 2000; // milliseconds

    private long lastShotTime;

    public Shooter(double x, double y) {
        super(x, y, SHOOTER_SIZE, SHOOTER_SIZE, SHOOTER_HEALTH, SHOOTER_SCORE);
        this.lastShotTime = 0;
    }

    @Override
    protected void behave(Player player, double delta) {
        double dx = player.x - x;
        double dy = player.y - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > SHOOT_RANGE) {
            moveTowards(player.x, player.y, SHOOTER_SPEED, delta);
        } else {
            velocityX = 0;
            velocityY = 0;
            tryShoot(player);
        }
    }

    private void tryShoot(Player player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
            lastShotTime = currentTime;
            double centerX = x + width / 2;
            double centerY = y + height / 2;
            double directionX = player.x - centerX;
            double directionY = player.y - centerY;
            EnemyProjectile projectile = new EnemyProjectile(centerX, centerY, directionX, directionY);
            Game.getInstance().addEnemyProjectile(projectile);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    protected double getSpeed() {
        return SHOOTER_SPEED;
    }
}