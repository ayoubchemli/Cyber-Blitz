package game;

import java.awt.*;

public abstract class Entity {
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected double velocityX;
    protected double velocityY;
    protected boolean active;

    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0;
        this.velocityY = 0;
        this.active = true;
    }

    public abstract void update(double delta);

    public abstract void render(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void move(double delta) {
        x += velocityX * delta;
        y += velocityY * delta;
    }
}