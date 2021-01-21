package Minigames.games.slimePopper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class PopperMed extends PopperItem {
    public static final float SIZE = 64f * Settings.scale;

    public int maxHp = AbstractDungeon.ascensionLevel >= 15 ? 350 : 250;
    public int hp = maxHp;

    private final TextureRegion hpRegion;

    public PopperMed() {
        super(TYPE.MED, "medSlimeIdle");
        hb = new Hitbox(SIZE, SIZE);
        DEATH_TIME = 0.1f * 4;

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pm.setColor(0xffffffff);
        pm.drawPixel(0, 0);
        Texture whitePixel = new Texture(pm);
        whitePixel.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        hpRegion = new TextureRegion(whitePixel);
    }

    public void dealDamage(PopperItem louse) {
        SlimePopper.playSlimeSoundRegulated();
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
        setAnimation("medSplit");
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (!isDying && !isDead) {
            sb.setColor(Color.DARK_GRAY);
            sb.draw(hpRegion, hb.x, hb.y + hb.height, hb.width, 8f * Settings.scale);
            sb.setColor(Color.FIREBRICK);
            sb.draw(hpRegion, hb.x, hb.y + hb.height, Interpolation.linear.apply(0, hb.width, hp / (float)maxHp), 8f * Settings.scale);
        }
    }
}
