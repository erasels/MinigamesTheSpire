package Minigames.games.mastermind;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static Minigames.games.mastermind.MastermindMinigame.POSSIBLE_COLORS;

public class MarbleControllers {

    private Marble[] marbles;

    private final MastermindMinigame parent;

    public MarbleControllers(MastermindMinigame parent) {
        this.parent = parent;

        marbles = new Marble[POSSIBLE_COLORS];
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            marbles[i] = new Marble(parent, 900, 100 * i, i);
        }
    }

    public void render(SpriteBatch sb) {
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            marbles[i].render(sb);
        }
    }

    public void dispose() {
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            marbles[i].dispose();
        }
    }

    public void update(float elapsed) {
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            marbles[i].update(elapsed);
        }
    }

    public void onMouse(Vector2 vector2) {
        for (int i = 0; i < POSSIBLE_COLORS; i++) {

        }
    }
}