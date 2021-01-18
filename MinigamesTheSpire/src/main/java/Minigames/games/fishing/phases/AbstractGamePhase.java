package Minigames.games.fishing.phases;

import Minigames.Minigames;
import Minigames.games.fishing.FishingGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class AbstractGamePhase {
    protected FishingGame parent;
    protected AbstractGamePhase nextGame;

    public boolean isDone, waiting;

    public AbstractGamePhase(FishingGame parent, AbstractGamePhase next) {
        this.parent = parent;
        nextGame = next;
        isDone = false;
        waiting = false;
    }

    public void initialize() { }

    public abstract void update(); //Should call kill when done
    public abstract void render(SpriteBatch sb);
    public abstract void action();

    protected void kill() {
        if(parent.waiting()) {
            if(nextGame != null)
                nextGame.initialize();
            Minigames.logger.info("Disposing of Fishing game.");
            killAction();
            dispose();
            parent.gamePhase = nextGame;
        } else {
            waiting = true;
        }
    }

    protected void killAction() {}

    public void dispose() {}
}
