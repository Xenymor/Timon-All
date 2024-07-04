package NeuralNetworkProjects.Pendulum;

import java.awt.Color;
import java.awt.Graphics;

public class Pendulum {
    Vector2 pos;
    Vector2 origin;
    double length;
    double angle;
    double angularVelocity;
    double gravity = 9.81;
    double previousOriginX;

    public Pendulum(Vector2 origin, double length, double angle) {
        this.origin = origin;
        this.length = length;
        this.angle = angle;
        this.angularVelocity = 0;
        this.previousOriginX = origin.x;
        updatePosition();
    }

    public void update() {
        double force = -gravity / length * Math.sin(angle);
        angularVelocity += force;

        // Damping to simulate air resistance
        angularVelocity *= 0.99;

        angle += angularVelocity;

        updatePosition();
    }

    private void updatePosition() {
        pos = new Vector2(
                origin.x + length * Math.sin(angle),
                origin.y + length * Math.cos(angle)
        );
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        // Draw the line representing the rod of the pendulum
        g.drawLine((int) origin.x, (int) origin.y, (int) pos.x, (int) pos.y);
        // Draw the circle representing the pendulum bob
        g.fillOval((int) pos.x - 5, (int) pos.y - 5, 10, 10);
    }

    public void moveOriginLeft() {
        previousOriginX = origin.x;
        origin.x -= 10;
        applyHorizontalForce();
    }

    public void moveOriginRight() {
        previousOriginX = origin.x;
        origin.x += 10;
        applyHorizontalForce();
    }

    private void applyHorizontalForce() {
        double deltaX = origin.x - previousOriginX;
        double force = deltaX * gravity / length;
        angularVelocity += force;
        previousOriginX = origin.x;
    }
}
