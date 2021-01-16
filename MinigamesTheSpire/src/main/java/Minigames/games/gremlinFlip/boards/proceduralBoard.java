package Minigames.games.gremlinFlip.boards;

import Minigames.Minigames;
import Minigames.games.gremlinFlip.constants.goldConstants;
import Minigames.games.gremlinFlip.tiles.AbstractTile;
import Minigames.games.gremlinFlip.tiles.GameTile;
import Minigames.games.gremlinFlip.tiles.InfoTile;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class proceduralBoard extends AbstractBoard {

    public static final Logger logger = LogManager.getLogger(Minigames.class.getName());

    public enum DIFFICULTY_LEVEL {
        DIFFICULTY_LEVEL_1(new int[][] {{3, 1, 6}, {0, 3, 6}, {5, 0, 6}, {2, 2, 6}, {4, 1, 6}}),
        DIFFICULTY_LEVEL_2(new int[][] {{1, 3, 7}, {6, 0, 7}, {3, 2, 7}, {0, 4, 7}, {5, 1, 7}}),
        DIFFICULTY_LEVEL_3(new int[][] {{2, 3, 8}, {7, 0, 8}, {4, 2, 8}, {1, 4, 8}, {6, 1, 8}}),
        DIFFICULTY_LEVEL_4(new int[][] {{3, 3, 8}, {0, 5, 8}, {8, 0, 10}, {5, 2, 10}, {2, 4, 10}}),
        DIFFICULTY_LEVEL_5(new int[][] {{7, 1, 10}, {4, 3, 10}, {1, 5, 10}, {9, 0, 10}, {6, 2, 10}}),
        DIFFICULTY_LEVEL_6(new int[][] {{3, 4, 10}, {0, 6, 10}, {8, 1, 10}, {5, 3, 10}, {2, 5, 10}}),
        DIFFICULTY_LEVEL_7(new int[][] {{7, 2, 10}, {4, 4, 10}, {1, 6, 13}, {9, 1, 13}, {6, 3, 10}}),
        DIFFICULTY_LEVEL_8(new int[][] {{2, 8, 15}, {4, 6, 13}, {5, 6, 14}, {3, 8, 14}, {2, 9, 14}});

        private final int[][] levelData;
        DIFFICULTY_LEVEL(int[][] levelData) { this.levelData = levelData; }
        public int[] returnBoardBounds(){ return levelData[AbstractDungeon.eventRng.random(levelData.length - 1)]; }
    }
    private DIFFICULTY_LEVEL LEVEL;
    private int x2Amount;
    private int x3Amount;
    private int nobAmount;

    public proceduralBoard(){ super(); }
    public void init(){
        super.init();
        generateBoardBasedOnAscension(AbstractDungeon.ascensionLevel);
    }

    public void generateBoardBasedOnAscension(int ascension){
        LEVEL = (ascension <= 2) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_1 :
                        (ascension <= 5) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_2 :
                                (ascension <= 8) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_3 :
                                        (ascension <= 11) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_4 :
                                                (ascension <= 14) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_5 :
                                                        (ascension <= 17) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_6 :
                                                                (ascension <= 19) ? DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_7 : DIFFICULTY_LEVEL.DIFFICULTY_LEVEL_8;

        logger.info(LEVEL.toString());
        int[] levelBounds = LEVEL.returnBoardBounds();
        x2Amount = levelBounds[0];
        x3Amount = levelBounds[1];
        nobAmount = levelBounds[2];
        do{ for(AbstractTile t: this.getTiles()){ if(isInsertableTile(t)){ setTile(t); } } }
        while (x2Amount != 0 && x3Amount != 0 && nobAmount != 0);
        generateInfoTiles();
    }
    public boolean isInsertableTile(AbstractTile t){ return (t instanceof GameTile && (!((GameTile) t).isEnemy() && ((GameTile) t).isNoGoldSet())); }
    public void setTile(AbstractTile t){
        int result = AbstractDungeon.eventRng.random(2);
        switch (result){
            case 0:
                if(x2Amount != 0){
                    ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_MEDIUM);
                    x2Amount -= 1;
                }
                else if(x3Amount != 0){
                    ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
                    x3Amount -= 1;
                }
                else if (nobAmount != 0){
                    ((GameTile) t).setEnemy();
                    nobAmount -= 1;
                }
                else { ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_LOW); }
                break;
            case 1:
                if(x3Amount != 0){
                    ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
                    x3Amount -= 1;
                }
                else if(x2Amount != 0){
                    ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_MEDIUM);
                    x2Amount -= 1;
                }
                else if (nobAmount != 0){
                    ((GameTile) t).setEnemy();
                    nobAmount -= 1;
                }
                else { ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_LOW); }
                break;
            case 2:
                if(nobAmount != 0){
                    ((GameTile) t).setEnemy();
                    nobAmount -= 1;
                }
                else if(x2Amount != 0){
                    ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_MEDIUM);
                    x2Amount -= 1;
                }
                else if(x3Amount != 0){
                    ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_HIGH);
                    x3Amount -= 1;
                }
                else { ((GameTile) t).setGoldAmount(goldConstants.GOLD_SCORE_LOW); }
                break;
        }
    }
    public void generateInfoTiles(){
        getHorizontal();
        getVertical();
    }
    public void getHorizontal(){
        ((InfoTile) tiles.get(5)).getInfoHorizontal(tiles, 0);
        ((InfoTile) tiles.get(11)).getInfoHorizontal(tiles, 6);
        ((InfoTile) tiles.get(17)).getInfoHorizontal(tiles, 12);
        ((InfoTile) tiles.get(23)).getInfoHorizontal(tiles, 18);
        ((InfoTile) tiles.get(29)).getInfoHorizontal(tiles, 24);
    }
    public void getVertical(){
        ((InfoTile) tiles.get(30)).getInfoVertical(tiles, 0);
        ((InfoTile) tiles.get(31)).getInfoVertical(tiles, 1);
        ((InfoTile) tiles.get(32)).getInfoVertical(tiles, 2);
        ((InfoTile) tiles.get(33)).getInfoVertical(tiles, 3);
        ((InfoTile) tiles.get(34)).getInfoVertical(tiles, 4);
    }

    public String returnDifficultyLevel(){ return LEVEL.toString(); }
}
