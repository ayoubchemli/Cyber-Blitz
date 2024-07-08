package game;

import java.awt.*;

public class Grunt extends EnemyBase {
    private static final double GRUNT_SPEED = 2;
    private static final int GRUNT_SIZE = 30;
    private static final int GRUNT_HEALTH = 50;
    private static final int GRUNT_SCORE = 100;

    public Grunt(double x, double y) {
        super(x, y, GRUNT_SIZE, GRUNT_SIZE, GRUNT_HEALTH, GRUNT_SCORE);
    }

    @Override
    protected void behave(Player player, double delta) {
        if (isCollidingWithOtherEnemies()) {
            avoidCollision();
        } else {
            moveTowards(player.x, player.y, GRUNT_SPEED, delta);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);
    }

    @Override
    protected double getSpeed() {
        return GRUNT_SPEED;
    }
}