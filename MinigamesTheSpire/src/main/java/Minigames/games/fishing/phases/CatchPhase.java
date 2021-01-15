package Minigames.games.fishing.phases;

import Minigames.Minigames;
import Minigames.games.fishing.FishingGame;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;

public class CatchPhase extends AbstractGamePhase {
    private static Texture imgBar;
    private static Texture imgSpinner;
    private static Texture imgCatcher;

    public CatchPhase(FishingGame parent, AbstractGamePhase next) {
        super(parent, next);
    }

    @Override
    public void initialize() {
        imgBar = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishingBar.png"));
        imgSpinner = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/SpinnyThing.png"));
        imgCatcher = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishCatcher.png"));
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void action() {

    }

    @Override
    public void dispose() {
        imgSpinner.dispose();
        imgSpinner = null;
        imgBar.dispose();
        imgBar = null;
        imgCatcher.dispose();
        imgCatcher = null;
    }
}
