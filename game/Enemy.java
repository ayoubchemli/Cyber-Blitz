package game;

import java.awt.*;


public class Enemy extends Entity {
    private static final double ENEMY_SPEED = 5; // Reduced from 100

    public Enemy(double x, double y) {
        super(x, y, 30, 30);
        double angle = Math.random() * 2 * Math.PI;
        setVelocityX(ENEMY_SPEED * Math.cos(angle));
        setVelocityY(ENEMY_SPEED * Math.sin(angle));
    }

    @Override
    public void update(double delta) {
        move(delta);
        if (x <= 0 || x >= Game.WIDTH - width) {
            velocityX = -velocityX;
        }
        if (y <= 0 || y >= Game.HEIGHT - height) {
            velocityY = -velocityY;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);
    }

    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(double vy) {
        this.velocityY = vy;
    }
}