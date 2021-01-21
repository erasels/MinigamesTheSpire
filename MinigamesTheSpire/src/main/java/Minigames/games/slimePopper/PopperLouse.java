package Minigames.games.slimePopper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

import java.util.HashMap;

public class PopperLouse extends PopperItem {
    public boolean isFiring = false;
    public boolean isResetting = false;
    private float resetTimer = 0f;

    public PopperLouse(String animationName) {
        super(PopperItem.TYPE.LOUSE, animationName);
        DEATH_TIME = 0f; // shouldn't ever die anyway, but has no animation
        priority = 7;
    }

    public void startReset() {
        isFiring = false;
        isPreview = true;
        isResetting = true;
        resetTimer = 0.15f;
        float angle = MathUtils.random(360);
        float mag = MathUtils.random(300f, 400f);
        xVelocity = mag * MathUtils.cosDeg(angle);
        yVelocity = mag * MathUtils.sinDeg(angle);
    }

    public void reset() {
        isFiring = false;
        isPreview = false;
        isResetting = false;
        resetTimer = 0.25f;
        setAnimation("louseIdle");
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        if (isResetting) {
            resetTimer -= elapsed;
            if (resetTimer <= 0f) {
                reset();
            }
        }
    }

    @Override
    public void startDeath() { }
}
