package Minigames.games.fishing.phases;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.fishing.FishingGame;
import Minigames.games.fishing.fish.AbstractFish;
import Minigames.util.HelperClass;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
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
    private static Texture imgFish; //Could be replaced with custom picture of fish
    private static int fbw = 56, fbh = 53;
    private static Color notCatchingColor = Color.SALMON.cpy();
    private static Color catchingColor = new Color(0.1f, 0.2f, 0.1f, 1f);

    private float spinnerAngle, speed, pos, maxPos;
    protected AbstractFish fish;

    public CatchPhase(FishingGame parent, AbstractGamePhase next) {
        super(parent, next);
        maxPos = bbh - (cbh/2f) - 100f;
        pos = 0;
        fish = parent.fish;
        fish.scaleBehaviorY(maxPos + 100f);
        notCatchingColor.a = 0.75f;
    }

    @Override
    public void initialize() {
        imgBar = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishingBar.png"));
        imgSpinner = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/SpinnyThing.png"));
        imgCatcher = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishCatcher.png"));
        imgFish = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/Fish.png"));
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

        fish.update(fish.isWithinY(pos, pos + cbh));

        if(fish.isCaught()) { //TODO: add escape logic and check as well
            //Add scoring/winning logic to FishingGame
            kill();
        }

        //System.out.println("Pos(" + maxPos + "): " + pos + " Speed: " + speed);
    }

    @Override
    public void render(SpriteBatch sb) {
        float blBound = (-(AbstractMinigame.SIZE/2f));
        parent.drawTexture(sb, imgBar,blBound + (bbw/2f), 0, 0, bbw, bbh, false, false);
        //parent.drawTexture(sb, imgSpinner, 50, 0, spinnerAngle, 12, 32, false, false);
        parent.drawTexture(sb, imgCatcher, blBound + (bbw/2f) + (cbw/2f) - 8f, blBound + (AbstractMinigame.SIZE - bbh) + (cbh/2f) + pos, 0, cbw, cbh, false, false);

        boolean catching = fish.isWithinY(pos, pos + cbh);
        Color fC = catching? catchingColor : notCatchingColor;
        sb.setColor(fC);
        parent.drawTexture(sb, imgFish, blBound + (bbw/2f) + (fbw/2f) - 20f, blBound + (AbstractMinigame.SIZE - bbh) + (fbh/2f) + fish.y - 12f, 0, fbw, fbh, catching, false);
        sb.setColor(Color.WHITE);
        //parent.drawTexture(sb, ImageMaster.WHITE_SQUARE_IMG, 0, blBound + pos, 0, 32, cbh, false, false);
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
