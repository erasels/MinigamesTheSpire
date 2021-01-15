package Minigames.games.fishing.phases;

import Minigames.Minigames;
import Minigames.games.fishing.FishingGame;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CatchPhase extends AbstractGamePhase {
    private static Texture imgBar;
    private static Texture imgSpinner;
    private static Texture imgCatcher;

    private float spinnerAngle, speed;

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
    public void render(SpriteBatch sb) {
        parent.drawTexture(sb, imgBar, 0, 0, 0, 152, 600, false, false);
        parent.drawTexture(sb, imgSpinner, 50, 0, spinnerAngle, 12, 32, false, false);
        parent.drawTexture(sb, imgCatcher, 100, 0, 0, 36, 124, false, false);
    }

    @Override
    public void action() {
        //increase speed
        spinnerAngle += 5f;
        //play sound
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
