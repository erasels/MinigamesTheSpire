package Minigames.events;

import Minigames.games.shellgame.ShellGame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;

public class ShellGameEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("ShellGame");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;


    public ShellGameEvent() {
//        super(NAME, DESCRIPTIONS[0], null);
        super("Slide the Spire", "slide the thingies for the thingy to solve the thingy", null);

//        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption("Play!");
    }

    @Override
    protected void buttonEffect(int buttonPressed)
    {
        switch (screenNum) {
            default:
                startGame(new ShellGame());
                break;
        }
    }
}