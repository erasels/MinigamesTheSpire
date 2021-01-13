package Minigames.games.input;


import Minigames.games.input.bindings.BindingGroup;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;

public class BoundInputProcessor extends ScrollInputProcessor {
    private static final BindingGroup emptyBinding = new BindingGroup();

    protected BindingGroup bindings = emptyBinding;

    public BoundInputProcessor()
    {
        super();
    }

    public void update(float elapsed)
    {
        bindings.update(elapsed);
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean base = super.keyDown(keycode);
        return bindings.receiveKeyDown(keycode) || base;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean base = super.keyUp(keycode);
        return bindings.receiveKeyUp(keycode) || base;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (button != 0 && button != 1)
            return false; //i only care about left and right click.

        return bindings.receiveTouchDown(screenX, screenY, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);

        if (button != 0 && button != 1)
            return false;

        return bindings.receiveTouchUp(screenX, screenY, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        super.touchDragged(screenX, screenY, pointer);

        return bindings.receiveTouchDragged(screenX, screenY);
    }

    public void bind(BindingGroup bindings) {
        this.bindings = bindings;
        bindings.clearInput();
    }

    public void unbind() {
        this.bindings = emptyBinding;
    }

    public void clearInput()
    {
        bindings.clearInput();
    }
}