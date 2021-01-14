package Minigames.games.fishing.fish;

public abstract class AbstractFish {
    public int hp, mHp;
    public AbstractFish(int hp) {
        mHp = this.hp = hp;
    }


    public static AbstractFish returnRandomFish() {
        return null;
    }

    public boolean isCaught() {
        return hp <= 0;
    }
}
