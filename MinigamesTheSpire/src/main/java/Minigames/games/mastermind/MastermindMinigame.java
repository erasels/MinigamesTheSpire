package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import static Minigames.Minigames.logger;
import static Minigames.Minigames.makeGamePath;
import static Minigames.games.mastermind.Marble.*;
import static Minigames.games.mastermind.Marble.BOX_SIZE;

public class MastermindMinigame extends AbstractMinigame {

    public static final int NUMBER_OF_ROWS = 8;
    public static final int NUMBER_OF_COLUMNS = 4;
    public static final int NUMBER_OF_POSSIBLE_COLORS = 6;

    private MarbleBoard marbleBoard;
    private MarbleControllers marbleControllers;
    private CheckButton checkButton;

    private int activeRow;
    private int[] answer;

    //background
    private static final int BG_SIZE = 648;
    private Texture background;

    public MastermindMinigame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        background = ImageMaster.loadImage(makeGamePath("mastermind/background.png"));
        activeRow = 0;
        marbleBoard = new MarbleBoard(this);
        marbleControllers = new MarbleControllers(this);
        checkButton = new CheckButton(this,
                -AbstractMinigame.SIZE / 2 + NUMBER_OF_POSSIBLE_COLORS * BOX_SIZE + 2 * MARGIN,
                -AbstractMinigame.SIZE / 2 + MARGIN);
        randomizeAnswer();
    }

    private void randomizeAnswer() {
        answer = new int[NUMBER_OF_COLUMNS];
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            answer[i] = (ThreadLocalRandom.current().nextInt(NUMBER_OF_POSSIBLE_COLORS)) + 1;
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

        sb.setColor(1, 1, 1, 1);
        drawTexture(sb, background, 0, 0, BG_SIZE);

        if (phase <= 0) {
            marbleBoard.render(sb);
            marbleControllers.render(sb);
            checkButton.render(sb);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        marbleBoard.dispose();
        marbleControllers.dispose();
        checkButton.dispose();
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
        marbleBoard.doActionOnPress(vector2);
        checkButton.doActionOnPress(vector2);
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

    public int getActiveRow() {
        return activeRow;
    }

    public static boolean isClicked(Hitbox hb, Vector2 v2) {
        Hitbox mouseClickHitbox = new Hitbox(0, 0);
        mouseClickHitbox.move(v2.x, v2.y);
        return hb.intersects(mouseClickHitbox);
    }

    public void checkTheAnswer() {
        Marble[][] marbles = marbleBoard.getMarbles();

        boolean allEmpty = true;
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (marbles[activeRow][i].getValue() != EMPTY) {
                allEmpty = false;
            }
        }
        if (allEmpty) {
            logger.warn("[Mastermind] - not checking the answer, because it's fully empty.");
            return;
        }

        int numberOfBlack = 0;
        int numberOfBlackAndWhite = 0;
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (marbles[activeRow][i].getValue() == answer[i]) {
                numberOfBlack++;
            }
        }
        for (int color = 1; color <= NUMBER_OF_POSSIBLE_COLORS; color++) {
            int inAnswer = 0;
            int inGuess = 0;
            for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
                if (answer[i] == color) {
                    inAnswer++;
                }
                if (marbles[activeRow][i].getValue() == color) {
                    inGuess++;
                }
            }
            numberOfBlackAndWhite += Math.min(inAnswer, inGuess);
        }
        int numberOfWhite = numberOfBlackAndWhite - numberOfBlack;

        marbleBoard.updateHints(numberOfBlack, numberOfWhite);
        activeRow++;
        marbleBoard.resetTextures();
        if (numberOfBlack == NUMBER_OF_COLUMNS) {
            System.out.println("VICTORY LOGIC");
            return;
        }
        if (activeRow == NUMBER_OF_ROWS) {
            System.out.println("LOSE LOGIC");
            activeRow = 0;
            return;
        }
    }
}
