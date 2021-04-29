package Minigames.games.slimePopper;

import com.megacrit.cardcrawl.core.CardCrawlGame;

public class PopperPotion extends PopperItem {

    public PopperPotion() {
        super(TYPE.POTION, "potion");
        priority = 6;
    }

    @Override
    public void startDeath() {
        isDying = true;
        setAnimation("potionGet");
        CardCrawlGame.sound.play("POTION_DROP_2");
    }
}
