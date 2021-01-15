package Minigames.patches;

import Minigames.games.input.BoundInputProcessor;
import Minigames.games.input.bindings.BindingGroup;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor;
import javassist.CtBehavior;

public class Input {
    public static BoundInputProcessor processor;

    public static void update(float elapsed)
    {
        processor.update(elapsed);
    }

    public static void setBindings(BindingGroup bindings)
    {
        bindings.createInputMap();
        processor.bind(bindings);
    }

    public static void clearBindings()
    {
        processor.unbind();
    }

    @SpirePatch(
            clz = InputHelper.class,
            method = "initialize"
    )
    public static class useAlternateProcessor {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = { "processor" }
        )
        public static void replaceProcessor(@ByRef ScrollInputProcessor[] inputProcessor)
        {
            inputProcessor[0] = processor = new BoundInputProcessor();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(com.badlogic.gdx.Input.class, "setInputProcessor");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
