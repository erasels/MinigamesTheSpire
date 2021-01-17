package Minigames.games.blackjack;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class StandButton extends BlackjackButton {
    private static final Texture texture = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardBack_green1.png"));

    public StandButton(float x, float y, BlackjackMinigame parent) {
        super(x, y, texture, parent);
        this.text = TEXT[3];
    }

    public void update() {
        super.update();
        if (pressed) {
            parent.startDealerTurn();
            pressed = false;
        }
    }
}
