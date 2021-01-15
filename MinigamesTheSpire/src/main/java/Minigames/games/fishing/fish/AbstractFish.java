package Minigames.games.fishing.fish;

import Minigames.util.HelperClass;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public abstract class AbstractFish {
    public float hp, mHp;
    public float y, initialY;

    protected int nextBehavior = 0;
    //time taken so far
    protected float ttl;

    //Y = Y location to move to
    //X = time spent on the move
    protected ArrayList<Vector2> originBehavior;
    protected Vector2 currentBehavior;

    public AbstractFish(int hp, ArrayList<Vector2> ogBehavior) {
        mHp = this.hp = hp;
        originBehavior = ogBehavior;
        currentBehavior = originBehavior.get(nextBehavior).cpy();
        y = initialY = 0;
    }

    public void update(boolean inArea) {
        if(inArea) {
            hp -= HelperClass.getTime();
            if(isCaught()) {
                dispose();
                return;
            }
        }

        ttl += HelperClass.getTime();
        y = Interpolation.exp10In.apply(initialY, currentBehavior.y, ttl / currentBehavior.x);

        if(y == currentBehavior.y) {
            cycleBehavior();
        }
    }

    public boolean isCaught() {
        return hp <= 0;
    }

    public void dispose() {
        originBehavior = null;
        currentBehavior = null;
    }

    private void cycleBehavior() {
        initialY = y;
        ttl = 0;
        if(nextBehavior >= originBehavior.size() - 1) {
            nextBehavior = 0;
        } else {
            nextBehavior++;
        }
        currentBehavior = originBehavior.get(nextBehavior);
    }

    public static AbstractFish returnRandomFish() {
        return null;
    }
}
