package Minigames.games.fishing.fish;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.FossilizedHelix;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;

public class FossilFish extends AbstractFish{
    public static final float MAX_HP = 0.4f;
    private static final ArrayList<Vector2> BEHAVIORS = new ArrayList<>(Arrays.asList(
            new Vector2(2.5f, 0.5f),
            new Vector2(1f, 0.6f),
            new Vector2(1.5f, 0.4f),
            new Vector2(2f, 0.7f),
            new Vector2(2f, 0.3f),
            new Vector2(1.5f, 0.8f),
            new Vector2(2.5f, 0.2f),
            new Vector2(3f, 0.9f)
    ));

    public FossilFish() {
        super(MAX_HP, BEHAVIORS, true);
    }

    @Override
    public ArrayList<RewardItem> returnReward() {
        return new ArrayList<>(Arrays.asList(new RewardItem(new FossilizedHelix())));
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(FossilizedHelix.ID) && AbstractDungeon.actNum > 1;
    }
}
