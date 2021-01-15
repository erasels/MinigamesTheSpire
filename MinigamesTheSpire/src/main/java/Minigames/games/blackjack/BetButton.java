package Minigames.games.blackjack;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class BetButton extends BlackjackButton {
    private static final Texture texture = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardBack_blue1.png"));

    public BetButton(float x, float y, BlackjackMinigame parent) {
        super(x, y, texture, parent);
    }

    public void update() {
        super.update();
        if (pressed) {
            parent.setBet();
            pressed = false;
        }
    }
}
