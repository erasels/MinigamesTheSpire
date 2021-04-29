package Minigames.games.slimePopper;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Consumer;

public class SlimeSpawnTimer {

    public final TIMER_TYPE type;
    private float delay;
    private final Consumer<PopperItem> addToGameFn;
    private float elapsed;
    private int spawnCount;
    public boolean isDone = false;

    private static float defaultDelay(TIMER_TYPE type) {
        if (type == TIMER_TYPE.MED_SLIME) {
            return 15f;
        } else if (type == TIMER_TYPE.BOSS) {
            return 45f;
        } else if (type == TIMER_TYPE.POTION) {
            return MathUtils.random(12f, 18f);
        } else if (type == TIMER_TYPE.SLIME) {
            return MathUtils.random(1f, 2f);
        }
        return 1f;
    }

    public SlimeSpawnTimer(TIMER_TYPE type, float delay, Consumer<PopperItem> addToGameFn) {
        this.type = type;
        this.delay = delay;
        this.addToGameFn = addToGameFn;
        elapsed = 0f;
        switch (type) {
            case SLIME:
                spawnCount = 5;
                break;
            case MED_SLIME:
                spawnCount = 2;
                break;
            case BOSS:
                spawnCount = 1;
                break;
            case POTION:
                spawnCount = AbstractDungeon.ascensionLevel < 15 ? 2 : 1;
                break;
            default:
                spawnCount = 0;
        }
    }

    public SlimeSpawnTimer(TIMER_TYPE type, Consumer<PopperItem> addToGameFn) {
        this(type, defaultDelay(type), addToGameFn);
    }

    private void spawn(PopperItem.TYPE type) {
        PopperItem newItem;
        switch (type) {
            case SLIME:
                newItem = new PopperSlime();
                newItem.yVelocity = -50f;
                newItem.xVelocity = MathUtils.random(-40f, 40f);
                break;
            case BOSS:
                newItem = new PopperBoss();
                newItem.yVelocity = -10f;
                newItem.xVelocity = 50f * MathUtils.randomSign();
                break;
            case MED:
                newItem = new PopperMed();
                newItem.yVelocity = -10f;
                newItem.xVelocity = 50f * MathUtils.randomSign();
                break;
            case POTION:
                newItem = new PopperPotion();
                newItem.yVelocity = -120;
                newItem.xVelocity = MathUtils.random(-80f, 80f);
                break;
            default:
                newItem = null;
                break;
        }
        if (newItem != null) {
            addToGameFn.accept(newItem);
        }
    }

    public void update(float dt) {
        elapsed += dt;
        if (elapsed > delay) {
            switch (type) {
                case SLIME:
                    spawn(PopperItem.TYPE.SLIME);
                    break;
                case MED_SLIME:
                    spawn(PopperItem.TYPE.MED);
                    break;
                case BOSS:
                    spawn(PopperItem.TYPE.BOSS);
                    break;
                case POTION:
                    spawn(PopperItem.TYPE.POTION);
                    break;
            }
            spawnCount -= 1;
            if (spawnCount > 0) {
                delay = defaultDelay(type);
                elapsed = 0f;
            } else {
                isDone = true;
            }
        }
    }

    public enum TIMER_TYPE {
        SLIME,
        MED_SLIME,
        BOSS,
        POTION
    }
}
