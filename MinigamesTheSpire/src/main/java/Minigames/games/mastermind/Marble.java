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
    public static final int HINT_BOX_SIZE = BOX_SIZE / 2;
    public static final int HINT_SIZE = SIZE / 2;
    public static final int MARGIN = BOX_SIZE / 4;
    public static final int EMPTY = 0;
    public static final int SELECTED_EMPTY = -1;
    public static final int HINT_BLACK = -2;
    public static final int HINT_WHITE = -3;
    public static final int HINT_NOTHING = -4;

    private final MastermindMinigame parent;

    public Vector2 position;

    public Vector2 dragPosition;

    private Texture t;

    public Hitbox hb;

    private int value;

    private Integer row;

    boolean isHint;

    public Marble(MastermindMinigame parent, int x, int y, int value, boolean isHint, Integer row) {
        this.parent = parent;
        this.position = new Vector2(x, y);
        this.dragPosition = null;
        this.value = value;
        this.isHint = isHint;
        this.row = row;

        this.t = getTexture();

        this.hb = new Hitbox(position.x, position.y, getSize(), getSize());
    }

    public void updateValueAndResetTexture(int newValue) {
        updateValue(newValue);
        resetTexture();
    }

    public void updateValue(int newValue) {
        this.value = newValue;
    }

    public void resetTexture() {
        this.t = getTexture();
    }

    private Texture getTexture() {
        int valueToRender = value == EMPTY ? (row == parent.getActiveRow() ? SELECTED_EMPTY : EMPTY) : value;
        return ImageMaster.loadImage(makeGamePath("mastermind/" + valueToRender + ".png"));
    }

    public void render(SpriteBatch sb) {
        sb.setColor(1F, 1F, 1F, 1F);
        parent.drawTexture(sb, t, dragPosition != null ? dragPosition.x : position.x, dragPosition != null ? dragPosition.y : position.y, getSize(), getSize(), 0, 0, 0, t.getWidth(), t.getHeight(), false, false);
    }

    public int getBoxSize(){
        return isHint ? HINT_BOX_SIZE : BOX_SIZE;
    }

    public int getSize(){
        return isHint ? HINT_SIZE : SIZE;
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