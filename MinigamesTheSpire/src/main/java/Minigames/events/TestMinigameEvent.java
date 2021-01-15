package Minigames.events;

import Minigames.games.mastermind.MastermindMinigame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;

public class TestMinigameEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("Test");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;


    public TestMinigameEvent() {
        super(NAME, DESCRIPTIONS[0], null);

        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed)
    {
        switch (screenNum) {
            default:
                startGame(new MastermindMinigame());
                break;
        }
    }
}
