package Minigames.games.mastermind;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

public class Marble {

    public static final int BOX_SIZE = 64;
    public static final int SIZE = 60;
    public static final int MARGIN = BOX_SIZE / 4;
    public static final int EMPTY = 0;
    public static final int SELECTED_EMPTY = -1;

    private final MastermindMinigame parent;

    public Vector2 position;

    public Vector2 dragPosition;

    private Texture t;

    public Hitbox hb;

    private int value;

    private Integer row;

    public Marble(MastermindMinigame parent, int x, int y, int value, Integer row) {
        this.parent = parent;
        this.position = new Vector2(x, y);
        this.dragPosition = null;
        this.value = value;
        this.row = row;

        this.t = getTexture(parent, value);

        this.hb = new Hitbox(position.x, position.y, SIZE, SIZE);
    }

    public void updateValue(int newValue) {
        this.value = newValue;
        this.t = getTexture(parent, value);
    }

    private Texture getTexture(MastermindMinigame parent, int value) {
        int valueToRender = value == EMPTY ? (row == parent.getActiveRow() ? SELECTED_EMPTY : EMPTY) : value;
        return ImageMaster.loadImage(makeGamePath("mastermind/" + valueToRender + ".png"));
    }

    public void render(SpriteBatch sb) {
        sb.setColor(1F, 1F, 1F, 1F);
        parent.drawTexture(sb, t, dragPosition != null ? dragPosition.x : position.x, dragPosition != null ? dragPosition.y : position.y, SIZE, SIZE, 0, 0, 0, t.getWidth(), t.getHeight(), false, false);
    }

    public void dispose() {
        t.dispose();
    }

    public void update(float elapsed) {
    }

    public int getValue() {
        return value;
    }
}