package Minigames;

import Minigames.events.TestMinigameEvent;
import Minigames.games.beatpress.BeatPress;
import Minigames.util.TextureLoader;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.AddAudioSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@SpireInitializer
public class Minigames implements
        PostInitializeSubscriber,
        EditStringsSubscriber,
        AddAudioSubscriber,
        PostUpdateSubscriber {
    private static SpireConfig modConfig = null;

    public static final Logger logger = LogManager.getLogger(Minigames.class.getName());

    public static void initialize() {
        BaseMod.subscribe(new Minigames());
    }

    private ModPanel settingsPanel;
    private final float xPos = 350f, yPos = 750f;

    @Override
    public void receivePostInitialize() {
        //UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(makeID("OptionsMenu"));
        //String[] TEXT = UIStrings.TEXT;
        settingsPanel = new ModPanel();

        BaseMod.registerModBadge(TextureLoader.getTexture(makeImgPath("modBadge.png")), "Minigames The Spire", "erasels", "A mod, boyo.", settingsPanel);

        BaseMod.addEvent(TestMinigameEvent.ID, TestMinigameEvent.class);
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
        BaseMod.addAudio(BeatPress.sfxC, makeAudioPath("C.wav"));
        BaseMod.addAudio(BeatPress.sfxD, makeAudioPath("D.wav"));
        BaseMod.addAudio(BeatPress.sfxE, makeAudioPath("E.wav"));
        BaseMod.addAudio(BeatPress.sfxWrong, makeAudioPath("Wrong.wav"));
        BaseMod.addAudio(BeatPress.sfxHighE, makeAudioPath("HighE.wav"));
        BaseMod.addAudio(BeatPress.sfxHighF, makeAudioPath("HighF.wav"));
        BaseMod.addAudio(BeatPress.sfxHighG, makeAudioPath("HighG.wav"));
        BaseMod.addAudio(BeatPress.sfxHighWrong, makeAudioPath("HighWrong.wav"));
        BaseMod.addAudio(BeatPress.sfxHammer, makeAudioPath("Noise.wav"));
        BaseMod.addAudio(BeatPress.sfxOof, makeAudioPath("Oof.wav"));
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
}