package Minigames.games.input.bindings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BooleanSupplier;

public class InputBinding {
    public ArrayList<String> conflictingBindings = new ArrayList<>();

    public static InputBinding create(String ID, InputInfo... input)
    {
        InputBinding binding = new InputBinding(ID);

        binding.inputs.addAll(Arrays.asList(input));
        binding.clearBinding();

        return binding;
    }

    public InputBinding addConflicts(String... conflicts)
    {
        Collections.addAll(conflictingBindings, conflicts);
        return this;
    }


    private final String inputID;

    private final ArrayList<InputInfo> inputs;

    private BooleanSupplier onDown;
    private boolean hasHold;
    private KeyHoldObject hold;
    private boolean hasRelease;
    private BooleanSupplier onRelease;

    private InputBinding(String ID) {
        this.inputID = ID;
        this.inputs = new ArrayList<>();
    }

    public String getInputID() {
        return inputID;
    }

    public ArrayList<InputInfo> getInputs() {
        return inputs;
    }

    public void clearBinding() {
        onRelease = null;
        onDown = null;
        hold = null;

        hasRelease = false;
        hasHold = false;
    }

    public boolean onDown()
    {
        if (hasHold)
            hold.reset();

        return onDown.getAsBoolean();
    }
    public boolean onRelease()
    {
        if (hasRelease)
            return onRelease.getAsBoolean();

        return false;
    }
    public boolean hasRelease()
    {
        return hasRelease;
    }
    public boolean hasHold()
    {
        return hasHold;
    }
    public KeyHoldObject getHold()
    {
        return hold;
    }

    public void bind(BooleanSupplier onDown) {
        bind(onDown, null, null);
    }
    public void bind(BooleanSupplier onDown, KeyHoldObject hold) {
        bind(onDown, hold, null);
    }
    public void bind(BooleanSupplier onDown, BooleanSupplier onRelease) {
        bind(onDown, null, onRelease);
    }
    public void bind(BooleanSupplier onDown, KeyHoldObject hold, BooleanSupplier onRelease) {
        if (onDown != null)
        {
            this.onDown = onDown;
        }
        else
        {
            this.onDown = ()->true;
        }

        this.hold = hold;
        this.hasHold = hold != null;

        this.onRelease = onRelease;
        this.hasRelease = onRelease != null;
    }


    public static class InputInfo {
        public int code;
        public boolean ctrl;
        public boolean shift;
        public boolean alt;

        public InputInfo(int code, boolean ctrl, boolean shift, boolean alt)
        {
            this.code = code;
            this.ctrl = ctrl;
            this.shift = shift;
            this.alt = alt;
        }

        public InputInfo(int code, boolean ctrl)
        {
            this(code, ctrl, false, false);
        }

        public InputInfo(int code)
        {
            this(code, false, false, false);
        }

        public int getCode()
        {
            return code;
        }
        public int getModifiers()
        {
            return (ctrl ? 1 : 0) | (shift ? 2 : 0) | (alt ? 4 : 0);
        }
    }
}
