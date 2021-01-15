package Minigames.games.blackjack;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class LeaveButton extends BlackjackButton {
    private static final Texture texture = TextureLoader.getTexture(makeGamePath("Blackjack/Cards/cardBack_blue1.png"));

    public LeaveButton(float x, float y, BlackjackMinigame parent) {
        super(x, y, texture, parent);
    }

    public void update() {
        super.update();
        if (pressed) {
            parent.setPhase(BlackjackMinigame.LEAVE);
            pressed = false;
        }
    }
}
