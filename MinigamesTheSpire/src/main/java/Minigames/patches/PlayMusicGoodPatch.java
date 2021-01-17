package Minigames.patches;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;

import static Minigames.Minigames.makeAudioPath;

@SpirePatch(clz = TempMusic.class, method = "getSong")
public class PlayMusicGoodPatch {
    @SpirePostfixPatch
    public static Music Postfix(Music __result, TempMusic __instance, String key) {
        if ("minigames:carnivalMusic".equals(key)) {
            return MainMusic.newMusic(makeAudioPath("carnivalMusic.mp3"));
        }
        return __result;
    }
}