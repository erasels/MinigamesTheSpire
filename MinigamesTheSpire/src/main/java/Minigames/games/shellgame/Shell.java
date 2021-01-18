package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Minigames.Minigames.makeGamePath;

public class Shell {

    static Texture shellTex = TextureLoader.getTexture(makeGamePath("shells/lagavulinshell.png"));
    static Texture hoveredShellTex = TextureLoader.getTexture(makeGamePath("shells/lagavulinshellHighlighted.png"));

    private AbstractMinigame parent;

    public float x;
    public float targetX;
    public float y;
    public float targetY;
    public AbstractCard heldCard;
    public AbstractRelic heldRelic;
    public Hitbox hb;

    public boolean isMoving;
    public boolean yApexReached;

    public float scale = 1F;
    public float targetScale = 1F;
    public float startScale = 1F;

    public float alpha = 0F;
    public float targetAlpha = 1F;
    public float startAlpha = 0F;

    public float startX;
    public float startY;

    public float shellOffsetX;
    public float shellOffsetY;

    public float moveTimer;
    public float startMoveTimer;

    public float moveTimerY;
    public float startMoveTimerY;

    public animPhase currentPhase = animPhase.NONE;

    public Shell(AbstractMinigame parent, float x, float y, AbstractCard held) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x - (shellTex.getWidth() / 2F), y - (shellTex.getHeight() / 2F), shellTex.getWidth(), shellTex.getHeight());
        this.heldCard = held;
        heldCard.current_x = heldCard.target_x = Settings.WIDTH / 2F;
        heldCard.current_y = heldCard.target_y = Settings.HEIGHT / 2F;
        heldCard.drawScale = heldCard.targetDrawScale = 1.33F;
        heldCard.targetTransparency = heldCard.transparency = 1F;

        currentPhase = animPhase.NONE;

        shellOffsetX = shellTex.getWidth() * -0.5F;
    }

    public Shell(AbstractMinigame parent, float x, float y, AbstractRelic held) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x - (shellTex.getWidth() / 2F), y - (shellTex.getHeight() / 2F), shellTex.getWidth(), shellTex.getHeight());
        this.heldRelic = held;
        heldRelic.currentX = heldRelic.targetX = Settings.WIDTH / 2F;
        heldRelic.currentY = heldRelic.targetY = Settings.HEIGHT / 2F;

        currentPhase = animPhase.NONE;

        shellOffsetX = shellTex.getWidth() * -0.5F;
    }

    public void grantReward() {
        if (this.heldCard != null) {
            if (this.heldCard.type == AbstractCard.CardType.CURSE) ShellGame.gotCurse = true;
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.heldCard.makeCopy(), this.heldCard.current_x, this.heldCard.current_y));
            this.heldCard = null;
        } else if (this.heldRelic != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(heldRelic.currentX + ((64 * Settings.scale) / 2), heldRelic.currentY + ((64 * Settings.scale) / 2), heldRelic.makeCopy());
            this.heldRelic = null;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        if (heldCard != null) {
            if (currentPhase != animPhase.SWITCHEROO && currentPhase != animPhase.NONE && currentPhase != animPhase.WAITINGFORPLAYER) {
                heldCard.render(sb);
            }
        }
        if (heldRelic != null) {
            if (currentPhase != animPhase.SWITCHEROO && currentPhase != animPhase.NONE && currentPhase != animPhase.WAITINGFORPLAYER) {
                sb.setColor(1F, 1F, 1F, 1F);
                sb.draw(heldRelic.img, heldRelic.currentX - (64 * heldRelic.scale / 2), heldRelic.currentY - (64 * heldRelic.scale / 2), 64 * heldRelic.scale, 64 * heldRelic.scale);

            }
        }

        sb.setColor(1F, 1F, 1F, alpha);

        if (this.hb.hovered && parent.phase == 3) {
            sb.draw(hoveredShellTex, x + shellOffsetX, y + shellOffsetY - (hoveredShellTex.getHeight() / 2), hoveredShellTex.getWidth() * scale, hoveredShellTex.getHeight() * scale);
        } else {
            sb.draw(shellTex, x + shellOffsetX, y + shellOffsetY - (shellTex.getHeight() / 2), shellTex.getWidth() * scale, shellTex.getHeight() * scale);
        }

        hb.render(sb);
    }

    public void setEnclosedLocations() {
        if (this.heldCard != null) {
            this.heldCard.current_x = this.heldCard.target_x = x;
            this.heldCard.current_y = this.heldCard.target_y = y;
        } else if (this.heldRelic != null) {
            this.heldRelic.currentX = this.heldRelic.targetX = x;
            this.heldRelic.currentY = this.heldRelic.targetY = y;
        }
    }

    public void update(float elapsed) {
        hb.update();
        /*
        if (heldCard != null) {
            heldCard.update();
        }
        if (heldRelic != null) {
            heldRelic.update();
        }
        */
        switch (currentPhase) {
            case REWARDINTRO: {
                moveTimer += elapsed;
                if (heldCard != null) {
                    heldCard.drawScale = heldCard.targetDrawScale = Interpolation.linear.apply(ShellGame.cardScaleStart, ShellGame.cardScalePeak, moveTimer / startMoveTimer);
                    if (moveTimer >= startMoveTimer) {
                        heldCard.drawScale = ShellGame.cardScalePeak;
                        startMoveTimer = 0.3F;
                        moveTimer = 0F;
                        currentPhase = animPhase.REWARDINTRO2;
                    }
                } else if (heldRelic != null) {
                    heldRelic.scale = Interpolation.linear.apply(ShellGame.relicScaleStart, ShellGame.relicScalePeak, moveTimer / startMoveTimer);
                    if (moveTimer >= startMoveTimer) {
                        heldRelic.scale = ShellGame.relicScalePeak;
                        startMoveTimer = 0.3F;
                        moveTimer = 0F;
                        currentPhase = animPhase.REWARDINTRO2;
                    }
                }
                break;
            }
            case REWARDINTRO2: {
                moveTimer += elapsed;
                if (heldCard != null) {
                    if (moveTimer < startMoveTimer) {
                        heldCard.drawScale = heldCard.targetDrawScale = Interpolation.linear.apply(ShellGame.cardScalePeak, ShellGame.cardScaleNorm, moveTimer / startMoveTimer);
                    } else {
                        heldCard.drawScale = ShellGame.cardScaleNorm;
                        //heldCard.current_x = heldCard.target_x = targetX;
                        if (moveTimer >= startMoveTimer * 2.5) {
                            startMoveTimer = 0.3F;
                            moveTimer = 0F;
                            currentPhase = animPhase.REWARDMOVETOSPACE;
                        }
                    }
                } else if (heldRelic != null) {
                    if (moveTimer < startMoveTimer) {
                        heldRelic.scale = Interpolation.linear.apply(ShellGame.relicScalePeak, ShellGame.relicScaleNorm, moveTimer / startMoveTimer);
                    } else {
                        heldRelic.scale = ShellGame.relicScaleNorm;
                        //heldCard.current_x = heldCard.target_x = targetX;
                        if (moveTimer >= startMoveTimer) {
                            startMoveTimer = 0.3F;
                            moveTimer = 0F;
                            currentPhase = animPhase.REWARDMOVETOSPACE;
                        }
                    }
                }
                break;
            }
            case REWARDMOVETOSPACE: {
                moveTimer += elapsed;
                if (heldCard != null) {
                    heldCard.drawScale = heldCard.targetDrawScale = Interpolation.linear.apply(ShellGame.cardScaleNorm, ShellGame.cardScaleCup, moveTimer / startMoveTimer);
                    x = Interpolation.linear.apply(x, targetX, moveTimer / startMoveTimer);
                    heldCard.current_x = heldCard.target_x = x;
                    if (moveTimer >= startMoveTimer) {
                        heldCard.drawScale = heldCard.targetDrawScale = ShellGame.cardScaleCup;
                        heldCard.current_x = heldCard.target_x = targetX;
                    }
                } else if (heldRelic != null) {
                    heldRelic.scale = Interpolation.linear.apply(ShellGame.relicScaleNorm, ShellGame.relicScaleCup, moveTimer / startMoveTimer);
                    //x = Interpolation.linear.apply(x, targetX, moveTimer / startMoveTimer);
                    //heldRelic.targetX = heldRelic.currentX = x;
                    if (moveTimer >= startMoveTimer) {
                        heldRelic.scale = ShellGame.relicScaleCup;
                        // heldRelic.targetX = heldRelic.currentX = targetX;
                    }
                }
                break;
            }
            case SHELLINTRO: {
                if (moveTimerY < startMoveTimerY) {
                    moveTimerY += elapsed;
                    shellOffsetY = Interpolation.linear.apply(ShellGame.offscreenShellHeight, 0F, moveTimerY / startMoveTimerY);
                    alpha = Math.min(1F, Interpolation.linear.apply(startAlpha, targetAlpha, moveTimerY / (startMoveTimerY / 2)));
                } else {
                    shellOffsetY = 0F;
                    alpha = 1F;
                }
                break;
            }
            case SWITCHEROO: {
                if (isMoving) {
                    moveTimer += elapsed;
                    moveTimerY += elapsed;

                    scale = Interpolation.linear.apply(startScale, targetScale, moveTimerY / startMoveTimerY);
                    y = Interpolation.linear.apply(startY, targetY, moveTimerY / startMoveTimerY);

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

                    x = Interpolation.linear.apply(startX, targetX, moveTimer / startMoveTimer);

                    if (moveTimer >= startMoveTimer) {
                        x = targetX;
                        y = ShellGame.yMid;
                        scale = 1F;
                        isMoving = false;
                        ShellGame.receiveSwapComplete();
                    }
                }
                break;
            }
            case WAITINGFORPLAYER: {

                break;
            }
            case SHELLOUTRO: {
                if (moveTimerY < startMoveTimerY) {
                    moveTimerY += elapsed;
                    shellOffsetY = Interpolation.linear.apply(0F, ShellGame.offscreenShellHeight, moveTimerY / startMoveTimerY);
                    // alpha = Interpolation.linear.apply(targetAlpha, startAlpha, moveTimerY / startMoveTimerY);
                }
                break;
            }
        }
        hb.translate(this.x - (hb.width / 2), this.y - (hb.height / 2));
    }


    public enum animPhase {
        NONE,
        REWARDINTRO,
        REWARDINTRO2,
        REWARDMOVETOSPACE,
        SHELLINTRO,
        SWITCHEROO,
        WAITINGFORPLAYER,
        SHELLOUTRO;

        animPhase() {
        }
    }
}
