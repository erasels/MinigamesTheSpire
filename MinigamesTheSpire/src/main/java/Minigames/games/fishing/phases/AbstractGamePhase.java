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

    private void kill() {
        if(parent.waiting()) {
            nextGame.initialize();
            dispose();
            parent.gamePhase = nextGame;
        }
    }

    public void dispose() {}
}
