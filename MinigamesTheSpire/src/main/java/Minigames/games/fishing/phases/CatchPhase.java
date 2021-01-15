package Minigames.games.fishing.phases;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.fishing.FishingGame;
import Minigames.util.HelperClass;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CatchPhase extends AbstractGamePhase {
    private static final float SPEED_THRESHOLD = 6.5f;
    private static final float GRAVITY_ACCEL = 180f, PULL_ACCEL = 170f;
    private static final float TERMINAL_VELOCITY = -1000f;
    private static final float BOUNCE_COEFFICIENT = -0.55f;

    private static Texture imgBar;
    private static int bbw = 152, bbh = 600;
    private static Texture imgSpinner;
    private static Texture imgCatcher;
    private static int cbw = 36, cbh = 124;

    private float spinnerAngle, speed, pos, maxPos;

    public CatchPhase(FishingGame parent, AbstractGamePhase next) {
        super(parent, next);
        maxPos = bbh - (cbh/2f) - 100f;
        pos = 0;
    }

    @Override
    public void initialize() {
        imgBar = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishingBar.png"));
        imgSpinner = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/SpinnyThing.png"));
        imgCatcher = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishCatcher.png"));
    }

    @Override
    public void update() {
        float dt = HelperClass.getTime();
        // apply gravity, assuming negative velocity implies going down
        speed = Math.max(TERMINAL_VELOCITY, speed - GRAVITY_ACCEL * dt);
        pos += speed * dt;

        if(pos > maxPos) {
            pos = maxPos;
            if(speed > 0) {
                speed = 0;
            }
        }
        else {
            if(pos <= 0) {
                pos = 0;
                speed *= BOUNCE_COEFFICIENT;
                if (speed < SPEED_THRESHOLD) speed = 0;
            }
        }

        System.out.println("Pos(" + maxPos + "): " + pos + " Speed: " + speed);
    }

    @Override
    public void render(SpriteBatch sb) {
        float blBound = (-(AbstractMinigame.BG_SIZE/2f));
        parent.drawTexture(sb, imgBar,blBound + (bbw/2f), 0, 0, bbw, bbh, false, false);
        //parent.drawTexture(sb, imgSpinner, 50, 0, spinnerAngle, 12, 32, false, false);
        parent.drawTexture(sb, imgCatcher, blBound + (bbw/2f) + (cbw/2f) - 8f, blBound + (AbstractMinigame.BG_SIZE - bbh) + (cbh/2f) + pos, 0, cbw, cbh, false, false);
    }

    @Override
    public void action() {
        float dt = HelperClass.getTime();
        //increase speed
        speed += (PULL_ACCEL + GRAVITY_ACCEL) * dt;
        float inc = 1.05f;
        if(speed < 0)
            inc -= 0.15f;
        speed *= (float) Math.pow(inc, dt / (1f / 30f));

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
