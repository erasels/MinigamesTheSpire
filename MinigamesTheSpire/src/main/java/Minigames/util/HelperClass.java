package Minigames.util;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.random.Random;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HelperClass {
    private static DecimalFormat twoDecFormat = new DecimalFormat("#0.00");

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String capitalize(String str, String match) {
        return str.replace(match, capitalize(match));
    }

    public static <T> T getRandomItem(ArrayList<T> list, Random rng) {
        return list.isEmpty() ? null : list.get(rng.random(list.size() - 1));
    }

    //SuperFastMode compatability
    public static float getTime() {
        return Gdx.graphics.getRawDeltaTime();
    }

    public static String get2DecString(float num) {
        if (num < 0) {
            num = 0;
        }
        BigDecimal bd = new BigDecimal(Float.toString(num));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return twoDecFormat.format(bd.floatValue());
    }
}
