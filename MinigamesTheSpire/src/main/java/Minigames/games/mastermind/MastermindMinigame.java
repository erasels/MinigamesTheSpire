package Minigames.games.mastermind;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MastermindMinigame extends AbstractMinigame {

    public static final int POSSIBLE_COLORS = 6;

    private MarbleBoard marbleBoard;
    private MarbleControllers marbleControllers;

    public MouseHoldObject mouseHoldObject;

    public MastermindMinigame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        marbleBoard = new MarbleBoard(this);
        marbleControllers = new MarbleControllers(this);
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

        bindings.addMouseBind((x, y, pointer) -> this.isWithinArea(x, y) && pointer == 0, pointer -> marbleControllers.onMouse(pointer), mouseHoldObject);

        return bindings;
    }


}
