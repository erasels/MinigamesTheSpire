package Minigames.games.input.bindings;

import Minigames.util.TriFunction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class BindingGroup {
    //Outer key: Modifier key state (ctrl = 1, shift = 2, alt = 4)
    //Inner key: keycode
    private final HashMap<Integer, HashMap<Integer, InputBinding>> keyInputs = new HashMap<>(); //used to quickly translate key input to the correct bindings

    private final HashMap<String, InputBinding> allBindings = new HashMap<>();

    private final HashMap<Integer, InputBinding> heldKeyInputs = new HashMap<>(); //currently held keys : binding linked to key
    private final HashMap<InputBinding, Integer> activeBindings = new HashMap<>(); //currently active bindings : number of active inputs

    private final MouseHoldObject[] mouseHolds = new MouseHoldObject[2]; //left and right click
    private final ArrayList<MouseInputInfo> mouseInputs = new ArrayList<>();

    public void addBinding(InputBinding binding)
    {
        allBindings.put(binding.getInputID(), binding);
    }

    public void createInputMap()
    {
        keyInputs.clear();
        for (InputBinding binding : allBindings.values())
        {
            for (InputBinding.InputInfo i : binding.getInputs())
            {
                int modifiers = i.getModifiers();
                if (!keyInputs.containsKey(modifiers))
                    keyInputs.put(modifiers, new HashMap<>());

                keyInputs.get(modifiers).put(i.getCode(), binding);
            }
        }
    }

    public BindingGroup resetBindings()
    {
        for (InputBinding binding : allBindings.values())
        {
            binding.clearBinding();
        }
        return this;
    }

    public void bind(String bindingKey, BooleanSupplier onDown, KeyHoldObject hold, BooleanSupplier onRelease)
    {
        InputBinding binding = allBindings.get(bindingKey);

        binding.bind(onDown, hold, onRelease);
    }
    public void bind(String bindingKey, Runnable onDown, KeyHoldObject hold, Runnable onRelease)
    {
        bind(bindingKey,
        ()->{
            onDown.run();
            return true;
        }, hold,
        ()->{
            onRelease.run();
            return true;
        });
    }
    public void bind(String bindingKey, BooleanSupplier onDown, KeyHoldObject hold)
    {
        InputBinding binding = allBindings.get(bindingKey);

        binding.bind(onDown, hold);
    }
    public void bind(String bindingKey, Runnable onDown, KeyHoldObject hold)
    {
        bind(bindingKey, ()->{
            onDown.run();
            return true;
        }, hold);
    }
    public void bind(String bindingKey, BooleanSupplier onDown)
    {
        allBindings.get(bindingKey).bind(onDown);
    }
    public void bind(String bindingKey, Runnable onDown)
    {
        bind(bindingKey, ()->{
            onDown.run();
            return true;
        });
    }

    //Parameters of the function are: x, y, button (0 = left click, 1 = right click), return value of boolean for whether or not this click is valid
    public void addMouseBind(TriFunction<Integer, Integer, Integer, Boolean> isValidClick, Consumer<Vector2> onPress) {
        mouseInputs.add(new MouseInputInfo(isValidClick, onPress));
    }
    public void addMouseBind(TriFunction<Integer, Integer, Integer, Boolean> isValidClick, Consumer<Vector2> onPress, MouseHoldObject holdObject) {
        mouseInputs.add(new MouseInputInfo(isValidClick, onPress, holdObject));
    }

    //more "convenient" methods
    public void bindDirectional(Runnable up, Runnable stopUp, Runnable down, Runnable stopDown, Runnable left, Runnable stopLeft, Runnable right, Runnable stopRight) {
        addBinding(InputBinding.create("Up", new InputBinding.InputInfo(Input.Keys.UP), new InputBinding.InputInfo(Input.Keys.W)).addConflicts("Down"));
        addBinding(InputBinding.create("Down", new InputBinding.InputInfo(Input.Keys.DOWN), new InputBinding.InputInfo(Input.Keys.S)).addConflicts("Up"));
        addBinding(InputBinding.create("Left", new InputBinding.InputInfo(Input.Keys.LEFT), new InputBinding.InputInfo(Input.Keys.A)).addConflicts("Right"));
        addBinding(InputBinding.create("Right", new InputBinding.InputInfo(Input.Keys.RIGHT), new InputBinding.InputInfo(Input.Keys.D)).addConflicts("Left"));

        bind("Up", up, null, stopUp);
        bind("Down", down, null, stopDown);
        bind("Left", left, null, stopLeft);
        bind("Right", right, null, stopRight);
    }


    public ArrayList<InputBinding.InputInfo> bindingInputs(String bindingKey)
    {
        return allBindings.get(bindingKey).getInputs();
    }

    public void clearInput() {
        heldKeyInputs.clear();
        activeBindings.clear();
        mouseHolds[0] = null;
        mouseHolds[1] = null;
    }

    public void update(float elapsed)
    {
        //Check currently held keys. If they are not held, release them.
        //This is due to keyUp events possibly being missed if something else consumes them, another layer is created, focus is lost, etc.
        //Many possibilities.

        Iterator<Map.Entry<Integer, InputBinding>> inputIterator = heldKeyInputs.entrySet().iterator();
        Map.Entry<Integer, InputBinding> next;
        int heldCount = 0;

        HashSet<InputBinding> stillHeldBindings = new HashSet<>();

        while (inputIterator.hasNext())
        {
            next = inputIterator.next();

            if (Gdx.input.isKeyPressed(next.getKey()))
            {
                if (next.getValue().hasHold() && !stillHeldBindings.contains(next.getValue())) {
                    next.getValue().getHold().update(elapsed);
                }
                stillHeldBindings.add(next.getValue());
            }
            else
            {
                heldCount = activeBindings.get(next.getValue()) - 1;
                if (heldCount == 0)
                {
                    if (next.getValue().hasRelease())
                    {
                        next.getValue().onRelease();
                    }

                    activeBindings.remove(next.getValue());
                }
                else
                {
                    activeBindings.put(next.getValue(), heldCount);
                }
                inputIterator.remove();
            }
        }

        if (mouseHolds[0] != null)
            mouseHolds[0].update(elapsed);

        if (mouseHolds[1] != null)
            mouseHolds[1].update(elapsed);
    }

    public boolean receiveKeyDown(int keycode)
    {
        HashMap<Integer, InputBinding> keyBindings = keyInputs.get(modifierState());

        if (keyBindings != null)
        {
            InputBinding binding = keyBindings.get(keycode);

            if (binding != null)
            {
                if (activeBindings.containsKey(binding))
                {
                    //already held down (probably using another supported key)
                    activeBindings.compute(binding, (b, x)->x == null ? 1 : x + 1);
                    heldKeyInputs.put(keycode, binding);
                    return true;
                }
                else
                {
                    boolean result = binding.onDown();

                    if (result)
                    {
                        if (binding.hasHold())
                        {
                            binding.getHold().reset();

                            for (String ID : binding.conflictingBindings) //remove conflicting held keys
                            {
                                InputBinding b = allBindings.get(ID);
                                if (b != null && activeBindings.containsKey(b))
                                {
                                    activeBindings.remove(b);

                                    for (InputBinding.InputInfo inputInfo : b.getInputs())
                                    {
                                        heldKeyInputs.remove(inputInfo.getCode());
                                    }

                                    if (b.hasRelease())
                                    {
                                        b.onRelease();
                                    }
                                }
                            }
                        }

                        activeBindings.put(binding, 1);
                        heldKeyInputs.put(keycode, binding);
                    }

                    return result;
                }
            }
        }

        return false;
    }
    public boolean receiveKeyUp(int keycode)
    {
        if (heldKeyInputs.containsKey(keycode))
        {
            InputBinding binding = heldKeyInputs.remove(keycode);

            int heldCount = activeBindings.get(binding) - 1;
            if (heldCount == 0)
            {
                if (binding.hasRelease())
                {
                    binding.onRelease();
                }

                activeBindings.remove(binding);
            }
            else
            {
                activeBindings.put(binding, heldCount);
            }
        }
        return false;
    }


    public boolean receiveTouchDown(int screenX, int screenY, int button) {
        int mX, mY;

        if (!Settings.isTouchScreen) {
            mX = screenX;
            if (mX > Settings.WIDTH) {
                mX = Settings.WIDTH;
            } else if (mX < 0) {
                mX = 0;
            }

            mY = Settings.HEIGHT - screenY;
            if (mY > Settings.HEIGHT) {
                mY = Settings.HEIGHT;
            } else if (mY < 1) {
                mY = 1;
            }
        } else {
            mX = screenX + Settings.VERT_LETTERBOX_AMT;
            mY = Settings.HEIGHT - screenY + Settings.HORIZ_LETTERBOX_AMT;
            if (mY < 1) {
                mY = 1;
            }
        }

        if (mouseHolds[button] != null) {
            mouseHolds[button].onRelease(mX, mY);
            mouseHolds[button] = null;
        }

        for (MouseInputInfo info : mouseInputs)
        {
            if (info.condition.apply(mX, mY, button)) {
                info.onPress.accept(new Vector2(mX, mY));
                mouseHolds[button] = info.holdObject;
                return true;
            }
        }

        return false;
    }

    public boolean receiveTouchUp(int screenX, int screenY, int button) {
        if (mouseHolds[button] != null)
        {
            int mX, mY;

            if (!Settings.isTouchScreen) {
                mX = screenX;
                if (mX > Settings.WIDTH) {
                    mX = Settings.WIDTH;
                } else if (mX < 0) {
                    mX = 0;
                }

                mY = Settings.HEIGHT - screenY;
                if (mY > Settings.HEIGHT) {
                    mY = Settings.HEIGHT;
                } else if (mY < 1) {
                    mY = 1;
                }
            } else {
                mX = screenX + Settings.VERT_LETTERBOX_AMT;
                mY = Settings.HEIGHT - screenY + Settings.HORIZ_LETTERBOX_AMT;
                if (mY < 1) {
                    mY = 1;
                }
            }

            boolean consumed = mouseHolds[button].onRelease(mX, mY);
            mouseHolds[button] = null;
            return consumed;
        }
        return false;
    }

    public boolean receiveTouchDragged(int screenX, int screenY) {
        boolean left = mouseHolds[0] != null, right = mouseHolds[1] != null;
        if (left || right)
        {
            int mX, mY;

            if (!Settings.isTouchScreen) {
                mX = screenX;
                if (mX > Settings.WIDTH) {
                    mX = Settings.WIDTH;
                } else if (mX < 0) {
                    mX = 0;
                }

                mY = Settings.HEIGHT - screenY;
                if (mY > Settings.HEIGHT) {
                    mY = Settings.HEIGHT;
                } else if (mY < 1) {
                    mY = 1;
                }
            } else {
                mX = screenX + Settings.VERT_LETTERBOX_AMT;
                mY = Settings.HEIGHT - screenY + Settings.HORIZ_LETTERBOX_AMT;
                if (mY < 1) {
                    mY = 1;
                }
            }

            if (left)
                mouseHolds[0].onDrag(mX, mY);

            if (right)
                mouseHolds[1].onDrag(mX, mY);
        }
        return false;
    }

    public static int modifierState()
    {
        return (ctrl() ? 1 : 0) |
                (shift() ? 2 : 0) |
                (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT) ? 4 : 0);
    }
    public static boolean ctrl()
    {
        return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
    }
    public static boolean shift()
    {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }
}
