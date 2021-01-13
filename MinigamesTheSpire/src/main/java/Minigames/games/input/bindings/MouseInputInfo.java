package Minigames.games.input.bindings;

import Minigames.util.TriFunction;
import com.badlogic.gdx.math.Vector2;

import java.util.function.Consumer;

class MouseInputInfo {
    public TriFunction<Integer, Integer, Integer, Boolean> condition;
    public Consumer<Vector2> onPress;
    public MouseHoldObject holdObject;

    public MouseInputInfo(TriFunction<Integer, Integer, Integer, Boolean> isValidClick, Consumer<Vector2> onPress) {
        this(isValidClick, onPress, null);
    }

    public MouseInputInfo(TriFunction<Integer, Integer, Integer, Boolean> isValidClick, Consumer<Vector2> onPress, MouseHoldObject holdObject) {
        this.condition = isValidClick;
        this.onPress = onPress;
        this.holdObject = holdObject;
    }
}
