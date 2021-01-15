package Minigames.games.blackjack;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;

import static Minigames.Minigames.makeGamePath;

public class HitButton extends BlackjackButton {
    private static final Texture texture = TextureLoader.getTexture(makeGamePath("Blackjack/Cards/cardBack_red1.png"));

    public HitButton(float x, float y, BlackjackMinigame parent) {
        super(x, y, texture, parent);
        this.text = TEXT[2];
    }

    public void update() {
        super.update();
        if (pressed) {
            parent.playerHit();
            pressed = false;
        }
    }
}
