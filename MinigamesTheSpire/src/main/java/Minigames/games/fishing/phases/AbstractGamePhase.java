package Minigames.games.fishing.phases;

import Minigames.games.fishing.FishingGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class AbstractGamePhase {
    protected FishingGame parent;
    protected AbstractGamePhase nextGame;
    public AbstractGamePhase(FishingGame parent, AbstractGamePhase next) {
        this.parent = parent;
        nextGame = next;
    }

    public void initialize() { }

    public abstract void update(); //Should call kill when done
    public abstract void render(SpriteBatch sb);
    public abstract void action();

    protected void kill() {
        if(parent.waiting()) {
            if(nextGame != null)
                nextGame.initialize();
            dispose();
            parent.gamePhase = nextGame;
        }
    }

    public void dispose() {}
}
