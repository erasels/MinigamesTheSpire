package Minigames.games.gremlinFlip.tiles;

import Minigames.games.gremlinFlip.gremlinFlip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static Minigames.Minigames.makeGamePath;

public class GameTile extends AbstractTile {

    private int shading;
    protected boolean isEnemy;
    protected int goldAmount;
    protected boolean flipped = false;
    protected boolean flagged = false;

    protected Texture flippedTexture;
    protected Texture flaggedTexture;

    public static final Logger logger = LogManager.getLogger(GameTile.class.getName());


    public GameTile(int x, int y) {
        super(x, y);
        this.tileTexture = new Texture(makeGamePath("gremlinflip/tile.png"));
        this.flaggedTexture = new Texture(makeGamePath("gremlinflip/nob_flag.png"));
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        if ((flipped && flippedTexture != null) || Settings.isDebug) { sb.draw(this.flippedTexture, this.x, this.y, this.flippedTexture.getWidth() / 2.0F, this.flippedTexture.getHeight() / 2.0F, this.flippedTexture.getWidth(), this.flippedTexture.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, this.flippedTexture.getWidth(), this.flippedTexture.getHeight(), false, false);
        } else { sb.draw(this.tileTexture, this.x, this.y, this.tileTexture.getWidth() / 2.0F, this.tileTexture.getHeight() / 2.0F, this.tileTexture.getWidth(), this.tileTexture.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, this.tileTexture.getWidth(), this.tileTexture.getHeight(), false, false); }
        if (flagged) { sb.draw(this.flaggedTexture, this.x, this.y, this.flaggedTexture.getWidth() / 2.0F, this.flaggedTexture.getHeight() / 2.0F, this.flaggedTexture.getWidth(), this.flaggedTexture.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, this.flaggedTexture.getWidth(), this.flaggedTexture.getHeight(), false, false); }
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
        this.flippedTexture = new Texture(makeGamePath("gremlinflip/nobbed.png"));
    }
    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
        this.flippedTexture = new Texture(makeGamePath("gremlinflip/" + String.valueOf(goldAmount)) + ".png");
    }
    public boolean isFlipped() { return flipped; }
    public boolean isEnemy() { return isEnemy; }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) { CardCrawlGame.sound.play("VO_GREMLINNOB_1A");
        } else if (roll == 1) { CardCrawlGame.sound.play("VO_GREMLINNOB_1B");
        } else { CardCrawlGame.sound.play("VO_GREMLINNOB_1C"); }
    }
}
