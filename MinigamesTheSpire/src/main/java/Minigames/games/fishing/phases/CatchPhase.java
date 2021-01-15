package Minigames.games.fishing.phases;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.fishing.FishingGame;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CatchPhase extends AbstractGamePhase {
    private static Texture imgBar;
    private static int bbw = 152, bbh = 600;
    private static Texture imgSpinner;
    private static Texture imgCatcher;
    private static int cbw = 36, cbh = 124;

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
        float blBound = (-(AbstractMinigame.BG_SIZE/2f));
        parent.drawTexture(sb, imgBar,blBound + (bbw/2f), 0, 0, bbw, bbh, false, false);
        //parent.drawTexture(sb, imgSpinner, 50, 0, spinnerAngle, 12, 32, false, false);
        parent.drawTexture(sb, imgCatcher, blBound + (bbw/2f) + (cbw/2f) - 8f, blBound + (AbstractMinigame.BG_SIZE - bbh) + (cbh/2f), 0, cbw, cbh, false, false);
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
