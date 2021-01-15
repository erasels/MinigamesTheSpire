package Minigames.games.gremlinFlip.tiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.ArrayList;

import static Minigames.Minigames.makeGamePath;

public class InfoTile extends AbstractTile {

    protected Texture nobCountTexture;

    protected int goldCount = 0;
    protected int nobCount = 0;

    public InfoTile(int x, int y) {
        super(x, y);
        this.tileTexture = new Texture(makeGamePath("gremlinflip/info.png"));
    }

    public void render(SpriteBatch sb){
        sb.setColor(Color.WHITE.cpy());
        sb.draw(this.tileTexture, this.x, this.y, this.tileTexture.getWidth() / 2.0F, this.tileTexture.getHeight() / 2.0F, this.tileTexture.getWidth(), this.tileTexture.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, this.tileTexture.getWidth(), this.tileTexture.getHeight(), false, false);
        if(nobCountTexture != null){ sb.draw(this.nobCountTexture, this.x, this.y, this.nobCountTexture.getWidth() / 2.0F, this.nobCountTexture.getHeight() / 2.0F, this.nobCountTexture.getWidth(), this.nobCountTexture.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, this.nobCountTexture.getWidth(), this.nobCountTexture.getHeight(), false, false); }
        FontHelper.renderFont(sb, FontHelper.buttonLabelFont, String.valueOf(goldCount), this.x + ((136F / 2f) * Settings.scale), this.y + (78F * Settings.scale), sb.getColor());
    }

    public void getInfoHorizontal(ArrayList<AbstractTile> tiles, int startingIndex){
        increaseInfoValues(((GameTile) tiles.get(startingIndex)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 1)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 2)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 3)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 4)));
        nobCountTexture = new Texture(makeGamePath("gremlinflip/nobs_" + String.valueOf(nobCount) + ".png"));
    }

    public void getInfoVertical(ArrayList<AbstractTile> tiles, int startingIndex){
        increaseInfoValues(((GameTile) tiles.get(startingIndex)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 6)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 12)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 18)));
        increaseInfoValues(((GameTile) tiles.get(startingIndex + 24)));
        nobCountTexture = new Texture(makeGamePath("gremlinflip/nobs_" + String.valueOf(nobCount) + ".png"));
    }

    public void increaseInfoValues(GameTile tile){
        if(tile.isEnemy){ nobCount += 1; }
        else { goldCount += tile.goldAmount; }
    }

}
