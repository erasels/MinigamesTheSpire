package Minigames.games.fishing.fish;

import Minigames.util.HelperClass;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;

public abstract class AbstractFish {
    //How long the player has to be catching the fish (percentage of total game time)
    public float hp, mHp;
    public float y, initialY;

    protected int nextBehavior = 0;
    //time taken so far
    protected float ttl;

    //Y = Y location to move to (percentage of total area)
    //X = time spent on the move
    protected ArrayList<Vector2> originBehavior;
    protected Vector2 currentBehavior;

    public AbstractFish(float hp, ArrayList<Vector2> ogBehavior) {
        mHp = this.hp = hp;
        originBehavior = ogBehavior;
        currentBehavior = originBehavior.get(nextBehavior);
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
        y = Interpolation.smoother.apply(initialY, currentBehavior.y, ttl / currentBehavior.x);
        //System.out.printf("Interpolation.smoother.apply(%f, %f, %f / %f) = %f%n", initialY, currentBehavior.y, ttl, currentBehavior.x, y);

        //If fish move has been finished
        if(ttl >= currentBehavior.x) {
            cycleBehavior();
        }
    }

    public boolean isCaught() {
        return hp <= 0;
    }

    public abstract ArrayList<RewardItem> returnReward();

    public void dispose() { }

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

    public boolean isWithinY(float y1, float y2) {
        return y >= y1 && y <= y2;
    }

    public void scaleBehavior(float maxGameTime, float maxPos) {
        hp = mHp = mHp * maxGameTime;
        for(Vector2 vec : originBehavior) {
            vec.y *= maxPos;
        }
    }

    public static AbstractFish returnRandomFish() {
        return new TestFish();
    }
}
