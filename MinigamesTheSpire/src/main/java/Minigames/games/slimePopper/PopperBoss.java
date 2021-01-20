package Minigames.games.slimePopper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

import java.util.Set;

public class PopperBoss extends PopperItem {
    public static final float SIZE = 128f * Settings.scale;

    public int hp = 100;

    private final Texture whitePixel;
    private final TextureRegion hpRegion;

    public PopperBoss(String animationName) {
        super(TYPE.BOSS, animationName);
        hb = new Hitbox(SIZE, SIZE);
        setAnimation(animationName);
        DEATH_TIME = 0.1f * 7;

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pm.setColor(0xffffffff);
        pm.drawPixel(0, 0);
        whitePixel = new Texture(pm);
        whitePixel.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        hpRegion = new TextureRegion(whitePixel);
    }

    public void dealDamage(PopperItem louse) {
        CardCrawlGame.sound.play("MONSTER_SLIME_ATTACK");
        float xDist = Math.abs(hb.cX - louse.hb.cX);
        float a = 1f - MathUtils.clamp((xDist * 2f) / hb.width, 0f, 1f);
        int damage = MathUtils.round(Interpolation.pow2Out.apply(5, 25, a));
        hp -= damage;
        if (hp <= 0) {
            startDeath();
        }
    }

    @Override
    public void startDeath() {
        isDying = true;
        setAnimation("bossSplit");
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!isDying && !isDead) {
            sb.setColor(Color.DARK_GRAY);
            sb.draw(hpRegion, hb.x, hb.y + hb.height, hb.width, 8f * Settings.scale);
            sb.setColor(Color.FIREBRICK);
            sb.draw(hpRegion, hb.x, hb.y + hb.height, Interpolation.linear.apply(0, hb.width, hp / 100f), 8f * Settings.scale);
        }
    }
}
