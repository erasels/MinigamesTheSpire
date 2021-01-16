package Minigames.games.shellgame;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static Minigames.Minigames.makeGamePath;

public class Shell {

    private enum RewardType {
        CARD,
        RELIC
    }

    static Texture shellTex = TextureLoader.getTexture(makeGamePath("shells/lagavulinshell.png"));

    private float x;
    private float y;
    private AbstractCard heldCard;
    private AbstractRelic heldRelic;
    private Hitbox hb;

    public Shell(float x, float y, AbstractCard held) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, shellTex.getWidth(), shellTex.getHeight());
        this.heldCard = held;
    }

    public Shell(float x, float y, AbstractRelic held) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, shellTex.getWidth(), shellTex.getHeight());
        this.heldRelic = held;
    }

    public void render(SpriteBatch sb) {
        if (heldCard != null) {
            heldCard.render(sb);
        }
        if (heldRelic != null) {
            heldRelic.render(sb);
        }
        sb.draw(shellTex, x, y); //TODO: Scale
    }
}
