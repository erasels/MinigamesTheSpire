package Minigames.games.fishing.fish;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;

public class CeramicFish extends AbstractFish{
    public static final float MAX_HP = 0.55f;
    private static final ArrayList<Vector2> BEHAVIORS = new ArrayList<>(Arrays.asList(
            new Vector2(5f, 0.3f),
            new Vector2(3f, 1f),
            new Vector2(1f, 0.9f), //Fake drops
            new Vector2(0.5f, 1f),
            new Vector2(1f, 0.9f),
            new Vector2(0.5f, 1f),
            new Vector2(1f, 0.9f),
            new Vector2(0.5f, 1f),
            new Vector2(2.5f, 0.15f), //Meteor drop
            new Vector2(8f, 0f)
            ));

    public CeramicFish() {
        super(MAX_HP, BEHAVIORS);
    }

    @Override
    public ArrayList<RewardItem> returnReward() {
        return new ArrayList<>(Arrays.asList(new RewardItem(new com.megacrit.cardcrawl.relics.CeramicFish())));
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(com.megacrit.cardcrawl.relics.CeramicFish.ID);
    }
}
