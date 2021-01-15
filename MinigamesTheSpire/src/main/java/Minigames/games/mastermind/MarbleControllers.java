package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static Minigames.games.mastermind.Marble.BOX_SIZE;
import static Minigames.games.mastermind.Marble.MARGIN;
import static Minigames.games.mastermind.MastermindMinigame.POSSIBLE_COLORS;

public class MarbleControllers {

    private Marble[] marbles;

    private final MastermindMinigame parent;

    private Marble activeMarble;

    public MarbleControllers(MastermindMinigame parent) {
        this.parent = parent;

        marbles = new Marble[POSSIBLE_COLORS];
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            marbles[i] = new Marble(parent, -AbstractMinigame.SIZE / 2 + i * BOX_SIZE + MARGIN, -AbstractMinigame.SIZE / 2 + MARGIN, i + 1, null);
        }

        activeMarble = null;
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

    public void doActionOnPress(Vector2 vector2) {
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            if (marbles[i].hb.clicked) {
                activeMarble = marbles[i];
            }
        }
    }

    public void doActionOnDrag(Vector2 vector2) {
        if (activeMarble != null) {
            activeMarble.dragPosition.x = vector2.x;
            activeMarble.dragPosition.y = vector2.y;
        }
    }

    public void doActionOnRelease(Vector2 vector2) {
        if (activeMarble != null) {
            parent.getMarbleBoard().updateState(activeMarble, vector2);
        }
        activeMarble = null;
    }

}