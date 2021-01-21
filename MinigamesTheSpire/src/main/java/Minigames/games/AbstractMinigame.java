package Minigames.games;

import Minigames.games.input.bindings.BindingGroup;
import Minigames.patches.Input;
import Minigames.util.QueuedSound;
import basemod.interfaces.TextReceiver;
import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.PriorityQueue;

import static Minigames.Minigames.makeGamePath;


//When option to play a game is chosen:
/*
    Minigame area will enter screen in some way. Fade in, and fade out rest of screen?
    Valid controls will be displayed for 3 seconds, then fade out for the last second.
    Minigame will begin.
 */
public abstract class AbstractMinigame implements TextReceiver {
    //Game stuff
    protected boolean isPlaying;
    protected boolean isDone;
    protected boolean blockingInput;

    protected float time = 0;

    public int phase = -2;
    //-2: Minigame area fading in.
    //-1: Displaying controls.
    //0 and up: Whatever you want. Use a switch statement, just stay at 0, doesn't really matter.

    //A sound thing, if you want it
    public final PriorityQueue<QueuedSound> queuedSounds = new PriorityQueue<>();

    //Rendering stuff
    //640x640
    public static final int SIZE = 640;

    protected int x = Settings.WIDTH / 2, y = Settings.HEIGHT / 2;
    protected float scale = 0.0f;
    protected float angle = 0.0f;

    private final Color c;

    //background
    public static final int BG_SIZE = 648;
    protected Texture background;

    public AbstractMinigame()
    {
        isPlaying = false;
        isDone = false;
        blockingInput = false;

        c = Color.WHITE.cpy();
    }

    //load necessary assets, if any
    public void initialize() {
        isPlaying = true;

        TextInput.startTextReceiver(this);
        blockingInput = true;

        BindingGroup b = getBindings();
        b.allowEsc();
        Input.setBindings(b);
        background = ImageMaster.loadImage(makeGamePath("tempBG.png"));
        transformScale(getMaxScale(), Settings.FAST_MODE ? 0.5f : 1.0f);
    }

    //dispose of loaded assets, if any
    public void dispose() {
        background.dispose();
        Input.clearBindings();
        TextInput.stopTextReceiver(this);
    }

    public boolean playing() {
        return isPlaying;
    }

    public boolean gameDone() {
        return isDone;
    }

    //will be called as long as isPlaying is true
    public void update(float elapsed) {
        if (CardCrawlGame.isPopupOpen || AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) {
            if (blockingInput)
            {
                blockingInput = false;
                TextInput.stopTextReceiver(this);
            }
        }
        else if (!blockingInput)
        {
            blockingInput = true;
            TextInput.startTextReceiver(this);
        }

        Input.update(elapsed);

        if (scaleProgress < scaleTime)
        {
            scaleProgress += elapsed;
            this.scale = Interpolation.linear.apply(initialScale, targetScale, Math.min(1, scaleProgress / scaleTime));
        }

        QueuedSound s = queuedSounds.peek();
        while (s != null && time > s.time)
        {
            CardCrawlGame.sound.play(s.key);
            queuedSounds.remove();
            s = queuedSounds.peek();
        }

        switch (phase) {
            case -2:
                time += elapsed;

                //c.a = Math.min(1, time * (Settings.FAST_MODE ? 2.0f : 1.0f));

                if (time > (Settings.FAST_MODE ? 0.5f : 1.0f))
                {
                    time = 0;
                    phase = -1;
                }
                break;
            case -1:
                time += elapsed;

                if (time > (Settings.FAST_MODE ? 0.2f : 0.5f))
                {
                    time = 0;
                    phase = 0;
                }
                break;
        }
    }

    public void render(SpriteBatch sb)
    {
        //render background
        sb.setColor(c);
        drawTexture(sb, background, 0, 0, BG_SIZE);

        if (phase == -1)
        {
            //display controls
        }
    }

    //rendering utility

    //These methods draw/scale/rotate whatever they are passed based on the scale/position/angle of the minigame.
    public void drawTexture(SpriteBatch sb, Texture t, float cX, float cY, int size)
    {
        drawTexture(sb, t, cX, cY, 0, size, size, false, false);
    }
    public void drawTexture(SpriteBatch sb, Texture t, float cX, float cY, float angle, int baseWidth, int baseHeight, boolean flipX, boolean flipY)
    {
        sb.draw(t, x + cX - baseWidth / 2.0f, y + cY - baseHeight / 2.0f, -(cX - baseWidth / 2.0f), -(cY - baseHeight / 2.0f), baseWidth, baseHeight, scale, scale, this.angle + angle, 0, 0, baseWidth, baseHeight, flipX, flipY);
    }
    public void drawTexture(SpriteBatch sb, Texture t, float x, float y, float width, float height, float angle, int originWidth, int originHeight, boolean flipX, boolean flipY)
    {
        sb.draw(t, this.x + x, this.y + y, -x, -y, width, height, scale, scale, this.angle + angle, 0, 0, originWidth, originHeight, flipX, flipY);
    }
    public void drawTexture(SpriteBatch sb, Texture t, float x, float y, float width, float height, float angle, int originX, int originY, int originWidth, int originHeight, boolean flipX, boolean flipY)
    {
        sb.draw(t, this.x + x, this.y + y, -x, -y, width, height, scale, scale, this.angle + angle, originX, originY, originWidth, originHeight, flipX, flipY);
    }


    // Position/Scale control
    private float initialScale = 0.0f; //scale at start of transform
    private float targetScale = 0.0f; //scale to end at
    private float scaleProgress = 0.0f; //time elapsed
    private float scaleTime = 2.0f; //time to perform transform

    public void setScale(float newScale)
    {
        this.scale = newScale;
        this.initialScale = scale;
        this.scaleProgress = 1;
        this.scaleTime = 1;
    }

    public void transformScale(float targetScale, float time)
    {
        initialScale = scale;
        this.targetScale = targetScale;
        scaleProgress = 0.0f;
        scaleTime = time;
    }

    public float getMaxScale()
    {
        return Settings.scale; // eh this is just simpler and will be more consistent.

        /*
        float maxSize = 0.8f * Math.min(Settings.WIDTH, Settings.HEIGHT); //Settings.HEIGHT will pretty much always be smaller but maybe someone fucked with something, who knows

        float ratio = ((int)(maxSize / SIZE * 4)) / 4.0f; //round to lower 0.25f

        if (ratio < 0.25f) //wtf
        {
            ratio = maxSize / SIZE;
        }

        return ratio;*/
    }

    public String getOption() {
        return "[wow] this is just hardcoded! (Please don't hardcode your strings.)";
    }

    //Input binding stuff

    protected abstract BindingGroup getBindings();

    //Other general utility stuff
    public boolean isWithinArea(float x, float y)
    {
        float offset = SIZE * scale * 0.5f;
        return x >= this.x - offset && x <= this.x + offset &&
                y >= this.y - offset && y <= this.y + offset;
    }

    //converts a Vector2 based on the bottom left of screen to a vector based on current x and y of game
    public Vector2 getRelativeVector(Vector2 base)
    {
        Vector2 cpy = base.cpy();
        cpy.x -= x; //convert to be based on centerpoint of area
        cpy.y -= y;

        cpy.scl(1 / scale); //scaling

        return cpy;
    }

    public boolean hasInstructionScreen = true;
    public void setupInstructionScreen(GenericEventDialog event) {
        event.updateBodyText("UPDATE BODY TEXT\n\nSet hasInstructionScreen to false in your constructor if you have no instructions!");
        event.setDialogOption("This event has no instructions!");
    }
    public boolean instructionsButtonPressed(int buttonIndex) {
        //If you wanna do fancy stuff, you can track pages in your event and have multiple pages of instructions.
        //Return true here to start the game.
        return true;
    }
    public boolean instructionsButtonPressed(int buttonIndex, GenericEventDialog event) {
        //If you wanna do fancy stuff, you can track pages in your event and have multiple pages of instructions.
        //Return true here to start the game.
        return instructionsButtonPressed(buttonIndex);
    }

    public boolean hasPostgameScreen = true;
    public void setupPostgameScreen(GenericEventDialog event) {
        event.updateBodyText("UPDATE BODY TEXT\n\nSet hasPostgameScreen to false in your constructor if you have no post-game screen!");
        event.setDialogOption("This event has no special postgame screen!");
    }
    public boolean postgameButtonPressed(int buttonIndex) {
        //If you wanna do fancy stuff, you can track pages in your event and have multiple pages.
        //Return true here to go to ending of event.
        return true;
    }
    public boolean postgameButtonPressed(int buttonIndex, GenericEventDialog event) {
        //If you wanna do fancy stuff, you can track pages in your event and have multiple pages.
        //Return true here to go to ending of event.
        return postgameButtonPressed(buttonIndex);
    }




    //This class implements TextReceiver to not screw over the console when it disables input, by being compatible with basemod's text input stuff.
    @Override
    public boolean acceptCharacter(char c) {
        return false;
    }

    @Override
    public String getCurrentText() {
        return "";
    }

    @Override
    public void setText(String s) {
    }

    //This is for a future update of TextReceiver.
    @Override
    public boolean isDone() {
        if (CardCrawlGame.isPopupOpen || AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE || isDone || !CardCrawlGame.isInARun()) {
            blockingInput = false;
            if (Input.processor.inactiveBindings == null) {
                if (isDone)
                {
                    Input.clearBindings();
                }
                else
                {
                    Input.processor.deactivate();
                }
            }
            return true;
        }
        return false;
    }

    //for when we need minigames to have conditional spawning I guess
    public boolean canSpawn() {
        return true;
    }

    // TODO: Figure out actNum behaviour - and switch if necessary

    // Determines if a minigame can spawn in the event ActOneArcade
    public boolean canSpawnInActOneEvent(){ return true; }
    // Determines if a minigame can spawn in the event ActTwoArcade
    public boolean canSpawnInActTwoEvent(){ return true; }
    // Determines if a minigame can spawn in the event ActTwoArcade
    public boolean canSpawnInActThreeEvent(){ return true; }

    public abstract AbstractMinigame makeCopy();
}
