package Minigames.events;

import Minigames.games.slimePopper.SlimePopper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import static Minigames.Minigames.makeID;

public class TestSlimePopperEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("SlimePopper");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;


    public TestSlimePopperEvent() {
        super(NAME, DESCRIPTIONS[0], null);

        imageEventText.setDialogOption(OPTIONS[0]);
        noCardsInRewards = true;
    }

    @Override
    protected void buttonEffect(int buttonPressed)
    {
        if (screenNum == 99) {
            openMap();
        } else {
            startGame(new SlimePopper());
        }
    }

    @Override
    public void finishGame() {
        GenericEventDialog.show();
        imageEventText.updateBodyText("Looks like you win a prize!");
        imageEventText.setDialogOption("Leave");
        screenNum = 99;
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }
}
