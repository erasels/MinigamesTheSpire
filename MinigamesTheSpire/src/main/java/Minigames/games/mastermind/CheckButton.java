package Minigames.games.mastermind;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;
import static Minigames.games.mastermind.MastermindMinigame.isClicked;

public class CheckButton {

    public static final int WIDTH = 210;
    public static final int HEIGTH = 64;

    private final MastermindMinigame parent;

    public Vector2 position;
    private Texture t;
    public Hitbox hb;

    public CheckButton(MastermindMinigame parent, int x, int y) {
        this.parent = parent;
        this.position = new Vector2(x, y);

        this.t = ImageMaster.loadImage(makeGamePath("mastermind/button.png"));

        this.hb = new Hitbox(position.x, position.y, WIDTH, HEIGTH);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(1F, 1F, 1F, 1F);
        parent.drawTexture(sb, t, position.x, position.y, WIDTH, HEIGTH, 0, 0, 0, t.getWidth(), t.getHeight(), false, false);
    }

    public void dispose() {
        t.dispose();
    }

    public void update(float elapsed) {
    }

    public void doActionOnPress(Vector2 vector2) {
        if (isClicked(hb, vector2)) {
            parent.checkTheAnswer();
        }
    }
}