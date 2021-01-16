package Minigames.games.shellgame;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Minigames.Minigames.makeGamePath;

public class Shell {

    static Texture shellTex = TextureLoader.getTexture(makeGamePath("shells/lagavulinshell.png"));

    public float x;
    public float targetX;
    public float y;
    public float targetY;
    private AbstractCard heldCard;
    private AbstractRelic heldRelic;
    public Hitbox hb;

    public boolean isMoving;
    public boolean yApexReached;

    public float scale = 1F;
    public float targetScale = 1F;
    public float startScale = 1F;

    public float startX;
    public float startY;

    public float moveTimer;
    public float startMoveTimer;

    public float moveTimerY;
    public float startMoveTimerY;

    public Shell(float x, float y, AbstractCard held) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, shellTex.getWidth(), shellTex.getHeight());
        this.heldCard = held;
    }

    public Shell(float x, float y, AbstractRelic held) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, shellTex.getWidth(), shellTex.getHeight());
        this.heldRelic = held;
    }

    public void grantReward() {
        if (this.heldCard != null) {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.heldCard.makeCopy(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        } else if (this.heldRelic != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, heldRelic.makeCopy());
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        sb.draw(shellTex, x, y, shellTex.getWidth() * scale, shellTex.getHeight() * scale);


        if (heldCard != null) {
            heldCard.render(sb);
        }
        if (heldRelic != null) {
            heldRelic.render(sb);
        }
    }
    public void update() {
        if (isMoving) {
            moveTimer += Gdx.graphics.getDeltaTime();
            moveTimerY += Gdx.graphics.getDeltaTime();

            scale = MathUtils.lerp(startScale, targetScale, moveTimerY / startMoveTimerY);
            y = MathUtils.lerp(startY, targetY, moveTimerY / startMoveTimerY);

            if (!yApexReached) {
                if (moveTimerY >= startMoveTimerY) {
                    y = targetY;
                    startY = targetY;
                    targetY = ShellGame.yMid;
                    yApexReached = true;
                    scale = targetScale;
                    targetScale = 1F;
                    startScale = scale;
                    moveTimerY -= startMoveTimerY;
                }
            }

            x = MathUtils.lerp(startX, targetX, moveTimer / startMoveTimer);

            if (moveTimer >= startMoveTimer) {
                x = targetX;
                y = ShellGame.yMid;
                scale = 1F;
                isMoving = false;
                ShellGame.receiveSwapComplete();
            }
        }
    }

}
