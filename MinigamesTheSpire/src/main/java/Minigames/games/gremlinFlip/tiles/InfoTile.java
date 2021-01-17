package Minigames.games.gremlinFlip.tiles;

import Minigames.games.gremlinFlip.gremlinFlip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

public class InfoTile extends AbstractTile {

    protected int goldCount = 0;
    protected int nobCount = 0;

    public InfoTile(int x, int y) {
        super(x, y);
        tileTexture = gremlinFlip.atlas.findRegion("info");
    }

    public void render(SpriteBatch sb){
        sb.setColor(Color.WHITE.cpy());
        sb.draw((TextureRegion) tileTexture, x, y, tileTexture.packedWidth /2F, tileTexture.packedHeight /2F, tileTexture.packedWidth, tileTexture.packedHeight, Settings.scale, Settings.scale, 0.0F);
        FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, String.valueOf(nobCount), this.x + FontHelper.getSmartWidth(FontHelper.buttonLabelFont, String.valueOf(nobCount), 9999f, 0F) + ((124F / 2f) * Settings.scale), this.y + FontHelper.getSmartHeight(FontHelper.buttonLabelFont, String.valueOf(nobCount), 9999f, 0F) + (24F * Settings.scale), sb.getColor());
        if(goldCount > 9){ FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, String.valueOf(goldCount), this.x + FontHelper.getSmartWidth(FontHelper.buttonLabelFont, String.valueOf(goldCount), 9999f, 0F) + ((100F / 2f) * Settings.scale), this.y + FontHelper.getSmartHeight(FontHelper.buttonLabelFont, String.valueOf(goldCount), 9999f, 0F) + (64F * Settings.scale), sb.getColor()); }
        else { FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, String.valueOf(goldCount), this.x + FontHelper.getSmartWidth(FontHelper.buttonLabelFont, String.valueOf(goldCount), 9999f, 0F) + ((124F / 2f) * Settings.scale), this.y + FontHelper.getSmartHeight(FontHelper.buttonLabelFont, String.valueOf(goldCount), 9999f, 0F) + (64F * Settings.scale), sb.getColor()); }
        }

    public void getInfoHorizontal(ArrayList<AbstractTile> tiles, int startingIndex){
        increaseInfoValues(((GameTile) tiles.get(startingIndex)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 1)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 2)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 3)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 4)));
    }

    public void getInfoVertical(ArrayList<AbstractTile> tiles, int startingIndex){
        increaseInfoValues(((GameTile) tiles.get(startingIndex)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 6)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 12)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 18)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 24)));
    }

    public void increaseInfoValues(GameTile tile){
        if(tile.isEnemy){ nobCount += 1; }
        else { goldCount += tile.goldAmount; }
    }

}
