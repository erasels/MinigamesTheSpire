package Minigames.games.beatpress;

import Minigames.games.AbstractMinigame;
import Minigames.util.QueuedSound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class Ball implements Comparable<Ball> {
    private static Texture bouncy, rolly, speedy;

    public static void initialize() {
        bouncy = ImageMaster.loadImage(makeGamePath("beatpress/bouncy.png"));
        rolly = ImageMaster.loadImage(makeGamePath("beatpress/rolly.png"));
        speedy = ImageMaster.loadImage(makeGamePath("beatpress/WEEEEEE.png"));
    }
    public static void dispose() {
        bouncy.dispose();
        rolly.dispose();
        speedy.dispose();
    }

    public enum BallType {
        ROLL, //rolls down. Slowest. 0.8 gap between sounds.
        BOUNCE, //Bounces on each thingy. 0.4 gap.
        SPEED //Comes in from side, bounces from first to the center. 0.4 gap. 0.8ish total?
    }

    private static final int ROLL_DIST = 275;
    private static final int ROLL_END = 25;
    private static final int BOUNCE_DIST = 250;
    private static final int SPEED_DIST = 400;
    //speeedy ball is speeedy and comes from the side (goes from -400 -> 200 -> 0) duration 0.8f
    //Bouncy ball goes 250 (from above) -> 200 -> 100 -> 0 duration 1.0f
    //Rolly ball goes from 275 (from above, falls more on the edge because it moves slow) 225 -> 125 -> 25 duration 2.0f

    //time for C O N S T A N T S
        //Roll
        private static final int ROLL_START_Y = 400;
        private static final int ROLL_Y_1 = 100 + 20;
        private static final int ROLL_Y_2 = 50 + 20;
        private static final int ROLL_Y_3 = 20;

        //Bounce
        private static final int BOUNCE_START_Y = 350; //use a more linear interpolation for first one
        private static final int BOUNCE_Y_1 = 100 + 8;
        private static final int BOUNCE_PEAK_Y_2 = 270 + 8;
        private static final int BOUNCE_Y_2 = 50 + 8;
        private static final int BOUNCE_PEAK_Y_3 = 190 + 8;
        private static final int BOUNCE_Y_3 = 8;

        //S p e e d
        private static final int SPEED_START_Y = 200;
        private static final int SPEED_PEAK_Y_1 = 300;
        private static final int SPEED_Y_1 = 100 + 5;
        private static final int SPEED_PEAK_Y_2 = 300;
        private static final int SPEED_Y_2 = 5;

    //All balls have constant horizontal speed. Vertical movement is the more fancy part.

    private AbstractMinigame parent;

    private BallType type;
    private boolean right;

    public boolean done;
    public BeatPress.PressResult score = BeatPress.PressResult.MISS;

    public boolean triggered; //whether or not this ball has become perfectly hittable yet
    public boolean hit; //whether or not this ball was hit at all
    public boolean failed;

    public float startTime; //The time that this ball will start rolling or bouncing.
    public float hitTime; //The time that this ball should be hit, relative to the start of the game.

    private int startX; //x position is interpolation from this to 0 based on duration
    private int startY; //y position for current step

    private int x, y;

    public Ball(AbstractMinigame parent, BallType type, float hitTime, boolean right) {
        this.parent = parent;

        this.type = type;
        this.right = right;

        this.hitTime = hitTime;
        this.startTime = hitTime - getDuration(type);

        this.x = startX = (right ? 1 : -1) * getDistance();
        this.y = startY = getStartY();

        triggered = false;
        hit = false;
        failed = false;
        done = false;
    }

    //Update using the current game time.
    public void update(float time, float elapsed) {
        float fromStart = time - startTime;
        x = (int) Interpolation.linear.apply(startX, getEnd(), fromStart / (hitTime - startTime));

        //calculate y as a function of x? or as a function of elapsed time
        switch (type)
        {
            case ROLL:
                if (fromStart < 1.0f) //falling from above onto first block
                {
                    y = (int) Interpolation.pow2In.apply(startY, ROLL_Y_1, Math.min(1, fromStart / 0.4f));
                }
                else if (fromStart < 1.8f) //rolling onto second block
                {
                    y = (int) Interpolation.pow2In.apply(ROLL_Y_1, ROLL_Y_2, Math.min(1, (fromStart - 1.0f) / 0.2f));
                }
                else if (!hit || fromStart < 2.0f) //falling from second block to middle
                {
                    y = (int) Interpolation.pow2In.apply(ROLL_Y_2, ROLL_Y_3, (fromStart - 1.8f) / 0.2f);
                    if (y <= -320) { //you must have failed for it to fall this low
                        CardCrawlGame.sound.play(BeatPress.sfxOof);
                        done = true;
                    }
                }
                else
                {
                    //successfully hit
                    y += 1920 * elapsed;

                    if (y > 320)
                        done = true;
                }
                break;
            case BOUNCE:
                if (fromStart < 0.2f) //falling from above onto first block
                {
                    y = (int) Interpolation.linear.apply(startY, BOUNCE_Y_1, fromStart / 0.2f);
                }
                else if (fromStart < 0.6f) //bouncing onto second block
                {
                    y = bounceInterpolation(BOUNCE_Y_1, BOUNCE_PEAK_Y_2, BOUNCE_Y_2, (fromStart - 0.2f) / 0.4f);
                }
                else if (!hit || fromStart < 1.0f) //bouncing from second block to middle
                {
                    y = bounceInterpolation(BOUNCE_Y_2, BOUNCE_PEAK_Y_3, BOUNCE_Y_3, (fromStart - 0.6f) / 0.4f);
                    if (y <= -320) { //you must have failed for it to fall this low
                        CardCrawlGame.sound.play(BeatPress.sfxOof);
                        done = true;
                    }
                }
                else
                {
                    //successfully hit
                    y += 1920 * elapsed;

                    if (y > 320)
                        done = true;
                }
                break;
            case SPEED:
                if (fromStart < 0.4f) //bouncing from left onto first block
                {
                    y = bounceInterpolation(startY, SPEED_PEAK_Y_1, SPEED_Y_1, fromStart / 0.4f);
                }
                else if (!hit || fromStart < 0.8f) //bouncing from first block to middle
                {
                    y = bounceInterpolation(SPEED_Y_1, SPEED_PEAK_Y_2, SPEED_Y_2, (fromStart - 0.4f) / 0.4f);
                    if (y <= -320) { //you must have failed for it to fall this low
                        CardCrawlGame.sound.play(BeatPress.sfxOof);
                        done = true;
                    }
                }
                else
                {
                    //successfully hit
                    y += 1920 * elapsed;

                    if (y > 320)
                        done = true;
                }
                break;
        }

        if (time > hitTime + 0.1f)
        {
            failed = true;
        }
    }

    public void render(SpriteBatch sb) {
        int bottom = y - getSize() / 2;
        int top = y + getSize() / 2;
        int left = x - getSize() / 2;
        int right = left + getSize();

        int height = getSize();
        int originY = 0;

        int width = getSize();
        int originX = 0;

        if (left < -320) //sticking off the left side of the area
        {
            width = right + 320;
            originX = getSize() - width;
        }
        else if (right > 320) //sticking out the right side
        {
            width = 320 - left;
        }
        if (width <= 0)
            return;

        if (bottom < -320) //falling away
        {
            height = top + 320;
            bottom += getSize() - height;
        }
        else if (top > 320) //sticking off the top
        {
            height = 320 - bottom;
            originY = getSize() - height;
        }

        if (height > 0)
            parent.drawTexture(sb, getTexture(), left, bottom, width, height, 0, originX, originY, width, height, false, false);
    }

    public void setResult(BeatPress.PressResult currentResult) {
        this.score = currentResult;
        hit = true;
        if (currentResult == BeatPress.PressResult.NICE) {
            switch (type) {
                case ROLL:
                    parent.queuedSounds.add(new QueuedSound(BeatPress.sfxE, hitTime));
                    break;
                case BOUNCE:
                    parent.queuedSounds.add(new QueuedSound(BeatPress.sfxHighG, hitTime));
                    break;
                default:
                    parent.queuedSounds.add(new QueuedSound(BeatPress.sfxHighC, hitTime));
                    break;
            }
        } else {
            if (type == BallType.ROLL) {
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxWrong, hitTime));
            } else {
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxHighWrong, hitTime));
            }
        }
    }

    public void queueSounds() {
        switch (type) {
            case ROLL:
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxC, startTime + 0.4f));
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxD, startTime + 1.2f));
                break;
            case BOUNCE:
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxHighE, startTime + 0.2f));
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxHighF, startTime + 0.6f));
                break;
            default:
                parent.queuedSounds.add(new QueuedSound(BeatPress.sfxHigherHighC, startTime + 0.4f));
                break;
        }
    }

    public static float getDuration(BallType type)
    {
        switch (type) {
            case ROLL:
                return 2.0f;
            case BOUNCE:
                return 1.0f;
            default:
                return 0.8f;
        }
    }

    private Texture getTexture()
    {
        switch (type) {
            case ROLL:
                return rolly;
            case BOUNCE:
                return bouncy;
            default:
                return speedy;
        }
    }

    private int getSize()
    {
        switch (type) {
            case ROLL:
                return 40;
            case BOUNCE:
                return 16;
            default:
                return 10;
        }
    }

    private int getDistance()
    {
        switch (type) {
            case ROLL:
                return ROLL_DIST;
            case BOUNCE:
                return BOUNCE_DIST;
            default:
                return SPEED_DIST;
        }
    }

    private int getEnd() {
        if (type == BallType.ROLL) {
            return right ? ROLL_END : -ROLL_END;
        }
        return 0;
    }

    private int getStartY()
    {
        switch (type) {
            case ROLL:
                return ROLL_START_Y;
            case BOUNCE:
                return BOUNCE_START_Y;
            default:
                return SPEED_START_Y;
        }
    }

    @Override
    public int compareTo(Ball o) {
        return Float.compare(startTime, o.startTime);
    }

    private static int bounceInterpolation(float start, float peak, float end, float progress) {
        if (progress < 0.5f)
        {
            return (int) Interpolation.pow2Out.apply(start, peak, progress * 2);
        }
        else
        {
            return (int) Interpolation.pow2In.apply(peak, end, (progress - 0.5f) * 2);
        }
    }
}