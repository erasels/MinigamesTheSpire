package Minigames.events;

import Minigames.games.shellgame.Shell;
import Minigames.games.shellgame.ShellGame;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;

import static Minigames.Minigames.makeID;

public class ShellGameEvent extends AbstractMinigameEvent {
    public static final String ID = makeID("ShellGame");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;


    public ShellGameEvent() {
        super(NAME, DESCRIPTIONS[0], "images/events/ballAndCup.jpg");

        imageEventText.setDialogOption(OPTIONS[0]);
        imageEventText.setDialogOption(OPTIONS[1]);
        imageEventText.setDialogOption(OPTIONS[2]);

        CardCrawlGame.music.playTempBgmInstantly("minigames:carnivalMusic", true);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (screenNum == 0) {
            switch (buttonPressed) {
                case 0: {
                    ShellGame.difficultyMode = 0;
                    startGame(new ShellGame());
                    break;
                }
                case 1: {
                    ShellGame.difficultyMode = 1;
                    startGame(new ShellGame());
                    break;
                }
                case 2: {
                    ShellGame.difficultyMode = 2;
                    startGame(new ShellGame());
                    break;
                }
                default:
                    startGame(new ShellGame());
                    break;
            }
        } else {
            openMap();
        }
    }

    @Override
    public void finishGame() {
        if (game instanceof ShellGame) {
            for (Shell s : ((ShellGame) game).shellsToRender) {
                s.heldCard = null;
                s.heldRelic = null;
            }
        }
        GenericEventDialog.show();
        imageEventText.clearAllDialogs();
        if (ShellGame.gotCurse) {
            this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
            this.imageEventText.setDialogOption(OPTIONS[3]);
            screenNum = 2;
        } else {
            this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
            this.imageEventText.setDialogOption(OPTIONS[3]);
            screenNum = 2;
        }
    }
}