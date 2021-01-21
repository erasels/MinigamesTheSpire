package Minigames.games.slimePopper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

import java.util.HashMap;

public class PopperSlime extends PopperItem {

    public PopperSlime() {
        super(PopperItem.TYPE.SLIME, "slimeIdle");
        priority = 6;
    }

    @Override
    public void startDeath() {
        isDying = true;
        setAnimation("slimeDie");
        SlimePopper.playSlimeSoundRegulated();
    }
}
