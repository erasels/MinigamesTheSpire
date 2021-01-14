package Minigames.events;

import Minigames.games.blackjack.BlackjackMinigame;
import Minigames.games.test.TestMinigame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;

public class TestBlackjackEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("BlackjackTest");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;


    public TestBlackjackEvent() {
        super(NAME, DESCRIPTIONS[0], null);

        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed)
    {
        switch (screenNum) {
            default:
                startGame(new BlackjackMinigame());
                break;
        }
    }
}
