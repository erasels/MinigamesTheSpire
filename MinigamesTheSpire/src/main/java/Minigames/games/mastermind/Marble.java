package Minigames.games.mastermind;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class Marble {
    private static final int BOX_SIZE = 80;

    private static final int SIZE = 75;

    private final MastermindMinigame parent;

    public Vector2 position;

    private Texture t;

    public Hitbox hb;

    private int value;

    public Marble(MastermindMinigame parent, int x, int y, int value) {
        this.parent = parent;
        position = new Vector2(x, y);
        this.value = value;

        t = ImageMaster.loadImage(makeGamePath("mastermind/" + value + ".png"));

        hb = new Hitbox(x, y, SIZE, SIZE);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(1F, 1F, 1F, 1F);
        parent.drawTexture(sb, t, position.x, position.y, SIZE); //why this doesn't work?
        //sb.draw(t, position.x, position.y, SIZE, SIZE); //this works
    }

    public void dispose() {
        t.dispose();
    }

    public void update(float elapsed) {

    }
}