package Minigames.games.blackjack;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static Minigames.Minigames.makeGamePath;
import static Minigames.games.blackjack.BlackjackMinigame.MAX_BET;

public class BetButton extends BlackjackButton {
    private static final Texture texture = TextureLoader.getTexture(makeGamePath("Blackjack/Cards/cardBack_blue1.png"));
    private int bet;

    public BetButton(float x, float y, BlackjackMinigame parent) {
        super(x, y, texture, parent);
        setBet();
    }

    public void setBet() {
        bet = Math.min(AbstractDungeon.player.gold, MAX_BET);
        this.text = TEXT[0] + bet + TEXT[1];
    }

    public void update() {
        super.update();
        if (pressed) {
            parent.setBet(bet);
            pressed = false;
        }
    }
}
