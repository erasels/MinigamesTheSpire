package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;

public class ShellGame extends AbstractMinigame {

    private Shell shell1;
    private Shell shell2;
    private Shell shell3;

    private void onClick() {
        switch (phase) {
            case 1:
        }
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), (p) -> onClick());
        return bindings;
    }
}
