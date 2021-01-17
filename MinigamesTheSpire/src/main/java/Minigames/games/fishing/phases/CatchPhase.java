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
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.commons.lang3.math.NumberUtils;

public class CatchPhase extends AbstractGamePhase {
    public static final float GAME_TIME = 40f; //40 seconds before fish escapes
    private static final float MINIMUM_BOUNCE_THRESHOLD = 10f;
    private static final float GRAVITY_ACCEL = 200f, PULL_ACCEL = 190f;
    private static final float TERMINAL_VELOCITY = -1000f;
    private static final float BOUNCE_COEFFICIENT = -0.55f;
    private static final float FBAR_GROUND_OFFSET = 12f;

    private static Texture imgBar;
    private static int bbw = 152, bbh = 600;
    private static Texture imgCatcher;
    private static int cbw = 36, cbh = 124;
    private static Texture imgFish; //Could be replaced with custom picture of fish
    private static int fbw = 56, fbh = 53;
    private static Texture imgCrank;
    private static Color notCatchingColor = new Color(0.75f, 0.65f, 0.65f, 0.75f);
    private static Color catchingColor = new Color(0.75f, 0.85f, 0.75f, 1f);

    private float gameTime;
    private float spinnerAngle, speed, pos, maxPos;
    private float bobTimer, reelTimer;

    private String timeString;

    protected AbstractFish fish;

    public CatchPhase(FishingGame parent, AbstractGamePhase next) {
        super(parent, next);
        maxPos = bbh - (cbh / 2f) - 100f;
        pos = 0;
        fish = parent.fish;
        fish.scaleBehavior(GAME_TIME, maxPos + 100f);
        gameTime = GAME_TIME;
    }

    @Override
    public void initialize() {
        imgBar = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishingBar.png"));
        imgCatcher = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishCatcher.png"));
        imgFish = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/Fish.png"));
        imgCrank = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/Crank.png"));

        timeString = FishingGame.uiStrings.TEXT_DICT.get("TIME");
    }

    @Override
    public void update() {
        if (!isDone && !waiting) {
            float dt = HelperClass.getTime();

            gameTime -= dt;

            // apply gravity, assuming negative velocity implies going down
            speed = Math.max(TERMINAL_VELOCITY, speed - GRAVITY_ACCEL * dt);
            pos += speed * dt;

            if (pos > maxPos) {
                pos = maxPos;
                if (speed > 0) {
                    speed = 0;
                }
            } else {
                if (pos <= 0) {
                    pos = 0;
                    speed *= BOUNCE_COEFFICIENT;
                    if (speed < MINIMUM_BOUNCE_THRESHOLD) speed = 0;
                }
            }

            boolean fishBeingCaught = fish.isWithinY(pos, pos + cbh);
            fish.update(fishBeingCaught);
            isDone = fish.isCaught() || gameTime <= 0;
            if (!isDone) {
                float gt = HelperClass.getTime();
                bobTimer -= gt;
                reelTimer -= gt;
                if (bobTimer <= 0) {
                    CardCrawlGame.sound.play(fishBeingCaught ? FishingGame.sHit : FishingGame.sBob);
                    bobTimer = fishBeingCaught ? FishingGame.timeHit : FishingGame.timeBob;
                }
            } else {
                //Reduce wait time because there is no next game phase
                parent.waitTimer = 0.2f;

                if (fish.isCaught()) {
                    parent.fishCaught = true;
                    CardCrawlGame.sound.play(FishingGame.sWaterSploosh);
                } else {
                    parent.fishCaught = false;
                    CardCrawlGame.sound.play(FishingGame.sWaterPlop);
                }
            }
        } else {
            kill();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        float blBound = (-(AbstractMinigame.SIZE / 2f));

        //Render game time
        if(!isDone && !waiting) {
            //System.out.printf("isdone = %b, waiting = %b, waitTimer = %f, gameTime = %f", isDone, waiting, parent.waitTimer, gameTime);
            Color col;
            if (gameTime > GAME_TIME * 0.66f) {
                col = Color.SKY;
            } else if (gameTime > GAME_TIME * 0.33f) {
                col = Color.ORANGE;
            } else {
                col = Color.RED;
            }
            parent.displayTimer(sb, timeString + HelperClass.get2DecString(gameTime), col);
        }


        //Render fishing bar
        parent.drawTexture(sb, imgBar, blBound + (bbw / 2f), 0, 0, bbw, bbh, false, false);

        //Render catcher area
        parent.drawTexture(sb, imgCatcher, blBound + (bbw / 2f) + (cbw / 2f) - 8f, blBound + (AbstractMinigame.SIZE - bbh) + (cbh / 2f) + pos, 0, cbw, cbh, false, false);

        //Render fish catching progress
        if (fish.hp > fish.mHp * 0.66f) {
            sb.setColor(Color.RED);
        } else if (fish.hp > fish.mHp * 0.33f) {
            sb.setColor(Color.ORANGE);
        } else {
            sb.setColor(Color.GREEN);
        }
        int height = (int) ((fish.hp / fish.mHp) * (bbh - FBAR_GROUND_OFFSET));
        parent.drawTexture(sb, ImageMaster.WHITE_SQUARE_IMG, blBound + bbw - 16f, blBound + (AbstractMinigame.SIZE - bbh) + (bbh / 2f) + ((height - (bbh - FBAR_GROUND_OFFSET)) / 2f) - 8f, 0, 10, (NumberUtils.max(height - 8, 0)), false, false);

        //Render Fish
        boolean catching = fish.isWithinY(pos, pos + cbh);
        Color fC = catching ? catchingColor : notCatchingColor;
        sb.setColor(fC);
        parent.drawTexture(sb, imgFish, blBound + (bbw / 2f) + (fbw / 2f) - 20f + (catching? getFishShake() : 0), blBound + (AbstractMinigame.SIZE - bbh) + (fbh / 2f) + fish.y - FBAR_GROUND_OFFSET, 0, fbw, fbh, catching, false);
        sb.setColor(Color.WHITE);

        //Render Crank
        parent.drawTexture(sb, imgCrank, 75, 0, spinnerAngle, 57, 58, false, false);
    }

    @Override
    public void action() {
        if (!isDone && !waiting) {
            float dt = HelperClass.getTime();
            //increase speed
            speed += (PULL_ACCEL + GRAVITY_ACCEL) * dt;
            float inc = 1.05f;
            if (speed < 0)
                inc -= 0.15f;
            speed *= (float) Math.pow(inc, dt / (1f / 30f));

            spinnerAngle += 5f;

            //play sound
            if (reelTimer <= 0) {
                boolean fishBeingCaught = fish.isWithinY(pos, pos + cbh);
                CardCrawlGame.sound.play(fishBeingCaught ? FishingGame.sLongReel : FishingGame.sShortReel);
                reelTimer = fishBeingCaught ? FishingGame.timeLReel : FishingGame.timeSReel;
            }
        }
    }

    private static final float FISH_SHAKE_TIME = 0.15f;
    private float fishShakeTimer = FISH_SHAKE_TIME;
    private boolean switchFishShake = false;
    private float getFishShake() {
        fishShakeTimer -= HelperClass.getTime();
        if(fishShakeTimer <= 0) {
            fishShakeTimer = FISH_SHAKE_TIME;
            switchFishShake = !switchFishShake;
        }
        if(switchFishShake) {
            return Interpolation.linear.apply(12f, -12f, 1 - fishShakeTimer/FISH_SHAKE_TIME);
        } else {
            return Interpolation.linear.apply(-12f, 12f, 1 - fishShakeTimer/FISH_SHAKE_TIME);
        }
    }

    @Override
    protected void killAction() {
        CardCrawlGame.sound.stop(FishingGame.sHit);
        CardCrawlGame.sound.stop(FishingGame.sBob);
        CardCrawlGame.sound.stop(FishingGame.sLongReel);
        CardCrawlGame.sound.stop(FishingGame.sShortReel);
    }

    @Override
    public void dispose() {
        imgBar.dispose();
        imgBar = null;
        imgCatcher.dispose();
        imgCatcher = null;
        imgFish.dispose();
        imgFish = null;
        imgCrank.dispose();
        imgCrank = null;
    }
}
