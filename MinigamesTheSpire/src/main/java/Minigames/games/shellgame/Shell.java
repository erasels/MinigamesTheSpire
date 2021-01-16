package Minigames.games.shellgame;

import Minigames.events.AbstractMinigameEvent;
import Minigames.games.AbstractMinigame;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static Minigames.Minigames.makeGamePath;

public class Shell {

    private enum RewardType {
        CARD,
        RELIC
    }

    static Texture shellTex = TextureLoader.getTexture(makeGamePath("shells/shell.png"));
    static Texture shellInBetweenTex = TextureLoader.getTexture(makeGamePath("shells/shellbetween.png"));
    static Texture shellUpTex = TextureLoader.getTexture(makeGamePath("shells/shellup.png"));

    private float x;
    private float y;
    private AbstractCard heldCard;
    private AbstractRelic heldRelic;

    public Shell(float x, float y, AbstractCard held) {
        this.x = x;
        this.y = y;
        this.heldCard = held;
    }

    public Shell(float x, float y, AbstractRelic held) {
        this.x = x;
        this.y = y;
        this.heldRelic = held;
    }

    public void render(SpriteBatch sb) {
        if (heldCard != null) {
            heldCard.render(sb);
        }
        if (heldRelic != null) {
            heldRelic.render(sb);
        }

    }
}
