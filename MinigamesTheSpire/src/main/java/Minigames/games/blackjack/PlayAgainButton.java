package Minigames.games.blackjack;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static Minigames.Minigames.makeGamePath;

public class PlayAgainButton extends BlackjackButton {
    private static final Texture texture = TextureLoader.getTexture(makeGamePath("Blackjack/Cards/cardBack_blue1.png"));

    public PlayAgainButton(float x, float y, BlackjackMinigame parent) {
        super(x, y, texture, parent);
        text = TEXT[10];
    }

    public void update() {
        if (parent.canPlayAgain()) {
            super.update();
            if (pressed) {
                parent.playAgain();
                pressed = false;
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (parent.canPlayAgain()) {
            super.render(sb);
        }
    }
}
