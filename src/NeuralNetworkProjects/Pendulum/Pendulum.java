package NeuralNetworkProjects.Pendulum;

import java.awt.Color;
import java.awt.Graphics;

public class Pendulum {
    final Vector2 pos;
    Vector2 origin;
    final double length;
    double angle;
    double angularVelocity;
    double angularAcceleration;
    final double gravity = 9.81 * 3000;
    final double previousOriginX;
    private final double lengthSquared;
    private double speedX = 0;
    private final double frictionConst;
    private final double mass;
    private double accelX = 0;

    private final Vector2 startPos;
    private final double startAngle;

    public Pendulum(Vector2 origin, double length, double angle, double frictionConst, double mass) {
        this.pos = new Vector2(0, 0);
        startAngle = angle;
        this.origin = new Vector2(origin.x, origin.y);
        startPos = origin;
        this.length = length;
        this.angle = angle;
        this.angularVelocity = 0;
        this.angularAcceleration = 0;
        this.previousOriginX = origin.x;
        this.frictionConst = frictionConst;
        this.mass = mass;
        lengthSquared = length * length;
        updatePosition();
    }

    public void update(double deltaTimeS) {
        final double partOne = Math.cos(angle) * accelX / length;
        final double partTwo = frictionConst * angularVelocity / (mass * lengthSquared);
        final double partThree = gravity * Math.sin(angle) / length;
        angularAcceleration = -partOne - partTwo - partThree;

        angularVelocity += angularAcceleration * deltaTimeS;

        angle += angularVelocity * deltaTimeS;
        origin.x += speedX * deltaTimeS;

        updatePosition();
        speedX = 0;
        accelX = 0;
    }

    private void updatePosition() {
        pos.x = origin.x + length * Math.sin(angle);
        pos.y = origin.y + length * Math.cos(angle);
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

    public void moveOrigin(double movement, double deltaTimeS) {
        final double newSpeed = movement / deltaTimeS;
        accelX = (newSpeed - speedX) / deltaTimeS;
        this.speedX = newSpeed;
    }

    public void reset() {
        origin = new Vector2(startPos.x, startPos.y);
        speedX = 0;
        accelX = 0;
        angle = startAngle;
        angularVelocity = 0;
        angularAcceleration = 0;
        updatePosition();
    }
}
