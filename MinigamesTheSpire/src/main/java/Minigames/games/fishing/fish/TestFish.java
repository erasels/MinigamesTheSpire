package Minigames.games.fishing.fish;

import Minigames.Minigames;
import Minigames.util.TextureLoader;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;
import java.util.Arrays;

public class TestFish extends AbstractFish {
    public static final float MAX_HP = 0.5f;
    private static final ArrayList<Vector2> BEHAVIORS = new ArrayList<>(Arrays.asList(new Vector2(10f, 0), new Vector2(2f, 1f)));
    public TestFish() {
        super(MAX_HP, BEHAVIORS);
    }

    @Override
    public ArrayList<RewardItem> returnReward() {
        return new ArrayList<>(Arrays.asList(new RewardItem(75)));
    }

    @Override
    protected void initImage() {
        img = TextureLoader.getTexture(Minigames.makeGamePath("Fishing/MagicFish.png"));
    }
}
