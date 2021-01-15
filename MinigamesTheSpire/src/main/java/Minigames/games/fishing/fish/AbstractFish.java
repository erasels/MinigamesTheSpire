package Minigames.games.fishing.fish;

import Minigames.util.HelperClass;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public abstract class AbstractFish {
    public float hp, mHp;
    public float y;

    protected int nextBehavior = 0;

    //Y = Y location to move to
    //X = time spent on the move
    protected ArrayList<Vector2> originBehavior;
    protected Vector2 currentBehavior;

    public AbstractFish(int hp, ArrayList<Vector2> ogBehavior) {
        mHp = this.hp = hp;
        originBehavior = ogBehavior;
        currentBehavior = originBehavior.get(nextBehavior).cpy();
        y=0;
    }

    public void update(boolean inArea) {
        if(inArea) {
            hp -= HelperClass.getTime();
            if(isCaught()) {
                dispose();
                return;
            }
        }

        //Iffy on using y as start since I'll modify y, maybe leave it as 0 and just add/subtract from y instead?
        y = Interpolation.exp10In.apply(y, currentBehavior.y, (/*Some mathy shit that goes from 0 to 1*/currentBehavior.x));
        currentBehavior.x -= HelperClass.getTime();

        if(y == currentBehavior.y) {
            currentBehavior = getNextBehavior();
        }
    }

    public boolean isCaught() {
        return hp <= 0;
    }

    public void dispose() {
        originBehavior = null;
        currentBehavior = null;
    }

    private Vector2 getNextBehavior() {
        if(nextBehavior >= originBehavior.size() - 1) {
            nextBehavior = 0;
        } else {
            nextBehavior++;
        }
        return originBehavior.get(nextBehavior);
    }

    public static AbstractFish returnRandomFish() {
        return null;
    }
}
