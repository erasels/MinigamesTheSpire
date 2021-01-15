package Minigames.games.blackjack;

import java.util.ArrayList;

public abstract class AbstractBlackjackPlayer {
    protected final BlackjackMinigame parent;
    protected ArrayList<PokerCard> hand = new ArrayList<>();
    public boolean busted = false;

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
        if (numAces == 0) {
            return pointValue;
        }
        int testValue;
        for (int i = 0; i <= numAces; i++) {
            //tries all combination of ace values starting from the highest
            testValue = pointValue + ((numAces - i) * PokerCard.ACE_HIGH_VALUE) + (i * PokerCard.ACE_LOW_VALUE);
            if (testValue <= BlackjackMinigame.BUST_THRESHOLD) {
                return testValue;
            }
            if (i == numAces) { //always return on last iteration
                return testValue;
            }
        }
        return pointValue;
    }
}
