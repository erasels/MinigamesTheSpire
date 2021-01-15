package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static Minigames.games.mastermind.Marble.BOX_SIZE;
import static Minigames.games.mastermind.Marble.MARGIN;
import static Minigames.games.mastermind.MastermindMinigame.POSSIBLE_COLORS;
import static Minigames.games.mastermind.MastermindMinigame.isClicked;

public class MarbleControllers {

    private Marble[] marbles;

    private final MastermindMinigame parent;

    private Marble activeMarble;
    private Vector2 startingVector;

    public MarbleControllers(MastermindMinigame parent) {
        this.parent = parent;

        marbles = new Marble[POSSIBLE_COLORS];
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            marbles[i] = new Marble(parent, -AbstractMinigame.SIZE / 2 + i * BOX_SIZE + MARGIN, -AbstractMinigame.SIZE / 2 + MARGIN, i + 1, null);
        }

        activeMarble = null;
        startingVector = null;
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

    public void doActionOnPress(Vector2 vector2) {
        for (int i = 0; i < POSSIBLE_COLORS; i++) {
            System.out.println(i + " " + isClicked(marbles[i].hb, vector2));
            if (isClicked(marbles[i].hb, vector2)) {
                activeMarble = marbles[i];
                startingVector = new Vector2(vector2);
            }
        }
    }

    public void doActionOnDrag(Vector2 vector2) {
        if (activeMarble != null) {
            activeMarble.dragPosition = new Vector2(activeMarble.position.x + vector2.x - startingVector.x, activeMarble.position.y + vector2.y - startingVector.y);
        }
    }

    public void doActionOnRelease(Vector2 vector2) {
        if (activeMarble != null) {
            parent.getMarbleBoard().updateState(activeMarble, vector2);
            activeMarble.dragPosition = null;
        }
        activeMarble = null;
        startingVector = null;
    }

}