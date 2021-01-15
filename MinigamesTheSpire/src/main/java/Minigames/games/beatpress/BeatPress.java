package Minigames.games.beatpress;

/*
    Game name: Beat Hammer

    Balls roll/bounce, make noise on bouncing/hitting next layer, you have to hit them with the hammer at the right time.
    Hitting at the wrong time results in the wrong sound and the ball falls upwards. You have 3 lives.
    Two kinds of balls: Bouncy/light ones, and rolling/heavy ones. Rolling ones are half as fast as bouncing ones and make deeper sounds.



 */


import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.util.QueuedSound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.ArrayList;
import java.util.PriorityQueue;

import static Minigames.Minigames.makeGamePath;
import static Minigames.Minigames.makeID;

public class BeatPress extends AbstractMinigame {
    public static final String sfxC = makeID("sfxC");
    public static final String sfxD = makeID("sfxD");
    public static final String sfxE = makeID("sfxE");
    public static final String sfxWrong = makeID("sfxWrong");
    public static final String sfxHighE = makeID("sfxHighE");
    public static final String sfxHighF = makeID("sfxHighF");
    public static final String sfxHighG = makeID("sfxHighG");
    public static final String sfxHighWrong = makeID("sfxHighWrong");
    public static final String sfxOof = makeID("sfxOof");
    public static final String sfxPress = makeID("sfxPress");
    public static final String sfxPressReady = makeID("sfxPressReady");

    private Texture title;
    private Texture input;
    private Texture sides;


    private PriorityQueue<Ball> balls = new PriorityQueue<>();
    private ArrayList<Ball> activeBalls = new ArrayList<>();

    private Press press;

    @Override
    public void initialize() {
        super.initialize();

        balls.clear();
        activeBalls.clear();

        title = ImageMaster.loadImage(makeGamePath("beatpress/title.png"));
        input = ImageMaster.loadImage(makeGamePath("beatpress/input.png"));
        sides = ImageMaster.loadImage(makeGamePath("beatpress/sides.png"));

        press = new Press(this);
        //generateBalls();
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase)
        {
            case 0:
                if (time == 0)
                    CardCrawlGame.sound.play(sfxOof, 0.1f);

            case 1:
                press.update(elapsed);
                time += elapsed;
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        drawTexture(sb, sides, 0, 0, SIZE);
        switch (phase)
        {
            case 0:
                drawTexture(sb, title, 0, 0, SIZE);
                if (time > 1) {
                    if (time % 1 < 0.5f) {
                        drawTexture(sb, input, 0, 0, SIZE);
                    }
                    if (time > 60) {
                        time -= 30; //I mean, nobody's gonna sit here til it overflows.... right..?
                    }
                }
                break;
            case 1:
                press.render(sb);
                break;
        }
    }

    public void boop() {
        switch (phase)
        {
            case -1:
            case 0:
                CardCrawlGame.sound.play(sfxE);
                queuedSounds.add(new QueuedSound(sfxHighE, 0.4f));
                press.show(Settings.FAST_MODE ? 0.8f : 1.6f);
                phase = 1;
                time = 0;
                break;
            case 1:
            case 2:
                if (press.press()) {
                    //The Press has been Pressed

                    //Test if this is a valid hit
                    //Possiblities are: Spot On, A Bit Off, and Whiff
                }
                break;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.bindAll("BOOP", this::boop, null, null);

        bindings.addMouseBind((x, y, pointer)->this.isWithinArea(x, y), (p)->boop());
        return bindings;
    }
}
