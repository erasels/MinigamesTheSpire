package Minigames.games.gremlinFlip.boards;

import Minigames.games.gremlinFlip.tiles.AbstractTile;
import Minigames.games.gremlinFlip.tiles.GameTile;
import Minigames.games.gremlinFlip.tiles.InfoTile;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;

public abstract class AbstractBoard {

    protected ArrayList<AbstractTile> tiles = new ArrayList<>();
    protected int BOARD_WIDTH;
    protected int BOARD_HEIGHT;
    protected float renderOffset = 94F * Settings.scale;

    public AbstractBoard(){
        BOARD_WIDTH = 5;
        BOARD_HEIGHT = 5;
    }

    public AbstractBoard(int w, int h){
        BOARD_WIDTH = w;
        BOARD_HEIGHT = h;
    }

    public void init(){
        int x = (int) ((Settings.HEIGHT / 1.875F) + this.renderOffset);
        int y = (int) (Settings.HEIGHT / 1.5F);

        int i = 0;

        for (int w = 0; w <= BOARD_WIDTH; w++) {
            for (int h = 0; h <= BOARD_HEIGHT; h++) {
                if(w == BOARD_WIDTH && h == BOARD_HEIGHT){  }
                else if(w == BOARD_WIDTH){ tiles.add(new InfoTile((int) (x + w * renderOffset), (int) (y - h * renderOffset))); }
                else if(h == BOARD_HEIGHT){ tiles.add(new InfoTile((int) (x + w * renderOffset), (int) (y - h * renderOffset))); }
                else { tiles.add(new GameTile((int) (x + w * renderOffset), (int) (y - h * renderOffset))); }
            }
        }
    }

    public void render(SpriteBatch sb){
        for(AbstractTile t : tiles){ t.render(sb); }
    }

    public void update(){
        for(AbstractTile t : tiles){ t.update(); }
    }

    public ArrayList<AbstractTile> getTiles(){ return tiles; }
    public AbstractTile getTile(int index){ return tiles.get(index); }

}
