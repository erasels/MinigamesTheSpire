package Minigames.games.blackjack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.buttons.Button;

import static Minigames.Minigames.makeGamePath;

public class BlackjackButton extends Button {
    protected BlackjackMinigame parent;
    protected Texture texture;

    public BlackjackButton(float x, float y, Texture texture, BlackjackMinigame parent) {
        super(x, y, texture);
        this.parent = parent;
        this.texture = texture;
    }

//    public void render(SpriteBatch sb)
//    {
//        parent.drawTexture(sb, texture, x, y, 0, texture.getWidth(), texture.getHeight(), false, false);
//    }
}
