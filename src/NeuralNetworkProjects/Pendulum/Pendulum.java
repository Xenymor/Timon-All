package NeuralNetworkProjects.Pendulum;

import java.awt.*;

public class Pendulum {
    Vector2 pos = new Vector2(0, 0);
    Vector2 origin;
    double length;
    double angle;
    double angularVelocity;
    double gravity = 9.81;

    public Pendulum(Vector2 origin, double length, double angle) {
        this.origin = origin;
        this.length = length;
        this.angle = angle;
        this.angularVelocity = 0;
        updatePosition();
    }

    public void update() {
        double force = -gravity / length * Math.sin(angle);
        angularVelocity += force;
        angle += angularVelocity;

        // Damping to simulate air resistance
        angularVelocity *= 0.99;

        updatePosition();
    }

    private void updatePosition() {
        pos.x = (origin.x + length * Math.sin(angle));
        pos.y = (origin.y + length * Math.cos(angle));
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void moveOriginLeft() {
        origin.x -= 10;
        updatePosition();
    }

    public void moveOriginRight() {
        origin.x += 10;
        updatePosition();
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        // Draw the line representing the rod of the pendulum
        g.drawLine((int) origin.x, (int) origin.y, (int) pos.x, (int) pos.y);
        // Draw the circle representing the pendulum bob
        g.fillOval((int) pos.x - 5, (int) pos.y - 5, 10, 10);
    }
}
