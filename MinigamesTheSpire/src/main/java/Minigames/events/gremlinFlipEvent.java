package Minigames.events;

import Minigames.games.gremlinFlip.gremlinFlip;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;

public class gremlinFlipEvent extends AbstractMinigameEvent {
    public static final String ID = makeID(gremlinFlip.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private final int GOLD_CLEAR_REWARD = 50;
    private int GOLD = 0;

    public gremlinFlipEvent() {
        super(NAME, DESCRIPTIONS[0], null);

        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed)
    {
        switch (screenNum) {
            default:
                startGame(new gremlinFlip());
                break;

            case 1:
                AbstractDungeon.player.gainGold(GOLD);
                openMap();
                break;

            case 2:
                openMap();
                break;
        }
    }

    public void finishGame() {
        GenericEventDialog.show();
        GOLD = GOLD_CLEAR_REWARD + gremlinFlip.goldScore;
        if(gremlinFlip.failedMinigame){
            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
            this.imageEventText.setDialogOption(OPTIONS[2]);
            screenNum = 2;
        }
        else {
            this.imageEventText.updateBodyText(String.format(DESCRIPTIONS[1], GOLD_CLEAR_REWARD, gremlinFlip.goldScore));
            this.imageEventText.setDialogOption(String.format(OPTIONS[1], GOLD));
            screenNum = 1;
        }
    }
}