package Minigames.games.fishing.fish;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Sozu;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;

public class PotionFish extends AbstractFish{
    public static final float MAX_HP = 0.6f;
    private static final ArrayList<Vector2> BEHAVIORS = new ArrayList<>(Arrays.asList(
            new Vector2(10f, 1f),
            new Vector2(1.5f, 0.7f),
            new Vector2(1.5f, 1f),
            new Vector2(1.8f, 0.6f),
            new Vector2(1.5f, 1f),
            new Vector2(2.1f, 0.5f),
            new Vector2(5f, 0.2f),
            new Vector2(5f, 0.2f),
            new Vector2(4f, 0f)
    ));

    public PotionFish() {
        super(MAX_HP, BEHAVIORS);
    }

    @Override
    public ArrayList<RewardItem> returnReward() {
        return new ArrayList<>(Arrays.asList(new RewardItem(AbstractDungeon.returnRandomPotion())));
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(Sozu.ID);
    }
}
