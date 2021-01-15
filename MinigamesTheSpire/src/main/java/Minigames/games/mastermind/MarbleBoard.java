package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;

import static Minigames.games.mastermind.Marble.BOX_SIZE;
import static Minigames.games.mastermind.Marble.MARGIN;
import static Minigames.games.mastermind.MastermindMinigame.*;

public class MarbleBoard {

    private Marble[][] marbles;

    private final MastermindMinigame parent;

    public MarbleBoard(MastermindMinigame parent) {
        this.parent = parent;

        marbles = new Marble[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j] = new Marble(parent, -AbstractMinigame.SIZE / 2 + j * BOX_SIZE + MARGIN, -AbstractMinigame.SIZE / 2 + i * BOX_SIZE + 2 * BOX_SIZE - MARGIN, 0, i);
            }
        }
    }

    public void render(SpriteBatch sb) {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].render(sb);
            }
        }
    }

    public void dispose() {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].dispose();
            }
        }
    }

    public void update(float elapsed) {
        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                marbles[i][j].update(elapsed);
            }
        }
    }

    public void updateState(Marble activeMarble, Vector2 vector2) {
        System.out.println("vector2: " + vector2);
        System.out.println("activeRow: " + parent.getActiveRow());
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            System.out.println(i + ": " + isClicked(marbles[parent.getActiveRow()][i].hb, vector2));
            if (isClicked(marbles[parent.getActiveRow()][i].hb, vector2)) {
                marbles[parent.getActiveRow()][i].updateValue(activeMarble.getValue());
            }
        }
    }
}