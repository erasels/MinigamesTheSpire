package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static Minigames.games.mastermind.Marble.*;
import static Minigames.games.mastermind.MastermindMinigame.*;

public class MarbleBoard {

    private Marble[][] marbles;
    private Marble[][] hints;

    private final MastermindMinigame parent;

    public MarbleBoard(MastermindMinigame parent) {
        this.parent = parent;

        marbles = new Marble[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        hints = new Marble[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j] = new Marble(parent,
                        -AbstractMinigame.SIZE / 2 + j * BOX_SIZE + MARGIN,
                        -AbstractMinigame.SIZE / 2 + i * BOX_SIZE + 2 * BOX_SIZE - MARGIN,
                        0, false, i);
                hints[i][j] = new Marble(parent,
                        -AbstractMinigame.SIZE / 2 + NUMBER_OF_COLUMNS * BOX_SIZE + 2 * MARGIN + j * HINT_BOX_SIZE,
                        -AbstractMinigame.SIZE / 2 + i * BOX_SIZE + 2 * BOX_SIZE,
                        0, true, i);
            }
        }

    }

    public void render(SpriteBatch sb) {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].render(sb);
                hints[i][j].render(sb);
            }
        }
    }

    public void dispose() {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].dispose();
                hints[i][j].dispose();
            }
        }
    }

    public void doActionOnPress(Vector2 vector2) {
        int activeRow = parent.getActiveRow();
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if(isClicked(marbles[activeRow][i].hb, vector2)) {
                marbles[activeRow][i].updateValueAndResetTexture(EMPTY);
            }
        }
    }

    public void update(float elapsed) {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].update(elapsed);
                hints[i][j].update(elapsed);
            }
        }
    }

    public void updateValue(Marble activeMarble, Vector2 vector2) {
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (isClicked(marbles[parent.getActiveRow()][i].hb, vector2)) {
                marbles[parent.getActiveRow()][i].updateValueAndResetTexture(activeMarble.getValue());
            }
        }
    }

    public Marble[][] getMarbles() {
        return marbles;
    }

    public void updateHints(int numberOfBlack, int numberOfWhite) {
        int activeRow = parent.getActiveRow();
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (numberOfBlack > 0) {
                hints[activeRow][i].updateValue(HINT_BLACK);
                numberOfBlack--;
            } else if (numberOfWhite > 0) {
                hints[activeRow][i].updateValue(HINT_WHITE);
                numberOfWhite--;
            } else{
                hints[activeRow][i].updateValue(HINT_NOTHING);
            }
        }
    }

    public void resetTextures() {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].resetTexture();
                hints[i][j].resetTexture();
            }
        }
    }

}