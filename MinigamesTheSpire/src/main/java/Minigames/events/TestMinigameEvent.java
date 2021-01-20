package Minigames.events;

import Minigames.games.AbstractMinigame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;
import static Minigames.Minigames.srcMinigameList;

public class TestMinigameEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("Test");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public TestMinigameEvent() {
        super(NAME, NAME, "images/events/ballAndCup.jpg");
        // Add all minigames that fill the criteria
        for(AbstractMinigame m : srcMinigameList){
            minigames.add(m.makeCopy());
        }
        for (AbstractMinigame minigame : minigames) {
            imageEventText.setDialogOption(minigame.getOption());
        }
    }

    @Override
    public void endOfEvent() {
        super.endOfEvent();
        this.imageEventText.loadImage("images/events/ballAndCup.jpg"); //in case one of the minigames change the image
        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }
}
