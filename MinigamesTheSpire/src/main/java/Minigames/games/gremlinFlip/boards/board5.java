package Minigames.games.gremlinFlip.boards;

import Minigames.games.gremlinFlip.constants.goldConstants;
import Minigames.games.gremlinFlip.tiles.AbstractTile;
import Minigames.games.gremlinFlip.tiles.GameTile;
import Minigames.games.gremlinFlip.tiles.InfoTile;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class board5 extends AbstractBoard {

    public board5(){ super(); }

    public void init() {

        super.init();
        if(tiles.get(0) instanceof GameTile){

            // r1
            ((GameTile) tiles.get(0)).setEnemy();
            ((GameTile) tiles.get(1)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(2)).setEnemy();
            ((GameTile) tiles.get(3)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(4)).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
            ((InfoTile) tiles.get(5)).getInfoHorizontal(tiles, 0);

            //r2
            ((GameTile) tiles.get(6)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(7)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(8)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(9)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(10)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((InfoTile) tiles.get(11)).getInfoHorizontal(tiles, 6);

            //r3
            ((GameTile) tiles.get(12)).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
            ((GameTile) tiles.get(13)).setEnemy();
            ((GameTile) tiles.get(14)).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
            ((GameTile) tiles.get(15)).setEnemy();
            ((GameTile) tiles.get(16)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((InfoTile) tiles.get(17)).getInfoHorizontal(tiles, 12);

            //r4
            ((GameTile) tiles.get(18)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(19)).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
            ((GameTile) tiles.get(20)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(21)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(22)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((InfoTile) tiles.get(23)).getInfoHorizontal(tiles, 18);

            //r5
            ((GameTile) tiles.get(24)).setEnemy();
            ((GameTile) tiles.get(25)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(26)).setEnemy();
            ((GameTile) tiles.get(27)).setGoldAmount(goldConstants.GOLD_SCORE_LOW);
            ((GameTile) tiles.get(28)).setEnemy();
            ((InfoTile) tiles.get(29)).getInfoHorizontal(tiles, 24);

            // r6
            ((InfoTile) tiles.get(30)).getInfoVertical(tiles, 0);
            ((InfoTile) tiles.get(31)).getInfoVertical(tiles, 1);
            ((InfoTile) tiles.get(32)).getInfoVertical(tiles, 2);
            ((InfoTile) tiles.get(33)).getInfoVertical(tiles, 3);
            ((InfoTile) tiles.get(34)).getInfoVertical(tiles, 4);
        }
    }

    public void render(SpriteBatch sb){
        for(AbstractTile t : tiles){ t.render(sb); }
    }
    public void update(){
        for(AbstractTile t : tiles){ t.update(); }
    }
}