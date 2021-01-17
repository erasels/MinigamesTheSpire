package Minigames.games.blackjack;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class PokerCard {

    public enum Suite {
        Clubs, Diamonds, Hearts, Spades;
    }

    public static final int ROYAL_VALUE = 10;
    public static final int ACE_HIGH_VALUE = 11;
    public static final int ACE_LOW_VALUE = 1;
    public static final Texture cardBack = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardBack_blue2.png"));

    private int value;
    private Suite suite;
    public boolean isAce;
    public boolean isFaceDown = false;
    private Texture t;
    private Color c;
    private BlackjackMinigame parent;

    public PokerCard(int value, Suite suite, boolean isAce, BlackjackMinigame parent) {
        this.value = value;
        this.suite = suite;
        this.isAce = isAce;
        this.parent = parent;
        c = Color.WHITE.cpy();
        if (value <= 10) {
            t = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/card" + suite.toString() + value + ".png"));
        } else {
            switch (value) {
                case 11: {
                    t = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardJ.png"));
                    break;
                }
                case 12: {
                    if (suite == Suite.Hearts || suite == Suite.Spades) {
                        t = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardHeartsQ.png"));
                    } else {
                        t = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardDiamondsQ.png"));
                    }
                    break;
                }
                case 13: {
                    t = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/cardK.png"));
                    break;
                }
                case 14: {
                    t = ImageMaster.loadImage(makeGamePath("Blackjack/Cards/card" + suite.toString() + "A.png"));
                    break;
                }
            }
        }
    }

    public PokerCard(int value, Suite suite, BlackjackMinigame parent) {
        this(value, suite, false, parent);
    }

    public void render(SpriteBatch sb, Vector2 position)
    {
        sb.setColor(c);
        if (isFaceDown) {
            parent.drawTexture(sb, cardBack, position.x, position.y, 0, cardBack.getWidth(), cardBack.getHeight(), false, false);
        } else {
            parent.drawTexture(sb, t, position.x, position.y, 0, t.getWidth(), t.getHeight(), false, false);
        }
    }

    public void flipOver() {
        isFaceDown = !isFaceDown;
    }

    public int getBlackjackValue() {
        if (isAce) {
            return 0;
        }
        if (value <= 10){
            return value;
        } else {
            return ROYAL_VALUE;
        }
    }

    public void dispose() {
        t.dispose();
    }

    @Override
    public String toString() {
        return value + " of " + suite.toString();
    }
}
