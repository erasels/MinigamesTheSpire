package Minigames.games.fishing;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.fishing.fish.AbstractFish;
import Minigames.games.fishing.phases.AbstractGamePhase;
import Minigames.games.fishing.phases.CatchPhase;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import Minigames.util.HelperClass;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static Minigames.Minigames.makeID;

public class FishingGame extends AbstractMinigame {
    private static float WAITTIME = 1f;

    public AbstractGamePhase gamePhase;
    public AbstractFish fish;
    public boolean fishCaught;

    public float waitTimer = WAITTIME;

    public static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Minigames.makeID("FishingText"));
    public static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(Minigames.makeID("Fishing"));

    //SOUNDS
    public static final String sBob = makeID("sound_bob");
    public static final String sWaterPlop = makeID("sound_water_plop");
    public static final String sHit = makeID("sound_enemy_hit");
    public static final String sWaterSploosh = makeID("sound_water_sploosh");
    public static final String sReward = makeID("sound_fishing_win");
    public static final String sLongReel = makeID("song_reel_long");
    public static final String sShortReel = makeID("song_reel_short");

    public static final float timePlop = 0.875f, timeHit = 0.45f, timeSploosh = 0.87f, timeLReel = 1.5f, timeSReel = 1f, timeBob = 0.5f;

    public FishingGame() {
        super();
        hasInstructionScreen = true;
        hasPostgameScreen = false;
    }

    @Override
    public void initialize() {
        super.initialize();

        background = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/bg.png"));

        fish = AbstractFish.returnRandomFish();

        gamePhase = new CatchPhase(this, null);
        gamePhase.initialize();

        fishCaught = false;
    }

    @Override
    public String getOption() { return eventStrings.NAME; }

    @Override
    public void setupInstructionScreen(GenericEventDialog event) {
        event.updateBodyText(eventStrings.DESCRIPTIONS[0]);
        event.setDialogOption(eventStrings.OPTIONS[0]);
    }

    @Override
    public AbstractMinigame makeCopy() {
        return new FishingGame();
    }

    private void doAction(Vector2 vec) {
        if(gamePhase != null)
            gamePhase.action();
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase) {
            case 0:
                if(gamePhase == null) {
                    phase++;
                } else {
                    gamePhase.update();
                }
                break;
            case 1:
                //Do some transition effect, victory screen, idk
                if(fishCaught) {
                    CardCrawlGame.sound.play(sReward, 1f);

                    AbstractDungeon.getCurrRoom().rewards = fish.returnReward();
                    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                    AbstractDungeon.combatRewardScreen.open(uiStrings.TEXT_DICT.get("WIN"));
                } else {
                    CardCrawlGame.sound.play("ENEMY_TURN", 1f);
                }
                phase = 2;
                isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(gamePhase != null)
            gamePhase.render(sb);
    }

    //To be called by AbstractGamePhase once task is finished
    public boolean waiting() {
        waitTimer -= HelperClass.getTime();
        if (waitTimer <= 0) {
            waitTimer = WAITTIME;
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();

        fish = null;
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> this.isWithinArea(x, y), this::doAction, new MouseHoldObject((x, y) -> doAction(new Vector2(x, y)), null));
        //Add more bindings which basically do the same thing, maybe space?

        //DEBUG Bindings
        //bindings.bindDirectional(() -> this.gamePhase.);

        return bindings;
    }

    private float standardFontWidth = -1;
    public void displayTimer(SpriteBatch sb, String msg, Color color) {
        BitmapFont font = FontHelper.charTitleFont;
        float fontHeight = FontHelper.getHeight(font);
        if(standardFontWidth == -1) {
            standardFontWidth = FontHelper.getSmartWidth(font, msg, 9999f, 0f);;
        }
        FontHelper.renderFontLeftTopAligned(sb, font, msg, (Settings.WIDTH / 2.0F) - (standardFontWidth/2f), fontHeight + (20f * Settings.scale), color);
    }
}
