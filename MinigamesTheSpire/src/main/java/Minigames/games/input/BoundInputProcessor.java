package Minigames.games.input;


import Minigames.games.input.bindings.BindingGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;

import static Minigames.Minigames.logger;

public class BoundInputProcessor extends ScrollInputProcessor {
    private static final BindingGroup emptyBinding = new BindingGroup();

    protected BindingGroup bindings = emptyBinding;
    public BindingGroup inactiveBindings = null;

    public BoundInputProcessor()
    {
        super();
    }

    public void update(float elapsed)
    {
        if (CardCrawlGame.isPopupOpen || AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE) {
            deactivate();
        }
        else if (inactiveBindings != null)
        {
            bindings = inactiveBindings;
            inactiveBindings = null;
            logger.info("Bindings reactivated.");
        }
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
        if (inactiveBindings != null)
        {
            this.inactiveBindings = bindings;
        }
        else
        {
            this.bindings = bindings;
        }
        bindings.clearInput();
    }

    public void unbind() {
        this.bindings = emptyBinding;
        this.inactiveBindings = null;
    }

    public void deactivate() {
        if (bindings != emptyBinding)
        {
            logger.info("Bindings deactivated. A popup or screen is open.");
            inactiveBindings = bindings;
            bindings = emptyBinding;
        }
    }

    public void clearInput()
    {
        bindings.clearInput();
        if (inactiveBindings != null)
            inactiveBindings.clearInput();
    }
}