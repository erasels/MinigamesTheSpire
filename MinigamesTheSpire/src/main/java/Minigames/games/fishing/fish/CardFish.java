package Minigames.games.fishing.fish;

import Minigames.Minigames;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;

public class CardFish extends AbstractFish{
    public static final float MAX_HP = 0.33f;
    private static final ArrayList<Vector2> BEHAVIORS = new ArrayList<>(Arrays.asList(
            new Vector2(4f, 1f),
            new Vector2(4f, 0f),
            new Vector2(2f, 0.5f),
            new Vector2(0.5f, 0.65f),
            new Vector2(0.5f, 0.8f),
            new Vector2(0.5f, 0.65f),
            new Vector2(0.5f, 0.5f),
            new Vector2(0.5f, 0.35f),
            new Vector2(5f, 0.9f)
    ));

    public CardFish() {
        super(MAX_HP, BEHAVIORS, true);
    }

    @Override
    public ArrayList<RewardItem> returnReward() {
        return new ArrayList<>(Arrays.asList(new RewardItem(AbstractDungeon.player.getCardColor())));
    }

    @Override
    protected void initImage() {
        img = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/CardFish.png"));
    }
}
