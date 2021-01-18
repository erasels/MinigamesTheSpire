package Minigames.events;

import Minigames.games.AbstractMinigame;
import Minigames.games.test.TestMinigame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;
import java.util.Collections;

import static Minigames.Minigames.makeID;
import static Minigames.Minigames.srcMinigameList;

public class ActThreeArcade extends AbstractMinigameEvent {
    public static final String ID = makeID(ActThreeArcade.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public ActThreeArcade() {
        super(NAME, DESCRIPTIONS[0], "images/events/winding.jpg");

        // Add all minigames that fill the criteria
        for(AbstractMinigame m : srcMinigameList){
            if(m.canSpawnInActThreeEvent() && m.canSpawn()){
                minigames.add(m.makeCopy());
            }
        }
        if(minigames.isEmpty()){
            // as a failsafe -> add testminigame
            minigames.add(new TestMinigame());
        }
        Collections.shuffle(minigames, AbstractDungeon.eventRng.random);
        ArrayList<AbstractMinigame> chosenGames = new ArrayList<>(minigames.subList(0, NUM_GAMES));
        for (AbstractMinigame minigame : chosenGames) {
            imageEventText.setDialogOption(minigame.getOption());
        }
    }

    @Override
    public void endOfEvent() {
        super.endOfEvent();
        this.imageEventText.loadImage("images/events/winding.jpg"); //in case one of the minigames change the image
        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[0]);
    }
}
