package Minigames.games.gremlinFlip.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

public abstract class AbstractTile {

    protected TextureAtlas.AtlasRegion tileTexture;

    protected int x;
    protected int y;
    protected float renderOffset = 94F * Settings.scale;

    protected Hitbox hb;


    public AbstractTile(int x, int y){
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, renderOffset, renderOffset);
    }

    public void render(SpriteBatch sb){
    }

    public void update(){

    }

    public void onClicked(){

    }
}
