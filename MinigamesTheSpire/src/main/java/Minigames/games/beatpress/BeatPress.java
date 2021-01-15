package Minigames.games.beatpress;

/*
    Game name: Beat Hammer

    Balls roll/bounce, make noise on bouncing/hitting next layer, you have to hit them with the hammer at the right time.
    Hitting at the wrong time results in the wrong sound and the ball falls upwards. You have 3 lives.
    Two kinds of balls: Bouncy/light ones, and rolling/heavy ones. Rolling ones are half as fast as bouncing ones and make deeper sounds.



 */


import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    public static final String sfxHammer = makeID("sfxNoise");

    private Texture title;
    private Texture inputBase;
    private Texture inputFlash;



    private PriorityQueue<Ball> balls = new PriorityQueue<>();
    private ArrayList<Ball> activeBalls = new ArrayList<>();
    //private Press press;

    @Override
    public void initialize() {
        super.initialize();

        balls.clear();
        activeBalls.clear();

        title = ImageMaster.loadImage(makeGamePath("beatpress/title.png"));
        inputBase = ImageMaster.loadImage(makeGamePath("beatpress/noflash.png"));
        inputFlash = ImageMaster.loadImage(makeGamePath("beatpress/flash.png"));
        //generateBalls();
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase)
        {
            case 0:
                time = 0;
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

    }

    @Override
    public void dispose() {
        super.dispose();

    }

    @Override
    protected BindingGroup getBindings() {
        return null;
    }
}
