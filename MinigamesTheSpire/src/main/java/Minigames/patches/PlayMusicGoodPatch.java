package Minigames.patches;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

import static Minigames.Minigames.makeAudioPath;

@SpirePatch(
        clz = TempMusic.class,
        method = "getSong")
public class PlayMusicGoodPatch {
    @SpirePostfixPatch
    public static SpireReturn<Music> Prefix(TempMusic __instance, String key) {
        System.out.println("YOU ACTUALLY HIT THE PATCH GOOD JOB, PAST VEX! key: " + key);
        switch (key) {
            case "minigames:carnivalMusic": {
                System.out.println("YES, IT LIKES THE KEY!");
                return SpireReturn.Return(MainMusic.newMusic(makeAudioPath("carnivalMusic.mp3")));
            }
            default: {
                System.out.println("NO, IT HATES THE KEY!");
                return SpireReturn.Continue();
            }
        }
    }
}

