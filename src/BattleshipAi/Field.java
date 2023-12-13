package BattleshipAi;

public class Field {
    private boolean isShip;
    private boolean isHit;

    public Field() {
        isShip = false;
        isHit = false;
    }

    public boolean isShip() {
        return isShip;
    }

    public void setShip(final boolean ship) {
        isShip = ship;
    }

    public void setHit(final boolean b) {
        isHit = b;
    }

    public boolean isHit() {
        return isHit;
    }
}
