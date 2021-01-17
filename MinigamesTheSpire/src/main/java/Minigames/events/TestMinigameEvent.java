package Minigames.events;

import Minigames.games.AbstractMinigame;
import Minigames.games.beatpress.BeatPress;
import Minigames.games.blackjack.BlackjackMinigame;
import Minigames.games.gremlinFlip.gremlinFlip;
import Minigames.games.mastermind.MastermindMinigame;
import Minigames.games.test.TestMinigame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;

import static Minigames.Minigames.makeID;
import static Minigames.Minigames.srcMinigameList;

public class TestMinigameEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("Test");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private int chosenMinigame;
    private ArrayList<AbstractMinigame> minigames = new ArrayList<>();

    public TestMinigameEvent() {
        super(NAME, DESCRIPTIONS[0], null);

        // add All Minigames, regardless of condition (Used for testing!)
        for(AbstractMinigame m : srcMinigameList){
            minigames.add(m.makeCopy());
            imageEventText.setDialogOption(m.getOption());
        }
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
                if (minigames.get(chosenMinigame).hasInstructionScreen)
                {
                    screenNum = 2;

                    this.imageEventText.clearAllDialogs();

                    minigames.get(chosenMinigame).setupInstructionScreen(this.imageEventText);
                }
                else
                {
                    startGame(minigames.get(chosenMinigame));
                }
                break;
            case 2:
                if (minigames.get(chosenMinigame).instructionsButtonPressed(buttonPressed, this.imageEventText)) {
                    startGame(minigames.get(chosenMinigame));
                }
                break;
            case 3:
                if (minigames.get(chosenMinigame).postgameButtonPressed(buttonPressed, this.imageEventText)) {
                    endOfEvent();
                }
                break;
            default:
                openMap();
                break;
        }
    }

    @Override
    public void finishGame() {
        super.finishGame();

        if (minigames.get(chosenMinigame).hasPostgameScreen) {
            screenNum = 3;

            this.imageEventText.clearAllDialogs();

            minigames.get(chosenMinigame).setupPostgameScreen(this.imageEventText);
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
