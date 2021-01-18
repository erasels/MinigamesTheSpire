package Minigames.games.beatpress;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class Press {
    private AbstractMinigame parent;

    //Rendering Stuff
    private static final int BOTTOM_Y = -AbstractMinigame.SIZE / 2;

    private static final int BASE_WIDTH = 144;
    private static final int BASE_READY_HEIGHT = 60;
    private static final int BASE_PRESSING_HEIGHT = 70;
    private static final int BASE_X = -BASE_WIDTH / 2;

    private static final int MID_WIDTH = 118;
    private static final int MID_READY_HEIGHT = 120;
    private static final int MID_PRESSING_HEIGHT = 160;
    private static final int MID_X = -MID_WIDTH / 2;

    private static final int PRESS_WIDTH = 96;
    private static final int PRESS_READY_HEIGHT = 180;
    private static final int PRESS_PRESSING_Y = BOTTOM_Y + 150;
    private static final int PRESS_X = -PRESS_WIDTH / 2;

    private int baseHeight;
    private int midHeight;

    private int pressHeight; //used to draw only a portion of the texture when moving to ready position from intro
    private int pressY;

    private final Texture base;
    private final Texture mid;
    private final Texture press;

    public STATE state;

    private STATE targetState;
    private boolean inTransition; //if inTransition, state is more of a "target state"
    private float stateTimer;
    private float transitionTime;

    public enum STATE {
        HIDDEN,
        READY,
        PRESS
    }

    public Press(AbstractMinigame parent) {
        this.parent = parent;
        base = ImageMaster.loadImage(makeGamePath("beatpress/base.png"));
        mid = ImageMaster.loadImage(makeGamePath("beatpress/mid.png"));
        press = ImageMaster.loadImage(makeGamePath("beatpress/press.png"));

        state = STATE.HIDDEN;
        targetState = STATE.HIDDEN;
        stateTimer = 0;
        transitionTime = 0;

        baseHeight = 0;
        midHeight = 0;
        pressHeight = 0;
        pressY = BOTTOM_Y;
    }

    public boolean press() {
        if (!inTransition && state == STATE.READY) {
            CardCrawlGame.sound.play(BeatPress.sfxPress);
            targetState = STATE.PRESS;
            stateTimer = 0;
            inTransition = true;
            transitionTime = 0.001f;
            return true;
        }
        return false;
    }

    public void update(float elapsed) {
        stateTimer += elapsed;
        if (inTransition)
        {
            switch (targetState)
            {
                case READY:
                    switch (state) {
                        case HIDDEN:
                            baseHeight = (int) Interpolation.linear.apply(0, BASE_READY_HEIGHT, Math.min(1, stateTimer / (transitionTime / 2.0f)));
                            midHeight = baseHeight + (int) Interpolation.linear.apply(0, MID_READY_HEIGHT - BASE_READY_HEIGHT, Math.min(1, Math.max(0, stateTimer - (transitionTime * 0.25f)) / (transitionTime * 0.5f)));
                            pressHeight = midHeight + (int) Interpolation.linear.apply(0, PRESS_READY_HEIGHT - MID_READY_HEIGHT, Math.min(1, Math.max(0, stateTimer - (transitionTime * 0.33f)) / (transitionTime * 0.66f)));
                            break;
                        case PRESS:
                            baseHeight = (int) Interpolation.linear.apply(BASE_PRESSING_HEIGHT, BASE_READY_HEIGHT, Math.min(1, stateTimer / (transitionTime * 0.5f)));
                            midHeight = (int) Interpolation.linear.apply(MID_PRESSING_HEIGHT, MID_READY_HEIGHT, Math.min(1, stateTimer / (transitionTime * 0.75f)));
                            pressY = (int) Interpolation.linear.apply(PRESS_PRESSING_Y, BOTTOM_Y, Math.min(1, stateTimer / transitionTime));
                            break;
                    }
                    break;
                case PRESS:
                    baseHeight = BASE_PRESSING_HEIGHT;
                    midHeight = MID_PRESSING_HEIGHT;
                    pressY = PRESS_PRESSING_Y;
                    break;
            }

            if (stateTimer >= transitionTime)
            {
                state = targetState;
                inTransition = false;

                switch (state) {
                    case READY:
                        CardCrawlGame.sound.play(BeatPress.sfxPressReady);
                        break;
                    case PRESS:
                        //play "hit" sound if there was a successful hit

                        //Transition back to ready
                        targetState = STATE.READY;
                        stateTimer = 0;
                        inTransition = true;
                        transitionTime = 0.19f;
                        break;
                }
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (baseHeight > 0)
        {
            parent.drawTexture(sb, press, PRESS_X, pressY, PRESS_WIDTH, pressHeight, 0, 0, 0, PRESS_WIDTH, pressHeight, false, false);
            parent.drawTexture(sb, mid, MID_X, BOTTOM_Y, MID_WIDTH, midHeight, 0, MID_WIDTH, 1, false, false);
            parent.drawTexture(sb, base, BASE_X, BOTTOM_Y, BASE_WIDTH, baseHeight, 0, BASE_WIDTH, 1, false, false);
        }
    }

    public void show(float time)
    {
        if (state == STATE.HIDDEN)
        {
            targetState = STATE.READY;
            stateTimer = 0;
            inTransition = true;
            transitionTime = time;
        }
    }

    public void dispose() {
        press.dispose();
        mid.dispose();
        base.dispose();
    }
}
