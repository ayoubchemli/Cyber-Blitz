package game;

import java.awt.*;

public class Tank extends EnemyBase {
    private static final double TANK_SPEED = 1;
    private static final int TANK_SIZE = 40;
    private static final int TANK_HEALTH = 200;
    private static final int TANK_SCORE = 300;

    public Tank(double x, double y) {
        super(x, y, TANK_SIZE, TANK_SIZE, TANK_HEALTH, TANK_SCORE);
    }

    @Override
    protected void behave(Player player, double delta) {
        double dx = player.x - x;
        double dy = player.y - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            velocityX = (dx / distance) * TANK_SPEED;
            velocityY = (dy / distance) * TANK_SPEED;
        }
        
        move(delta);
    }
    

    @Override
    public void render(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    protected double getSpeed() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSpeed'");
    }
}