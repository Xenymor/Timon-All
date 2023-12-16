package BattleshipAi.Bots;

import StandardClasses.Vector2I;

public interface BattleshipBot {
    Vector2I getMove();

    void moveResult(Vector2I pos, final boolean attack);

    void reset();
}
