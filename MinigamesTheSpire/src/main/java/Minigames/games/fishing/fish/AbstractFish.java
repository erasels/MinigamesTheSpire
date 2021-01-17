package Minigames.games.fishing.fish;

import Minigames.util.HelperClass;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

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
    protected boolean shuffleWhenCycled;

    public AbstractFish(float hp, ArrayList<Vector2> ogBehavior, boolean shuffleWhenCycled) {
        mHp = this.hp = hp;
        this.shuffleWhenCycled = shuffleWhenCycled;
        originBehavior = ogBehavior.stream().map(Vector2::cpy).collect(Collectors.toCollection(ArrayList::new));
        currentBehavior = originBehavior.get(nextBehavior);
        y = initialY = 0;
    }

    public AbstractFish(float hp, ArrayList<Vector2> ogBehavior) {
        this(hp, ogBehavior, false);
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
            if(shuffleWhenCycled)
                Collections.shuffle(originBehavior);
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

    public boolean canSpawn() {
        return true;
    }

    public static AbstractFish returnRandomFish() {
        ArrayList<AbstractFish> fishies = new ArrayList<>(Arrays.asList(
                new CeramicFish(),
                new GoldFish()
                ));

        fishies.removeIf(f -> !f.canSpawn());
        AbstractFish f = HelperClass.getRandomItem(fishies, AbstractDungeon.miscRng);

        return f;
    }
}
