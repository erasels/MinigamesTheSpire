package Minigames.games.blackjack;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Dealer extends AbstractBlackjackPlayer{
    private float timer;
    private static final float TIMER_THRESHOLD = 1.0f;

    public Dealer(BlackjackMinigame parent) {
        super(parent);
    }

    public void render(SpriteBatch sb) {
        int count = 1;
        int initalXOffset = -AbstractMinigame.SIZE / 2;
        for (PokerCard card : hand) {
            card.render(sb, new Vector2(initalXOffset + (float)AbstractMinigame.SIZE / 8 * count, (float)AbstractMinigame.SIZE / 3));
            count++;
        }
    }

    public void update(float elapsed) {
        timer += elapsed;
        if (timer >= TIMER_THRESHOLD) {
            takeTurn();
            timer = 0;
        }
    }

    public void flipUpCard() {
        for (PokerCard card : hand) {
            if (card.isFaceDown) {
                card.flipOver();
            }
        }
    }

    public void takeTurn() {
        if (getHandValue() >= 17) {
            parent.compareHands();
        } else {
            parent.hit(this);
        }
    }
}
