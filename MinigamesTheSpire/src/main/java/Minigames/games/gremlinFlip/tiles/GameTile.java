package Minigames.games.gremlinFlip.tiles;

import Minigames.games.gremlinFlip.gremlinFlip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class GameTile extends AbstractTile {

    protected boolean isEnemy;
    protected int goldAmount;
    protected boolean flipped = false;
    protected boolean flagged = false;
    protected TextureAtlas.AtlasRegion flippedTexture;
    protected TextureAtlas.AtlasRegion flaggedTexture;

    public GameTile(int x, int y) {
        super(x, y);
        tileTexture = gremlinFlip.atlas.findRegion("tile");
        flaggedTexture = gremlinFlip.atlas.findRegion("nob_flag");
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        if ((flipped && flippedTexture != null) || Settings.isDebug) { sb.draw((TextureRegion) flippedTexture, x, y, 0, 0, flippedTexture.packedWidth, flippedTexture.packedHeight, Settings.scale, Settings.scale, 0.0F);
        } else { sb.draw((TextureRegion) tileTexture, x, y, 0, 0, tileTexture.packedWidth, tileTexture.packedHeight, Settings.scale, Settings.scale, 0.0F); }
        if (flagged) { sb.draw((TextureRegion) flaggedTexture, x, y, 0, 0, flaggedTexture.packedWidth, flaggedTexture.packedHeight, Settings.scale, Settings.scale, 0.0F); }
    }

    public void update() {
        this.hb.update();
        if (this.hb.hovered && InputHelper.justClickedLeft && !flipped) { onClicked();
        } else if (this.hb.hovered && InputHelper.justClickedRight && !flipped) {
            if (!gremlinFlip.locked) {
                if (flagged) { flagged = false;
                } else { flagged = true; }
            }
        }
    }

    public void onClicked() {
        if (!gremlinFlip.locked) {
            if (flagged) { flagged = false;
            } else {
                flipped = true;
                if (isEnemy) {
                    gremlinFlip.goldScore = 0;
                    gremlinFlip.locked = true;
                    gremlinFlip.failedMinigame = true;
                    playSfx();
                } else {
                    if (gremlinFlip.goldScore == 0) { gremlinFlip.goldScore += goldAmount;
                    } else {
                        if (goldAmount == 1) { gremlinFlip.goldScore += goldAmount;
                        } else { gremlinFlip.goldScore *= goldAmount; }
                    }
                }
            }
        }
    }
    public void unclickedShowTile() {
        if (!flipped) {
            if (flagged) { flagged = false; }
            flipped = true;
        }
    }
    public void setEnemy() {
        isEnemy = true;
        flippedTexture = gremlinFlip.atlas.findRegion("nobbed");
    }
    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
        flippedTexture = gremlinFlip.atlas.findRegion(String.valueOf(goldAmount));
    }
    public boolean isNoGoldSet(){ return goldAmount == 0; }
    public boolean isEnemy() { return isEnemy; }
    public boolean isFlipped() { return flipped; }
    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) { CardCrawlGame.sound.play("VO_GREMLINNOB_1A");
        } else if (roll == 1) { CardCrawlGame.sound.play("VO_GREMLINNOB_1B");
        } else { CardCrawlGame.sound.play("VO_GREMLINNOB_1C"); }
    }
}
