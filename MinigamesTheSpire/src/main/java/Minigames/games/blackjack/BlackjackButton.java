package Minigames.games.blackjack;

import Minigames.Minigames;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.buttons.Button;

public class BlackjackButton extends Button {
    protected BlackjackMinigame parent;
    protected TextureRegion region;
    protected Texture texture;
    protected String text = "";
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Minigames.makeID("BlackjackText"));
    protected String[] TEXT = uiStrings.TEXT;

    public BlackjackButton(float x, float y, Texture texture, BlackjackMinigame parent) {
        super(x, y, texture);
        this.parent = parent;
        this.texture = texture;
        this.region = new TextureRegion(texture);
    }

    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(this.activeColor);
        } else {
            sb.setColor(this.inactiveColor);
        }

        sb.draw(this.region, this.x, this.y, 0.0f, 0.0f, texture.getWidth(), texture.getHeight(), Settings.scale, Settings.scale, 0.0f);
        sb.setColor(Color.WHITE);
        this.hb.render(sb);
        FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, text, this.x + (float)texture.getWidth() / 2 * Settings.scale, this.y + (float)texture.getHeight() / 2 * Settings.scale, Color.WHITE.cpy());
    }

    public void dispose() {
        texture.dispose();
    }
}
