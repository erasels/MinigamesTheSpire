package Minigames.games.fishing.phases;

import Minigames.games.fishing.FishingGame;

public abstract class AbstractGamePhase {
    private FishingGame parent;
    private AbstractGamePhase nextGame;
    public AbstractGamePhase(FishingGame parent, AbstractGamePhase next) {
        this.parent = parent;
        nextGame = next;
    }

    public void initialize() { }

    public abstract void update(); //Should call kill when done
    public abstract void render();
    public abstract void action();

    private void kill() {
        if(parent.waiting()) {
            nextGame.initialize();
            parent.gamePhase = nextGame;
        }
    }

    public void dispose() {}
}
