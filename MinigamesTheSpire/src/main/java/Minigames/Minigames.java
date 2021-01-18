package Minigames;

import Minigames.events.ActOneArcade;
import Minigames.events.ActThreeArcade;
import Minigames.events.ActTwoArcade;
import Minigames.events.TestMinigameEvent;
import Minigames.games.AbstractMinigame;
import Minigames.games.beatpress.BeatPress;
import Minigames.games.blackjack.BlackjackMinigame;
import Minigames.games.fishing.FishingGame;
import Minigames.games.gremlinFlip.gremlinFlip;
import Minigames.games.mastermind.MastermindMinigame;
import Minigames.games.rubik.RubikMinigame;
import Minigames.games.shellgame.ShellGame;
import Minigames.games.slidepuzzle.SlidePuzzleMinigame;
import Minigames.games.slimePopper.SlimePopper;
import Minigames.util.TextureLoader;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.eventUtil.AddEventParams;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.SeenEvents;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

@SpireInitializer
public class Minigames implements
        PostInitializeSubscriber,
        EditStringsSubscriber,
        AddAudioSubscriber,
        PostUpdateSubscriber {
    private static SpireConfig modConfig = null;

    public static final Logger logger = LogManager.getLogger(Minigames.class.getName());
    public static final ArrayList<AbstractMinigame> srcMinigameList = new ArrayList<>();

    public static void initialize() {
        BaseMod.subscribe(new Minigames());
    }

    private ModPanel settingsPanel;
    private final float xPos = 350f, yPos = 750f;

    private static boolean oncePerRun = true; // set up some config for this or something

    @Override
    public void receivePostInitialize() {
        //UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(makeID("OptionsMenu"));
        //String[] TEXT = UIStrings.TEXT;
        settingsPanel = new ModPanel();
        addMinigames();

        BaseMod.registerModBadge(TextureLoader.getTexture(makeImgPath("modBadge.png")), "Minigames The Spire", "erasels", "A mod, boyo.", settingsPanel);

        BaseMod.addEvent(TestMinigameEvent.ID, TestMinigameEvent.class, ""); //Only appears in dungeons with the ID "", which should be none.

        BaseMod.addEvent(new AddEventParams.Builder(ActOneArcade.ID, ActOneArcade.class).dungeonID(Exordium.ID).spawnCondition(this::canEventSpawn).create());
        BaseMod.addEvent(new AddEventParams.Builder(ActTwoArcade.ID, ActTwoArcade.class).dungeonID(TheCity.ID).spawnCondition(this::canEventSpawn).create());
        BaseMod.addEvent(new AddEventParams.Builder(ActThreeArcade.ID, ActThreeArcade.class).dungeonID(TheBeyond.ID).spawnCondition(this::canEventSpawn).create());
    }

    private boolean canEventSpawn() {
        //This won't work correctly if the ID has a space in it, since those get converted to underscores, or if another event is registered with the same ID. But I mean, who would do that. Right?
        return !oncePerRun || (!SeenEvents.seenEvents.get(AbstractDungeon.player).contains(ActOneArcade.ID) &&
                !SeenEvents.seenEvents.get(AbstractDungeon.player).contains(ActTwoArcade.ID) &&
                !SeenEvents.seenEvents.get(AbstractDungeon.player).contains(ActThreeArcade.ID));
    }

    @Override
    public void receivePostUpdate() {

    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, getModID() + "Resources/loc/"+locPath()+"/uiStrings.json");
        BaseMod.loadCustomStringsFile(EventStrings.class, getModID() + "Resources/loc/"+locPath()+"/eventStrings.json");
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio(BeatPress.sfxC, makeAudioPath("C.ogg"));
        BaseMod.addAudio(BeatPress.sfxD, makeAudioPath("D.ogg"));
        BaseMod.addAudio(BeatPress.sfxE, makeAudioPath("E.ogg"));
        BaseMod.addAudio(BeatPress.sfxWrong, makeAudioPath("Wrong.ogg"));
        BaseMod.addAudio(BeatPress.sfxHighC, makeAudioPath("HighC.ogg"));
        BaseMod.addAudio(BeatPress.sfxHighD, makeAudioPath("HighD.ogg"));
        BaseMod.addAudio(BeatPress.sfxHighE, makeAudioPath("HighE.ogg"));
        BaseMod.addAudio(BeatPress.sfxHighF, makeAudioPath("HighF.ogg"));
        BaseMod.addAudio(BeatPress.sfxHighG, makeAudioPath("HighG.ogg"));
        BaseMod.addAudio(BeatPress.sfxHighWrong, makeAudioPath("HighWrong.ogg"));
        BaseMod.addAudio(BeatPress.sfxHigherHighC, makeAudioPath("VeryHighC.ogg"));
        BaseMod.addAudio(BeatPress.sfxOof, makeAudioPath("Oof.ogg"));
        BaseMod.addAudio(BeatPress.sfxPress, makeAudioPath("Press.ogg"));
        BaseMod.addAudio(BeatPress.sfxPressReady, makeAudioPath("DeepC.ogg"));
        BaseMod.addAudio(makeID("cardPlace1"), makeGamePath("Blackjack/SFX/cardPlace1.ogg"));
        BaseMod.addAudio(makeID("cardPlace2"), makeGamePath("Blackjack/SFX/cardPlace2.ogg"));
        BaseMod.addAudio(makeID("cardPlace3"), makeGamePath("Blackjack/SFX/cardPlace3.ogg"));

        BaseMod.addAudio(FishingGame.sBob, makeAudioPath("Fishing/bob.wav"));
        BaseMod.addAudio(FishingGame.sWaterPlop, makeAudioPath("Fishing/dropItemInWater.wav"));
        BaseMod.addAudio(FishingGame.sHit, makeAudioPath("Fishing/hitEnemy.wav"));
        BaseMod.addAudio(FishingGame.sWaterSploosh, makeAudioPath("Fishing/pullItemFromWater.wav"));
        BaseMod.addAudio(FishingGame.sReward, makeAudioPath("Fishing/reward.wav"));
        BaseMod.addAudio(FishingGame.sLongReel, makeAudioPath("Fishing/fastReel.wav"));
        BaseMod.addAudio(FishingGame.sShortReel, makeAudioPath("Fishing/slowReel.wav"));
    }

    private static String locPath() {
        return "eng";
    }

    public static String makeImgPath(String resourcePath) {
        return getModID() + "Resources/img/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return getModID() + "Resources/img/ui/" + resourcePath;
    }

    public static String makeGamePath(String resourcePath) {
        return getModID() + "Resources/img/games/" + resourcePath;
    }

    public static String makeAudioPath(String resourcePath) {
        return getModID() + "Resources/audio/" + resourcePath;
    }

    public static String getModID() {
        return "minigames";
    }

    public static String makeID(String input) {
        return getModID() + ":" + input;
    }

    private void saveConfig() {
        try {
            modConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMinigames(){
        // Add your Minigame here!
        srcMinigameList.add(new BeatPress());
        srcMinigameList.add(new BlackjackMinigame());
        srcMinigameList.add(new gremlinFlip());
        srcMinigameList.add(new MastermindMinigame());
        srcMinigameList.add(new SlimePopper());
        srcMinigameList.add(new FishingGame());
        srcMinigameList.add(new ShellGame());
        srcMinigameList.add(new SlidePuzzleMinigame());
        srcMinigameList.add(new RubikMinigame());
    }
}