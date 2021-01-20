package Minigames.games.slimePopper;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

import java.util.HashMap;

public class PopperItem {
    public TYPE type;
    public Hitbox hb;
    public static final float SIZE = 32f * Settings.scale;

    public boolean isDying = false;
    public boolean isDead = false;

    public float xVelocity = 0f;
    public float yVelocity = 0f;
    public boolean friction = false;

    private float animTime = 0f;

    private static final HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();
    private Animation<TextureRegion> animation;
    TextureRegion frame;

    public PopperItem(TYPE type, String animationName) {
        this.type = type;
        hb = new Hitbox(SIZE, SIZE);
        setAnimation(animationName);
    }

    public void setAnimation(String animationName) {
        animation = animations.computeIfAbsent(
                animationName,
                key -> new Animation<>(0.1f, SlimePopper.atlas.findRegions(key), Animation.PlayMode.LOOP)
        );
        animTime = 0;
    }

    public void startDeath() {
        isDying = true;
        setAnimation("slimeDie");
        CardCrawlGame.sound.play("MONSTER_SLIME_ATTACK");
    }

    protected float DEATH_TIME = 0.1f * 7;
    public void update(float elapsed) {
        animTime += elapsed;
        frame = animation.getKeyFrame(animTime, true);
        hb.update();
        if (friction) {
            xVelocity = MathUtils.lerp(xVelocity, 0f, animTime / 500f);
            yVelocity = MathUtils.lerp(yVelocity, 0f, animTime / 500f);
        }
        if (isDying && animTime > DEATH_TIME) {
            isDead = true;
        }
    }

    public void render(SpriteBatch sb) {
        float w = hb.width;
        float h = hb.height;
        float w2 = w / 2f;
        float h2 = h / 2f;
        sb.draw(frame, hb.cX - w2, hb.cY - h2, -(hb.cX - w2), -(hb.cY - h2), w, h, Settings.scale, Settings.scale, 0f);
    }

    public enum TYPE {
        LOUSE,
        SLIME,
        BOSS,
        MED
    }
}
