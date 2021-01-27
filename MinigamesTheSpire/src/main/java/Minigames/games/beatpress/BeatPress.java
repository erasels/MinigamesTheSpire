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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

import static Minigames.Minigames.makeGamePath;
import static Minigames.Minigames.makeID;

public class BeatPress extends AbstractMinigame {
    public static final String ID = makeID("BeatPress");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public static final String sfxC = makeID("sfxC"); //rollers go C -> D -> E
    public static final String sfxD = makeID("sfxD");
    public static final String sfxE = makeID("sfxE");
    public static final String sfxWrong = makeID("sfxWrong");
    public static final String sfxHighC = makeID("sfxHighC");
    public static final String sfxHighD = makeID("sfxHighD"); //D and C are used for "countdown"
    public static final String sfxHighE = makeID("sfxHighE"); //bouncy ones go E -> F -> G
    public static final String sfxHighF = makeID("sfxHighF");
    public static final String sfxHighG = makeID("sfxHighG");
    public static final String sfxHigherHighC = makeID("sfxVeryHighC"); //speedy boi go Very High C -> High C
    public static final String sfxHighWrong = makeID("sfxHighWrong");
    public static final String sfxOof = makeID("sfxOof");
    public static final String sfxPress = makeID("sfxPress");
    public static final String sfxPressReady = makeID("sfxPressReady");

    private static final ArrayList<BeatPattern> basicPatterns = new ArrayList<>();
    private static final ArrayList<BeatPattern> mediumPatterns = new ArrayList<>();
    private static final ArrayList<BeatPattern> heckPatterns = new ArrayList<>();

    static {
        basicPatterns.add(new BeatPattern("!B0.8 ?B1.6 ?R2.4", BeatPattern.PatternType.NORMAL));
        basicPatterns.add(new BeatPattern("!R1.6 !B0.8 =B1.6 !S0.8", BeatPattern.PatternType.NORMAL));
        basicPatterns.add(new BeatPattern("!R1.6 =R3.2 !B0.8", BeatPattern.PatternType.NOFINISH));
        basicPatterns.add(new BeatPattern("!R1.6 !S0.8 !R2.4 !B0.8", BeatPattern.PatternType.NOFINISH));
        basicPatterns.add(new BeatPattern("!R1.6 =S1.6 !R1.6 =S1.6", BeatPattern.PatternType.NOFINISH));
        basicPatterns.add(new BeatPattern("!R1.6 !R1.6 !R2.4 =B0.8", BeatPattern.PatternType.NOFINISH));
        basicPatterns.add(new BeatPattern("!B0.8 =R1.6 !B0.8 !S1.6", BeatPattern.PatternType.FINISH));
        basicPatterns.add(new BeatPattern("!R1.6 =S0.8", BeatPattern.PatternType.FINISH));

        mediumPatterns.add(new BeatPattern("!B0.8 !R0.8 =S0.8 !B1.6 !R0.8", BeatPattern.PatternType.NORMAL));
        mediumPatterns.add(new BeatPattern("!B1.2 !R0.4 !B2.8 !R0.4", BeatPattern.PatternType.NORMAL));
        mediumPatterns.add(new BeatPattern("!B0.8 =B1.6 !B2.0 !R0.4", BeatPattern.PatternType.NORMAL));
        mediumPatterns.add(new BeatPattern("!R1.6 =S0 !B0.8 !R2.4 =R0.4 =S0.4", BeatPattern.PatternType.NORMAL));
        mediumPatterns.add(new BeatPattern("!B0.8 =R1.6 !S0.8", BeatPattern.PatternType.FINISH));
        mediumPatterns.add(new BeatPattern("!B0.8 !S0.8 !S0.8", BeatPattern.PatternType.FINISH));

        heckPatterns.add(new BeatPattern("!R1.6 =S0.4 !B0.8 !S0.8 !B0.8 =S0.4", BeatPattern.PatternType.NORMAL));
        heckPatterns.add(new BeatPattern("!B0.8 !B0 !B1.6 !B0 !S1.2 !S0 !S0.8 !S0 !B1.2 !B0", BeatPattern.PatternType.NORMAL));
        heckPatterns.add(new BeatPattern("!B0.8 =B1.6 !S0.8 !B0.8 !S0.8 !R1.6", BeatPattern.PatternType.NOFINISH));
        heckPatterns.add(new BeatPattern("!B0.8 !S0 =S1.2 !B1.2 !S0 !R3.2", BeatPattern.PatternType.NOFINISH));
        heckPatterns.add(new BeatPattern("!S0.4 !S1.2 !S1.2 !S1.2", BeatPattern.PatternType.FINISH));
    }

    private Texture title;
    private Texture input;
    private Texture sides;

    private Texture grade;
    private Texture perfect;
    private Texture notbad;
    private Texture ouch;

    //excellent naming
    private Texture thingies;
    private Texture stringies;

    private static final int THINGY_WIDTH = 90;
    private static final int THINGY_HEIGHT = 200;
    private static final int CLOSE_LEFT_THINGY_OFFSET = -100 - THINGY_WIDTH / 2;
    private static final int CLOSE_RIGHT_THINGY_OFFSET = 100 - THINGY_WIDTH / 2;
    private static final int FAR_LEFT_THINGY_OFFSET = -200 - THINGY_WIDTH / 2;
    private static final int FAR_RIGHT_THINGY_OFFSET = 200 - THINGY_WIDTH / 2;
    private static final int LOW_THINGY_Y = -50 - THINGY_HEIGHT / 2;
    private static final int HIGH_THINGY_Y = -THINGY_HEIGHT / 2;
    private static final int LOW_THINGY_START_Y = LOW_THINGY_Y + 470;
    private static final int HIGH_THINGY_START_Y = HIGH_THINGY_Y + 470;
    private int highThingyY, lowThingyY;


    //First sound of first ball should occur at exactly 0.0f
    //0.4f gap between bounces of bouncy balls
    //0.8f gap between falls of rolling balls
    private PriorityQueue<Ball> balls = new PriorityQueue<>();
    private ArrayList<Ball> activeBalls = new ArrayList<>();

    private ArrayList<Ball> allBalls = new ArrayList<>(); //for tallying score after

    private PressResult currentResult = PressResult.MISS; //the result if the press is pressed after a specific frame of update
    private final ArrayList<Ball> hitBalls = new ArrayList<>();

    protected enum PressResult {
        MISS,
        NOT_QUITE,
        NICE
    }

    private Press press;

    private Rating finalRating = Rating.NOT_BAD;

    private enum Rating {
        PERFECT,
        NOT_BAD,
        OUCH
    }

    public BeatPress() {
        super();
    }

    @Override
    public String getOption() {
        return OPTIONS[0];
    }

    @Override
    public void setupInstructionScreen(GenericEventDialog event) {
        event.updateBodyText(DESCRIPTIONS[0]);
        event.setDialogOption(OPTIONS[1]);
    }

    private int heal = 0;
    private int maxhp = 0;
    @Override
    public void setupPostgameScreen(GenericEventDialog event) {
        switch (finalRating) {
            case OUCH:
                if (AbstractDungeon.actNum <= 2) {
                    event.updateBodyText(DESCRIPTIONS[1]);
                    event.setDialogOption(OPTIONS[2]);
                }
                else {
                    event.updateBodyText(DESCRIPTIONS[2]);
                    event.setDialogOption(OPTIONS[3]);
                }
                break;
            case NOT_BAD:
                heal = 5;
                maxhp = 5;
                event.updateBodyText(DESCRIPTIONS[3]);
                event.setDialogOption(OPTIONS[4]);
                break;
            case PERFECT:
                event.updateBodyText(DESCRIPTIONS[4]);
                maxhp = 5 + (AbstractDungeon.actNum * 5);
                heal = 5 + AbstractDungeon.actNum * 10; //only used in act 1 or 2.
                switch (AbstractDungeon.actNum) {
                    case 1:
                    case 2:
                        event.setDialogOption(OPTIONS[5] + maxhp + OPTIONS[7] + heal + OPTIONS[8]);
                        break;
                    default:
                        event.setDialogOption(OPTIONS[5] + maxhp + OPTIONS[6]);
                        break;
                }
                break;
        }
    }

    @Override
    public boolean postgameButtonPressed(int buttonIndex) {
        switch (finalRating) {
            case OUCH:
                if (AbstractDungeon.actNum > 2) {
                    AbstractDungeon.player.damage(new DamageInfo(null, 1, DamageInfo.DamageType.HP_LOSS));
                }
                break;
            case NOT_BAD:
                AbstractDungeon.player.increaseMaxHp(maxhp, true);
                AbstractDungeon.player.heal(heal);
                break;
            case PERFECT:
                //max hp+
                AbstractDungeon.player.increaseMaxHp(maxhp, true);

                if (AbstractDungeon.actNum > 2) {
                    //heal to full
                    AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth);
                }
                else
                {
                    //heal
                    AbstractDungeon.player.heal(heal);
                }
                break;
        }
        return true;
    }

    private int patternCount = 6;
    private float mediumRate = 0.3f;
    private float hardRate = 0.1f;
    public void setDifficulty(int patternCount, float mediumRate, float hardRate) {
        this.patternCount = patternCount;
        this.mediumRate = mediumRate;
        this.hardRate = hardRate;
    }

    @Override
    public void initialize() {
        super.initialize();

        Ball.initialize();
        //One pattern is 6.4 seconds long.

        switch (AbstractDungeon.actNum) {
            case 1:
                setDifficulty(5, 0.3f, 0);
                break;
            case 2:
                setDifficulty(7, 0.5f, 0.1f);
                break;
            case 3:
                setDifficulty(9, 0.5f, 0.25f);
                break;
            default:
                setDifficulty(9, 0.65f, 0.35f);
                break;
        }

        generateBalls(patternCount, mediumRate, hardRate);

        title = ImageMaster.loadImage(makeGamePath("beatpress/title.png"));
        input = ImageMaster.loadImage(makeGamePath("beatpress/input.png"));
        sides = ImageMaster.loadImage(makeGamePath("beatpress/sides.png"));

        grade = ImageMaster.loadImage(makeGamePath("beatpress/grade.png"));
        perfect = ImageMaster.loadImage(makeGamePath("beatpress/perfect.png"));
        notbad = ImageMaster.loadImage(makeGamePath("beatpress/notbad.png"));
        ouch = ImageMaster.loadImage(makeGamePath("beatpress/ouch.png"));

        thingies = ImageMaster.loadImage(makeGamePath("beatpress/thingy.png"));
        stringies = ImageMaster.loadImage(makeGamePath("beatpress/string.png"));

        highThingyY = HIGH_THINGY_START_Y;
        lowThingyY = LOW_THINGY_START_Y;

        press = new Press(this);

        CardCrawlGame.music.silenceBGM();
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

                lowThingyY = (int) Interpolation.pow2In.apply(LOW_THINGY_START_Y, LOW_THINGY_Y, Math.min(1, time / 0.4f));
                highThingyY = (int) Interpolation.pow2In.apply(HIGH_THINGY_START_Y, HIGH_THINGY_Y, Math.min(1, time / 0.4f));

                if (press.state != Press.STATE.HIDDEN)
                {
                    phase = 2;
                    time = -1.5f;

                    /*queuedSounds.add(new QueuedSound(sfxHighE, -1.2f));
                    queuedSounds.add(new QueuedSound(sfxHighD, -0.8f));
                    queuedSounds.add(new QueuedSound(sfxHighC, -0.4f));*/
                }
                break;
            case 2:
                //time for balls
                press.update(elapsed);

                currentResult = PressResult.MISS;
                hitBalls.clear();

                Ball b = balls.peek();
                while (b != null && time >= b.startTime)
                {
                    b.queueSounds();
                    activeBalls.add(balls.remove());
                    b = balls.peek();
                }

                Iterator<Ball> ballIterator = activeBalls.iterator();
                while (ballIterator.hasNext())
                {
                    b = ballIterator.next();

                    b.update(time, elapsed); //and balls use time

                    float gap = b.hitTime - time;

                    if (gap < 0.04f && (gap > -0.04f || !b.triggered)) //ensure there is always at least one frame where you can get a perfect
                    {
                        currentResult = PressResult.NICE;
                        b.triggered = true;
                        hitBalls.add(b);
                    }
                    else if (gap < 0.08f && gap > -0.08f)
                    {
                        if (currentResult == PressResult.MISS)
                            currentResult = PressResult.NOT_QUITE;
                        hitBalls.add(b);
                    }

                    if (b.done)
                    {
                        ballIterator.remove();
                    }
                }

                time += elapsed;

                if (activeBalls.isEmpty() && balls.isEmpty())
                {
                    time = 0;
                    phase = 3;
                    calculateRating();
                }
                break;
            case 3:
                time += elapsed;
                //short pause before transition to score

                if (time > 2.0f) {
                    time = 0;
                    phase = 4;
                    CardCrawlGame.sound.play(sfxPressReady);
                }
                break;
            case 4:
                time += elapsed;
                if (time > 1.6f)
                {
                    time = 0;
                    phase = 5;
                    playScoreJingle();
                }
                break;
            case 5:
                time += elapsed;
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        if (phase < 4)
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
            case 2:
                //b alls
                for (Ball b : activeBalls)
                    b.render(sb);
            case 1:
            case 3:
                drawThingy(sb, CLOSE_LEFT_THINGY_OFFSET, lowThingyY);
                drawThingy(sb, CLOSE_RIGHT_THINGY_OFFSET, lowThingyY);
                drawThingy(sb, FAR_LEFT_THINGY_OFFSET, highThingyY);
                drawThingy(sb, FAR_RIGHT_THINGY_OFFSET, highThingyY);
                press.render(sb);
                break;
            case 5:
                switch (finalRating)
                {
                    case PERFECT:
                        drawTexture(sb, perfect, 0, 0, SIZE);
                        break;
                    case NOT_BAD:
                        drawTexture(sb, notbad, 0, 0, SIZE);
                        break;
                    case OUCH:
                        drawTexture(sb, ouch, 0, 0, SIZE);
                        break;
                }
            case 4:
                drawTexture(sb, grade, 0, 0, SIZE);
                //first display "Your Grade:"
                //after a pause, display the rank + appropriate sfx
                //Pressing any button after this ends the game
        }
    }

    private void drawThingy(SpriteBatch sb, int x, int y) {
        if (y < 320)
        {
            drawTexture(sb, thingies, x, y, THINGY_WIDTH, Math.min(THINGY_HEIGHT, 320 - y), 0, THINGY_WIDTH, THINGY_HEIGHT, false, false);
            if (y < 120)
            {
                drawTexture(sb, stringies, x, y + THINGY_HEIGHT, THINGY_WIDTH, 120 - y, 0, THINGY_WIDTH, 1, false, false);
            }
        }
    }

    public void boop() {
        switch (phase)
        {
            case -1:
            case 0:
                CardCrawlGame.sound.play(sfxE);
                //queuedSounds.add(new QueuedSound(sfxHighE, 0.2f));
                press.show(Settings.FAST_MODE ? 0.8f : 1.6f);
                phase = 1;
                time = 0;
                break;
            case 1:
                press.press();
                break;
            case 2:
                if (press.press()) {
                    //The Press has been Pressed

                    //Test if this is a valid hit
                    //Possiblities are: Spot On, A Bit Off, and Whiff
                    for (Ball b : hitBalls)
                    {
                        b.setResult(currentResult);
                    }
                    hitBalls.clear();
                }
                break;
            case 5:
                isDone = true;
                break;
        }
    }

    private void generateBalls(int length, float mediumRate, float heckRate) {
        float time = 0;

        mediumRate += heckRate;

        float difficulty;
        ArrayList<BeatPattern> possible;

        for (int i = 1; i <= length; ++i)
        {
            difficulty = MathUtils.random();
            if (difficulty < heckRate)
            {
                possible = heckPatterns;
            }
            else if (difficulty < mediumRate)
            {
                possible = mediumPatterns;
            }
            else
            {
                possible = basicPatterns;
            }

            getRandomPattern(possible, i == length).addBalls(this, time, balls, allBalls);

            time += 6.4f;
        }
    }

    private BeatPattern getRandomPattern(ArrayList<BeatPattern> source, boolean isFinish) {
        ArrayList<BeatPattern> validPatterns = new ArrayList<>(source);

        if (isFinish)
            validPatterns.removeIf((bp)->bp.type == BeatPattern.PatternType.NOFINISH);
        else
            validPatterns.removeIf((bp)->bp.type == BeatPattern.PatternType.FINISH);

        return validPatterns.get(MathUtils.random(validPatterns.size() - 1));
    }

    private void calculateRating() {
        float score = 0;
        boolean perfect = true;

        for (Ball b : allBalls)
        {
            switch (b.score)
            {
                case NICE:
                    score += 1;
                    break;
                case NOT_QUITE:
                    score += 0.5f;
                case MISS:
                    perfect = false;
            }
        }

        score /= allBalls.size();

        if (perfect)
        {
            finalRating = Rating.PERFECT;
        }
        else if (score >= 0.7)
        {
            finalRating = Rating.NOT_BAD;
        }
        else
        {
            finalRating = Rating.OUCH;
        }
    }

    private void playScoreJingle()
    {
        switch (finalRating)
        {
            case PERFECT:
                CardCrawlGame.sound.play(sfxC);
                queuedSounds.add(new QueuedSound(sfxHighC, 0.2f));
                queuedSounds.add(new QueuedSound(sfxHighE, 0.4f));
                queuedSounds.add(new QueuedSound(sfxHighG, 0.8f));
                queuedSounds.add(new QueuedSound(sfxHigherHighC, 1.2f));
                break;
            case NOT_BAD:
                CardCrawlGame.sound.play(sfxC);
                queuedSounds.add(new QueuedSound(sfxD, 0.2f));
                queuedSounds.add(new QueuedSound(sfxE, 0.4f));
                queuedSounds.add(new QueuedSound(sfxD, 0.6f));
                queuedSounds.add(new QueuedSound(sfxC, 0.8f));
                break;
            case OUCH:
                CardCrawlGame.sound.play(sfxOof);
                CardCrawlGame.sound.play(sfxWrong);
                CardCrawlGame.sound.play(sfxE);
                break;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        press.dispose();
        Ball.dispose();

        CardCrawlGame.music.unsilenceBGM();
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.bindAll("BOOP", this::boop, null, null);

        bindings.addMouseBind((x, y, pointer)->this.isWithinArea(x, y), (p)->boop());
        return bindings;
    }

    public AbstractMinigame makeCopy(){ return new BeatPress(); }

}
