package Minigames.games.blackjack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public abstract class AbstractBlackjackPlayer {
    protected final BlackjackMinigame parent;
    protected ArrayList<PokerCard> hand = new ArrayList<>();

    public AbstractBlackjackPlayer(BlackjackMinigame parent) {
        this.parent = parent;
    }

    public void dispose() {
        for (PokerCard card : hand) {
            card.dispose();
        }
    }

    public void addToHand(PokerCard card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public int getHandValue() {
        int numAces = 0;
        int pointValue = 0;
        for (PokerCard card : hand) {
            if (card.isAce) {
                numAces++;
            } else {
                pointValue += card.getBlackjackValue();
            }
        }
        int testValue;
        for (int i = 0; i < numAces; i++) {
            testValue = pointValue + PokerCard.ACE_HIGH_VALUE;
            if (testValue > BlackjackMinigame.BUST_THRESHOLD) {
                testValue = pointValue + PokerCard.ACE_LOW_VALUE;
            }
            pointValue = testValue;
        }
        return pointValue;

    }
}
