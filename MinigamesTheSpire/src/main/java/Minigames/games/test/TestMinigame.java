package Minigames.games.test;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.mastermind.MastermindMinigame;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import static Minigames.Minigames.makeGamePath;

//A simple minigame with a circle that you control to pick up a dot.
//Valid input is arrow keys or wasd, or you can click a point to move to it.

//Don't put everything in one class, this is just for demonstration.

//Pretty much all features are optional, the only part that I would strongly recommend using is the input stuff.
//If you wanna completely change how it's rendered/is initialized, feel free
//If you don't wanna put in the effort of managing the textures, just use the TextureLoader
//(If you have a lot or really big textures, please do manage them.)

public class TestMinigame extends AbstractMinigame {
    private Player player;
    private Target target;

    private int score;

    public TestMinigame() {
        super();

        hasInstructionScreen = true;
        hasPostgameScreen = true;
    }

    @Override
    public void initialize() {
        super.initialize();

        player = new Player(this);
        target = new Target(this);
        score = 0;
    }

    @Override
    public String getOption() {
        return "THIS IS THE TEST GAME";
    }

    @Override
    public void setupInstructionScreen(GenericEventDialog event) {
        event.updateBodyText("Click or use wasd/the arrow keys to collect #y~stars!~ Collect 5 #ystars to win!");
        event.setDialogOption("Get started!");
    }
    @Override
    public boolean instructionsButtonPressed(int buttonIndex) {
        return true;
    }

    @Override
    public void setupPostgameScreen(GenericEventDialog event) {
        event.updateBodyText("Good job! I would have been really impressed if you failed a game with no fail state!");
        event.setDialogOption("There is no reward!");
    }
    @Override
    public boolean postgameButtonPressed(int buttonIndex) {
        return super.postgameButtonPressed(buttonIndex);
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase)
        {
            case 0:
                player.update(elapsed);

                if (player.hb.intersects(target.hb)) {
                    target.randomizePosition();
                    ++score;

                    if (score >= 5)
                    {
                        phase = 1;
                    }
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
        if (phase <= 0)
        {
            player.render(sb);
            target.render(sb);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        player.dispose();
        target.dispose();
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.bindDirectional(()->player.setDirection(Player.DIRECTION.UP), ()->player.stopDirection(Player.DIRECTION.UP),
                ()->player.setDirection(Player.DIRECTION.DOWN), ()->player.stopDirection(Player.DIRECTION.DOWN),
                ()->player.setDirection(Player.DIRECTION.LEFT), ()->player.stopDirection(Player.DIRECTION.LEFT),
                ()->player.setDirection(Player.DIRECTION.RIGHT), ()->player.stopDirection(Player.DIRECTION.RIGHT));

        bindings.addMouseBind((x, y, pointer)->this.isWithinArea(x, y) && pointer == 0, (p)->player.setTargetPoint(p));

        return bindings;
    }

    private static class Player {
        private static final float BASE_SPEED = 100; //This does not have to be scaled, as
        private static final float DIAGONAL_RATE = (float) (1 / Math.sqrt(2));
        private static final int SIZE = 32;

        private final TestMinigame parent;

        private Vector2 targetPoint;
        private Vector2 position;

        private Texture t;
        private Color c;

        public Hitbox hb;

        public Player(TestMinigame parent) {
            this.parent = parent;
            position = new Vector2(0, 0);
            targetPoint = null;

            t = ImageMaster.loadImage(makeGamePath("testgame/player.png"));
            c = Color.CYAN.cpy();

            hb = new Hitbox(SIZE, SIZE);
            hb.move(position.x, position.y); //hitbox constructor is based on bottom left, but this method utilizes center
        }

        private enum DIRECTION {
            UP,
            DOWN,
            LEFT,
            RIGHT
        }

        private boolean left = false, right = false, up = false, down = false;

        public void update(float elapsed)
        {
            float travelDistance = BASE_SPEED * elapsed;

            if (targetPoint != null)
            {
                float distance = position.dst(targetPoint);

                if (distance < travelDistance)
                {
                    position.set(targetPoint);
                    targetPoint = null;
                }
                else
                {
                    position.add(targetPoint.cpy().sub(position).nor().scl(travelDistance));
                }
            }
            else
            {
                if ((left || right) && (up || down)) {
                    //traveling diagonal, reduce distance
                    travelDistance *= DIAGONAL_RATE;
                }

                if (left)
                    position.x -= travelDistance;
                else if (right)
                    position.x += travelDistance;

                if (up)
                    position.y += travelDistance;
                else if (down)
                    position.y -= travelDistance;
            }
            hb.move(position.x, position.y);
        }

        public void render(SpriteBatch sb)
        {
            sb.setColor(c);
            parent.drawTexture(sb, t, position.x, position.y, SIZE);
        }

        public void setTargetPoint(Vector2 targetPoint)
        {
            this.targetPoint = parent.getRelativeVector(targetPoint);
        }
        public void setDirection(DIRECTION direction)
        {
            targetPoint = null; //inputting a direction clears mouse input
            switch (direction) {
                case UP:
                    up = true;
                    down = false;
                    break;
                case DOWN:
                    down = true;
                    up = false;
                    break;
                case LEFT:
                    left = true;
                    right = false;
                    break;
                case RIGHT:
                    right = true;
                    left = false;
                    break;
            }
        }
        public void stopDirection(DIRECTION direction)
        {
            switch (direction) {
                case UP:
                    up = false;
                    break;
                case DOWN:
                    down = false;
                    break;
                case LEFT:
                    left = false;
                    break;
                case RIGHT:
                    right = false;
                    break;
            }
        }

        public void dispose() {
            t.dispose();
        }
    }

    private static class Target {
        private static final int SIZE = 26;

        private final TestMinigame parent;

        private Vector2 position;

        private Texture t;
        private Color c;

        public Hitbox hb;

        public Target(TestMinigame parent) {
            this.parent = parent;
            position = new Vector2(0, 0);

            t = ImageMaster.loadImage(makeGamePath("testgame/star.png"));
            c = Color.GOLD.cpy();

            hb = new Hitbox(SIZE, SIZE);
            randomizePosition();
        }

        public void randomizePosition() {
            position.x = MathUtils.random(-AbstractMinigame.SIZE / 2.2f, AbstractMinigame.SIZE / 2.2f);
            position.y = MathUtils.random(-AbstractMinigame.SIZE / 2.2f, AbstractMinigame.SIZE / 2.2f);

            hb.move(position.x, position.y); //hitbox constructor is based on bottom left, but this method utilizes center
        }

        public void render(SpriteBatch sb)
        {
            sb.setColor(c);
            parent.drawTexture(sb, t, position.x, position.y, SIZE);
        }

        public void dispose() {
            t.dispose();
        }
    }

    public AbstractMinigame makeCopy(){ return new TestMinigame(); }
}
