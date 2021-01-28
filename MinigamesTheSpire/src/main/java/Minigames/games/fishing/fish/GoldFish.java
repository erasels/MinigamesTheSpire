package Minigames.games.fishing.fish;

import Minigames.Minigames;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;

public class GoldFish extends AbstractFish{
    public static final float MAX_HP = 0.25f;
    private static final ArrayList<Vector2> BEHAVIORS = new ArrayList<>(Arrays.asList(
            new Vector2(3.5f, 1f),
            new Vector2(4.5f, 0f),
            new Vector2(2f, 0.5f),
            new Vector2(1.5f, 0.7f),
            new Vector2(2.5f, 0.3f),
            new Vector2(1f, 0.5f),
            new Vector2(1.75f, 0.9f),
            new Vector2(3f, 0.15f)
    ));

    public GoldFish() {
        super(MAX_HP, BEHAVIORS, true);
    }

    @Override
    public ArrayList<RewardItem> returnReward() {
        return new ArrayList<>(Arrays.asList(new RewardItem(80)));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum < 3;
    }

    @Override
    protected void initImage() {
        img = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/GoldFish.png"));
    }
}
