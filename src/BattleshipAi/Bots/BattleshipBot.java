package BattleshipAi.Bots;

import StandardClasses.Vector2L;

public interface BattleshipBot {
    public Vector2L getMove();
    public void moveResult(Vector2L pos, final boolean attack);
}
