package Minigames.games.slimePopper;

import com.badlogic.gdx.math.MathUtils;

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
        previewTime = 0.1f;
        isResetting = true;
        resetTimer = 0.1f;
        float angle = MathUtils.random(125f, 315f);
        float mag = MathUtils.random(300f, 400f);
        xVelocity = mag * MathUtils.cosDeg(angle);
        yVelocity = mag * MathUtils.sinDeg(angle);
    }

    public void reset() {
        isFiring = false;
        previewTime = 0f;
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
