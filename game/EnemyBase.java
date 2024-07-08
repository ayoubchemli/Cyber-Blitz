package game;
public abstract class EnemyBase extends Entity {
    protected int health;
    protected int scoreValue;
    protected static final double COLLISION_BUFFER = 5.0; // Buffer for collision avoidance

    public EnemyBase(double x, double y, int width, int height, int health, int scoreValue) {
        super(x, y, width, height);
        this.health = health;
        this.scoreValue = scoreValue;
    }

    @Override
    public void update(double delta) {
        behave(Game.getInstance().getPlayer(), delta);
        checkBoundaries();
    }

    protected abstract void behave(Player player, double delta);

    protected void checkBoundaries() {
        x = Math.max(0, Math.min(x, Game.WIDTH - width));
        y = Math.max(0, Math.min(y, Game.HEIGHT - height));
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            setActive(false);
        }
    }

    public int getScoreValue() {
        return scoreValue;
    }

    protected void moveTowards(double targetX, double targetY, double speed, double delta) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            double velocityX = (dx / distance) * speed;
            double velocityY = (dy / distance) * speed;
            x += velocityX * delta;
            y += velocityY * delta;
        }
    }

    protected boolean isCollidingWithOtherEnemies() {
        for (EnemyBase other : Game.getInstance().getEnemies()) {
            if (other != this && getBounds().intersects(other.getBounds())) {
                return true;
            }
        }
        return false;
    }

    protected void avoidCollision() {
        double avoidX = x + (Math.random() * 2 - 1) * COLLISION_BUFFER;
        double avoidY = y + (Math.random() * 2 - 1) * COLLISION_BUFFER;
        moveTowards(avoidX, avoidY, getSpeed(), 1.0);
    }

    protected abstract double getSpeed();
}