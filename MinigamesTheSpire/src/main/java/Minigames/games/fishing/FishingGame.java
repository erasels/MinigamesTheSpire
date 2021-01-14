package Minigames.games.fishing;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.fishing.fish.AbstractFish;
import Minigames.games.fishing.phases.AbstractGamePhase;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.util.HelperClass;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.CeramicFish;

public class FishingGame extends AbstractMinigame {
    private static float WAITTIME = 1f;
    private static Texture imgBar;
    private static Texture imgSpinner;
    private static Texture imgCatcher;
    private static Texture imgFish; //Could be replaced with custom picture of fish

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

        imgBar = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishingBar.png"));
        imgSpinner = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/SpinnyThing.png"));
        imgCatcher = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/FishCatcher.png"));
        imgFish = ImageMaster.getRelicImg(CeramicFish.ID);

        fish = AbstractFish.returnRandomFish();

        //gamePhase = new CatchPhase(this);
        score = 0;
    }

    private void doAction(Vector2 vec) {
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
            gamePhase.render();
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

        imgSpinner.dispose();
        imgSpinner = null;
        imgBar.dispose();
        imgBar = null;
        imgCatcher.dispose();
        imgCatcher = null;

        fish = null;
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> this.isWithinArea(x, y) && pointer == 0, this::doAction);
        //Add more bindings which basically do the same thing, maybe space?

        return bindings;
    }
}
