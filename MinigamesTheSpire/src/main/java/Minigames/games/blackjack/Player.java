package Minigames.games.blackjack;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player extends AbstractBlackjackPlayer {

    public Player(BlackjackMinigame parent) {
        super(parent);
    }

    public void render(SpriteBatch sb) {
        int count = 1;
        int initalXOffset = -AbstractMinigame.SIZE / 2;
        for (PokerCard card : hand) {
            card.render(sb, new Vector2(initalXOffset + (float)AbstractMinigame.SIZE / 4 * count, (float)-AbstractMinigame.SIZE / 3));
            count++;
        }
    }
}
