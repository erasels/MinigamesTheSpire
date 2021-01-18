package Minigames.events;

import Minigames.games.AbstractMinigame;
import Minigames.util.HelperClass;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;

import java.util.ArrayList;

public abstract class AbstractMinigameEvent extends AbstractImageEvent {
    public static AbstractMinigame game; //should never be more than one. Also lets you make sure it is disposed.
    protected int chosenMinigame;
    protected ArrayList<AbstractMinigame> minigames = new ArrayList<>();
    public static final int NUM_GAMES = 3;

    public AbstractMinigameEvent(String title, String body, String imgUrl) {
        super(title, body, imgUrl);

        if (game != null)
        {
            game.dispose(); //player quit in middle of a minigame, dispose the old one.
            game = null;
        }

        //TODO - Decide if alt music should play for all minigames.  Is currently only playing in Shell Game.  Could also play only during minigame time.
        //CardCrawlGame.music.playTempBgmInstantly("minigames:carnivalMusic", true);
        noCardsInRewards = true;
    }

    protected void startGame(AbstractMinigame newGame)
    {
        this.imageEventText.clearAllDialogs();
        GenericEventDialog.hide();

        game = newGame;
        game.initialize();
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

    public void update() {
        if (game != null && game.playing())
        {
            game.update(HelperClass.getTime()); //no superfast mode shenangnagiagngas

            if (game.gameDone())
            {
                game.dispose();
                game = null;
                finishGame();
            }
        }
        super.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (game != null && game.playing())
        {
            game.render(sb);
        }
    }

    public void finishGame() {
        GenericEventDialog.show();
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

    public void endOfEvent() {
        CardCrawlGame.music.fadeOutTempBGM();
        this.imageEventText.clearAllDialogs();
        screenNum = 4;
    }
}
