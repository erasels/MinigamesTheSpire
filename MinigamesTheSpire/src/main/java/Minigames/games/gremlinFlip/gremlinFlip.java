package Minigames.games.gremlinFlip;

/*
    Game name: Gremlin Flip

    Inspired by Voltorb Flip, a minigame in Pokemon HeartGold and SoulSilver, Gremlin Flip tasks the player with flipping cards to gain gold and avoiding Gremlin Nobs.
    It features a 5x5 grid, containing tiles which give gold, and Gremlin Nobs.

    The first tile flipped gives the player that much gold on the first card, and each subsequent card that is greater than 1 multiplies the total by the number on the card.
    For subsequent tiles that are flipped that have the number 1, 1 is added to the player's gold.
    Flipping a Gremlin Nob causes the play to spill all gold and lose the minigame.

    The player presses left-click to flip tiles, and can press right-click to mark a tile.

 */

import Minigames.games.AbstractMinigame;
import Minigames.games.gremlinFlip.boards.*;
import Minigames.games.gremlinFlip.tiles.AbstractTile;
import Minigames.games.gremlinFlip.tiles.GameTile;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.*;

import java.util.ArrayList;

import static Minigames.Minigames.makeID;

public class gremlinFlip extends AbstractMinigame {

    public static final String ID = makeID(gremlinFlip.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public static final String ASSET_PATH = "minigamesResources/img/games/gremlinflip/gremlinFlip.atlas";
    public static final AssetManager assetManager = new AssetManager();
    public static TextureAtlas atlas;

    protected static AbstractBoard board;

    public static boolean locked;
    public static boolean failedMinigame;
    public static int goldScore;
    private float countdown = 0.1f;
    private float baseCD = 0.1f;

    private int currentIndexPointer = 0;
    private boolean finished = false;

    private int GOLD = 0;
    private int screenNum = 0;

    private static final int COMMON_RELIC_PRICE = 150;
    private static final int UNCOMMON_RELIC_PRICE = 250;
    private static final int RARE_RELIC_PRICE = 300;
    private static final int CAPSULE_PRICE = 500;

    private String difficulty_level;
    private int GOLD_CLEAR_REWARD = 150;

    public gremlinFlip() {
        super();
        hasInstructionScreen = true;
        hasPostgameScreen = true;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!assetManager.isLoaded(ASSET_PATH)) {
            assetManager.load(ASSET_PATH, TextureAtlas.class);
            assetManager.finishLoadingAsset(ASSET_PATH);
        }
        atlas = assetManager.get(ASSET_PATH, TextureAtlas.class);

        locked = false;
        failedMinigame = false;
        goldScore = 0;
        board = new proceduralBoard();
        board.init();
        difficulty_level = ((proceduralBoard) board).returnDifficultyLevel();

        phase = 0;
        setScale(getMaxScale());
    }

    @Override
    public String getOption() { return NAME; }

    @Override
    public void setupInstructionScreen(GenericEventDialog event) {
        event.updateBodyText(DESCRIPTIONS[0]);
        event.setDialogOption(OPTIONS[0]);
    }

    @Override
    public void setupPostgameScreen(GenericEventDialog event) {
        GOLD = GOLD_CLEAR_REWARD;
        if(goldScore >= 1000){ GOLD += 25; }
        if(goldScore >= 10000){ GOLD += 50; }
        if(goldScore >= 20000){ GOLD += 75; }
        if(goldScore >= 40000){ GOLD += 100; }
        if(goldScore >= 60000){ GOLD += 100; }
        if(failedMinigame){
            event.updateBodyText(DESCRIPTIONS[2]);
            event.setDialogOption(OPTIONS[2]);
            screenNum = 0;
        }
        else {
            event.updateBodyText(DESCRIPTIONS[1]);
            event.setDialogOption(OPTIONS[1]);
            screenNum = 1;
        }
    }
    @Override
    public boolean postgameButtonPressed(int buttonIndex, GenericEventDialog event) {

        switch (screenNum){
            case 1:
                event.updateBodyText(String.format(DESCRIPTIONS[3], GOLD, goldScore, difficulty_level));
                event.clearAllDialogs();
                event.setDialogOption(GOLD < COMMON_RELIC_PRICE ? String.format(OPTIONS[12], COMMON_RELIC_PRICE) : String.format(OPTIONS[3], COMMON_RELIC_PRICE), GOLD < COMMON_RELIC_PRICE);
                event.setDialogOption(GOLD < UNCOMMON_RELIC_PRICE ? String.format(OPTIONS[12], UNCOMMON_RELIC_PRICE) : String.format(OPTIONS[4], UNCOMMON_RELIC_PRICE), GOLD < UNCOMMON_RELIC_PRICE);
                event.setDialogOption(GOLD < RARE_RELIC_PRICE ? String.format(OPTIONS[12], RARE_RELIC_PRICE) : String.format(OPTIONS[5], RARE_RELIC_PRICE), GOLD < RARE_RELIC_PRICE);
                event.setDialogOption(GOLD < CAPSULE_PRICE ? String.format(OPTIONS[12], CAPSULE_PRICE) : String.format(OPTIONS[6], CAPSULE_PRICE), GOLD < CAPSULE_PRICE);
                event.setDialogOption(String.format(OPTIONS[7], GOLD));
                screenNum = 2;
                return false;
            case 2:
                switch (buttonIndex){
                    case 0:
                        event.clearAllDialogs();
                        GOLD -= COMMON_RELIC_PRICE;
                        relicReward(AbstractRelic.RelicTier.COMMON);
                        AbstractDungeon.player.gainGold(GOLD);
                        event.updateBodyText(String.format(DESCRIPTIONS[4], getCharacterStorageRef()));
                        screenNum = 0;
                        event.setDialogOption(OPTIONS[2]);
                        return false;
                    case 1:
                        event.clearAllDialogs();
                        GOLD -= UNCOMMON_RELIC_PRICE;
                        relicReward(AbstractRelic.RelicTier.UNCOMMON);
                        AbstractDungeon.player.gainGold(GOLD);
                        event.updateBodyText(String.format(DESCRIPTIONS[4], getCharacterStorageRef()));
                        screenNum = 0;
                        event.setDialogOption(OPTIONS[2]);
                        return false;
                    case 2:
                        event.clearAllDialogs();
                        GOLD -= RARE_RELIC_PRICE;
                        relicReward(AbstractRelic.RelicTier.RARE);
                        AbstractDungeon.player.gainGold(GOLD);
                        event.updateBodyText(String.format(DESCRIPTIONS[4], getCharacterStorageRef()));
                        screenNum = 0;
                        event.setDialogOption(OPTIONS[2]);
                        return false;
                    case 3:
                        event.clearAllDialogs();
                        GOLD -= CAPSULE_PRICE;
                        capsuleReward();
                        AbstractDungeon.player.gainGold(GOLD);
                        event.updateBodyText(String.format(DESCRIPTIONS[6], getCharacterStorageRef()));
                        screenNum = 0;
                        event.setDialogOption(OPTIONS[2]);
                        return false;
                    case 4:
                        event.clearAllDialogs();
                        AbstractDungeon.player.gainGold(GOLD);
                        event.updateBodyText(String.format(DESCRIPTIONS[5], getCharacterStorageRef()));
                        screenNum = 0;
                        event.setDialogOption(OPTIONS[2]);
                        return false;
                }
        }
        return super.postgameButtonPressed(buttonIndex);
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase)
        {
            case 0:
                board.update();
                if(locked){ phase = 1; }
                else if(flippedAllCoinTiles()){
                    locked = true;
                    countdown = 1F;
                    phase = 1;
                }
                break;
            case 1:
                board.update();
                countdown -= Gdx.graphics.getDeltaTime();
                if(countdown <= 0f){
                    if(finished) { phase = 2; }
                    AbstractTile currentTile = board.getTile(currentIndexPointer);
                    if(currentTile instanceof GameTile && !((GameTile) currentTile).isFlipped()){ ((GameTile) currentTile).unclickedShowTile(); }
                    if(allTilesFlipped()){
                        countdown = 1F;
                        finished = true;
                    }
                    else { countdown = baseCD; }
                    currentIndexPointer += 1;
                }
                break;
            case 2:
                isDone = true;
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (phase != 2)
        {
            board.render(sb);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean onKeyDown(int keycode) {
        return false;
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        return bindings;
    }

    public boolean allTilesFlipped(){
        for(AbstractTile t: board.getTiles()){
            if(t instanceof GameTile && !((GameTile) t).isFlipped()){ return false; }
        }
        return true;
    }
    public boolean flippedAllCoinTiles(){
        for(AbstractTile t: board.getTiles()){
            if(t instanceof GameTile && (!((GameTile) t).isFlipped() && !((GameTile) t).isEnemy())){ return false; }
        }
        return true;
    }

    private void relicReward(AbstractRelic.RelicTier tier) {
        AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(tier);
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.28F, Settings.HEIGHT / 2.0F, r);
    }
    private void capsuleReward() {
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH * 0.28F, Settings.HEIGHT / 2.0F, getBossUpgrade());
        AbstractDungeon.player.gainEnergy(1);
    }
    private AbstractRelic getBossUpgrade() {
        // Patch into this to add your character-boss-relic
        switch (AbstractDungeon.player.chosenClass){
            case IRONCLAD: return new BurningBlood();
            case THE_SILENT: return new RingOfTheSerpent();
            case DEFECT: return new FrozenCore();
            case WATCHER: return new HolyWater();
            default: return new Circlet();
        }
    }
    private String getCharacterStorageRef() {
        // Patch into this to add your own dialogue
        switch (AbstractDungeon.player.chosenClass){
            case IRONCLAD: return OPTIONS[8];
            case THE_SILENT: return OPTIONS[9];
            case DEFECT: return OPTIONS[10];
            case WATCHER: return OPTIONS[11];
            default: return OPTIONS[8];
        }
    }
}
