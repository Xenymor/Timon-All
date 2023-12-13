package BattleshipAi.Bots;

import StandardClasses.Vector2L;

public interface BattleshipBot {
    Vector2L getMove();
    void moveResult(Vector2L pos, final boolean attack);
    void reset();
}
