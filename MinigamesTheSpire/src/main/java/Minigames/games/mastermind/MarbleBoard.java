package Minigames.games.mastermind;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MarbleBoard {

    private Marble[][] marbles;

    private final MastermindMinigame parent;

    public MarbleBoard(MastermindMinigame parent) {
        this.parent = parent;

        marbles = new Marble[8][4];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                marbles[i][j] = new Marble(parent, i*100, j*100, 0);
            }
        }
    }

    public void render(SpriteBatch sb) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                marbles[i][j].render(sb);
            }
        }
    }

    public void dispose() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                marbles[i][j].dispose();
            }
        }
    }

    public void update(float elapsed) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                marbles[i][j].update(elapsed);
            }
        }
    }
}