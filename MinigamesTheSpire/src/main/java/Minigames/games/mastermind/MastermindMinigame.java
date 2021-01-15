package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;

import java.security.SecureRandom;

public class MastermindMinigame extends AbstractMinigame {

    public static final int NUMBER_OF_ROWS = 8;
    public static final int NUMBER_OF_COLUMNS = 4;
    public static final int POSSIBLE_COLORS = 6;

    private MarbleBoard marbleBoard;
    private MarbleControllers marbleControllers;

    private int activeRow;
    private int[] answer;

    public MastermindMinigame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        activeRow = 0;
        marbleBoard = new MarbleBoard(this);
        marbleControllers = new MarbleControllers(this);
        randomizeAnswer();
    }

    private void randomizeAnswer() {
        answer = new int[NUMBER_OF_COLUMNS];
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            answer[i] = (secureRandom.nextInt() % POSSIBLE_COLORS) + 1;
        }
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase) {
            case 0:
                marbleBoard.update(elapsed);
                marbleControllers.update(elapsed);
                break;
            case 1:
                //Do some transition effect, victory screen, idk
                phase = 2;
                break;
            case 2:
                isDone = true;
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (phase <= 0) {
            marbleBoard.render(sb);
            marbleControllers.render(sb);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        marbleBoard.dispose();
        marbleControllers.dispose();
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> this.isWithinArea(x, y) && pointer == 0, v2 -> doActionOnPress(getRelativeVector(v2)),
                new MouseHoldObject((x, y) -> doActionOnDrag(getRelativeVector(new Vector2(x, y))), ((x, y) -> doActionOnRelease(getRelativeVector(new Vector2(x, y))))));

        return bindings;
    }

    private void doActionOnPress(Vector2 vector2) {
        marbleControllers.doActionOnPress(vector2);
    }

    private void doActionOnDrag(Vector2 vector2) {
        marbleControllers.doActionOnDrag(vector2);
    }

    private boolean doActionOnRelease(Vector2 vector2) {
        marbleControllers.doActionOnRelease(vector2);
        return false;
    }

    public MarbleBoard getMarbleBoard() {
        return marbleBoard;
    }

    public MarbleControllers getMarbleControllers() {
        return marbleControllers;
    }

    public int getActiveRow() {
        return activeRow;
    }

    public static boolean isClicked(Hitbox hb, Vector2 v2) {
        Hitbox mouseClickHitbox = new Hitbox(0, 0);
        mouseClickHitbox.move(v2.x, v2.y);
        return hb.intersects(mouseClickHitbox);
    }
}
