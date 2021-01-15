package Minigames.games.fishing;

import Minigames.games.AbstractMinigame;
import Minigames.games.fishing.fish.AbstractFish;
import Minigames.games.fishing.phases.AbstractGamePhase;
import Minigames.games.fishing.phases.CatchPhase;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.MouseHoldObject;
import Minigames.util.HelperClass;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FishingGame extends AbstractMinigame {
    private static float WAITTIME = 1f;

    public AbstractGamePhase gamePhase;
    public AbstractFish fish;

    private int score;
    private float waitTimer = WAITTIME;


    public FishingGame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();

        fish = AbstractFish.returnRandomFish();

        gamePhase = new CatchPhase(this, null);
        gamePhase.initialize();
        score = 0;
    }

    private void doAction(Vector2 vec) {
        if(gamePhase != null)
            gamePhase.action();
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase) {
            case 0:
                if(gamePhase == null) {
                    phase++;
                } else {
                    gamePhase.update();
                }
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
        if(gamePhase != null)
            gamePhase.render(sb);
    }

    //To be called by AbstractGamePhase once task is finished
    public boolean waiting() {
        waitTimer -= HelperClass.getTime();
        if (waitTimer <= 0) {
            waitTimer = WAITTIME;
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        gamePhase.dispose();

        fish = null;
    }

    public void doDebugAction(Dir dir) {

    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> this.isWithinArea(x, y) && pointer == 0, this::doAction, new MouseHoldObject((x, y) -> doAction(new Vector2(x, y)), null));
        //Add more bindings which basically do the same thing, maybe space?

        //DEBUG Bindings
        //bindings.bindDirectional(() -> this.gamePhase.);

        return bindings;
    }

    public enum Dir {
        UP, DOWN, LEF, RIGHT
    }
}
