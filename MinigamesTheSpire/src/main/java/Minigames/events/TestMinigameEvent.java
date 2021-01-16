package Minigames.events;

import Minigames.games.AbstractMinigame;
import Minigames.games.beatpress.BeatPress;
import Minigames.games.gremlinFlip.gremlinFlip;
import Minigames.games.test.TestMinigame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;

public class TestMinigameEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("Test");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private AbstractMinigame[] minigames;
    private int chosenMinigame;

    public TestMinigameEvent() {
        super(NAME, DESCRIPTIONS[0], null);

        minigames = new AbstractMinigame[3];
        minigames[0] = new TestMinigame();
        minigames[1] = new BeatPress();
        minigames[2] = new gremlinFlip();

        imageEventText.setDialogOption(minigames[0].getOption());
        imageEventText.setDialogOption(minigames[1].getOption());
        imageEventText.setDialogOption(minigames[2].getOption());
    }

    @Override
    protected void buttonEffect(int buttonPressed)
    {
        switch (screenNum) {
            case 0:
                //use earlier cases to do the event's flavor stuff
            case 1:
                //screen with choice
                chosenMinigame = buttonPressed;
                if (minigames[chosenMinigame].hasInstructionScreen)
                {
                    screenNum = 2;

                    this.imageEventText.clearAllDialogs();

                    minigames[chosenMinigame].setupInstructionScreen(this.imageEventText);
                }
                else
                {
                    startGame(minigames[chosenMinigame]);
                }
                break;
            case 2:
                if (minigames[chosenMinigame].instructionsButtonPressed(buttonPressed, this.imageEventText)) {
                    startGame(minigames[chosenMinigame]);
                }
                break;
            case 3:
                if (minigames[chosenMinigame].postgameButtonPressed(buttonPressed, this.imageEventText)) {
                    endOfEvent();
                }
                break;
            case 4:

                break;
        }
    }

    @Override
    public void finishGame() {
        super.finishGame();

        if (minigames[chosenMinigame].hasPostgameScreen) {
            screenNum = 3;

            this.imageEventText.clearAllDialogs();

            minigames[chosenMinigame].setupPostgameScreen(this.imageEventText);
        }
        else
        {
            endOfEvent();
        }
    }

    @Override
    public void endOfEvent() {
        this.imageEventText.clearAllDialogs();

        this.imageEventText.updateBodyText("hmmmm");
        this.imageEventText.setDialogOption("I guess it's over?");

        screenNum = 4;
    }
}
