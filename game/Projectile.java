package game;

import java.awt.*;

public class Projectile extends Entity {
    private static final double PROJECTILE_SPEED = 10;
    private static final int PROJECTILE_SIZE = 20;

    public Projectile(double x, double y, double directionX, double directionY) {
        super(x, y, PROJECTILE_SIZE, PROJECTILE_SIZE);
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        this.velocityX = (directionX / magnitude) * PROJECTILE_SPEED;
        this.velocityY = (directionY / magnitude) * PROJECTILE_SPEED;
    }

    @Override
    public void update(double delta) {
        move(delta);
        // Remove projectile if it goes off-screen
        if (x < 0 || x > Game.WIDTH || y < 0 || y > Game.HEIGHT) {
            setActive(false);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int)x, (int)y, width, height);
    }
}