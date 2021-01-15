package Minigames.games.blackjack;

import Minigames.Minigames;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.buttons.Button;

public class BlackjackButton extends Button {
    protected BlackjackMinigame parent;
    protected Texture texture;
    protected String text = "";
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Minigames.makeID("BlackjackText"));
    protected String[] TEXT = uiStrings.TEXT;

    public BlackjackButton(float x, float y, Texture texture, BlackjackMinigame parent) {
        super(x, y, texture);
        this.parent = parent;
        this.texture = texture;
    }

    public void render(SpriteBatch sb) {
       super.render(sb);
       FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, text, this.x + (float)texture.getWidth() / 2, this.y + (float)texture.getHeight() / 2, Color.WHITE.cpy());
    }
}
