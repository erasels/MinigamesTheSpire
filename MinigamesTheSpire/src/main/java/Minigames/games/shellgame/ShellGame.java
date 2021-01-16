package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;

public class ShellGame extends AbstractMinigame {

    private Shell shell1;
    private Shell shell2;
    private Shell shell3;

    private int chosen;

    private void onClick() {
        switch (phase) {
            case 1:
                if (shell1.hb.hovered) {
                    chosen = 1;
                    phase = 2;
                }
                else if (shell2.hb.hovered) {
                    chosen = 2;
                    phase = 2;
                }
                else if (shell3.hb.hovered) {
                    chosen = 3;
                    phase = 2;
                }
        }
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), (p) -> onClick());
        return bindings;
    }
}
