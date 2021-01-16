package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Collections;

import static Minigames.Minigames.getModID;

public class ShellGame extends AbstractMinigame {

    /*
    So, basically the way this should be coded, I think
    is that we have the 3 shell objects.
    We initialize the rewards on wherever this stuff inits,
    then initialize the shells with those rewards.
    Phase is 0 on setup, which is where we see the rewards, and then the shell x/y move to cover them.
    Then we do phase 1, which is the shuffling part. You watch them shuffle
    and then once they're done shuffling, we make it clear you can click,
    and then that's phase 2. On phase 2, when you click,
    pull up the corresponding shell (that's phase 3), and grant the reward in a pretty fashion,
    like how Gremlin Match puts the cards in your deck from the screen. Boom!
     */

    private Shell shell1;
    private Shell shell2;
    private Shell shell3;

    private int chosen = -1;

    private static float timeModifier = 1F;

    private static float xpos1 = Settings.WIDTH * 0.4F;
    private static float xpos2 = Settings.WIDTH * 0.5F;
    private static float xpos3 = Settings.WIDTH * 0.6F;

    public static float yBackgroundSwap = Settings.HEIGHT * 0.55F;
    public static float yForegroundSwap = Settings.HEIGHT * 0.45F;
    public static float yMid = Settings.HEIGHT * 0.5F;

    public static float scaleForegroundSwap = 1.25F;
    public static float scaleBackgroundSwap = 0.75F;

    private static int totalSwaps = 10;
    private static int currentSwaps = 0;

    public static float baseSpeed = .75F;

    private static boolean listenForSwap = false;

    private static float sppedIncreasePerSwap = 0.25F;

    public static float timeToBeginNextSwap;

    private ArrayList<Shell> shellsToRender = new ArrayList<>();

    @Override
    public void initialize() {
        super.initialize();

        /*
        These should probably be just a part of the Shell object.  That way they move
         when the shells move.  At the end, after clicking, the shell can just slide up and reveal
         the reward underneath.
         */
        AbstractRelic rewardRelic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier());
        AbstractCard rewardCard = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE);
        AbstractCard nastyCurse = CardLibrary.getCurse();

        //yMid + some offset to get them to start above at the beginning
        shell1 = new Shell(xpos1, yMid, rewardCard);
        shell2 = new Shell(xpos2, yMid, rewardRelic);
        shell3 = new Shell(xpos3, yMid, nastyCurse);

        shellsToRender.add(shell1);
        shellsToRender.add(shell2);
        shellsToRender.add(shell3);

        timeToBeginNextSwap = 1F;
    }

    private void onClick() {
        switch (phase) {
            case 2:
                if (shell1.hb.hovered) {
                    chosen = 1;
                    phase = 3;
                } else if (shell2.hb.hovered) {
                    chosen = 2;
                    phase = 3;
                } else if (shell3.hb.hovered) {
                    chosen = 3;
                    phase = 3;
                }
        }
    }

    @Override
    public void update(float elapsed) {
        if (phase == 3) {
            switch (chosen) {
                case 1:
                    shell1.targetY = 100;
                    if (shell1.y == shell1.targetY) {
                        phase = 4;
                    }
                    break;
                case 2:
                    shell2.targetY = 100;
                    if (shell2.y == shell2.targetY) {
                        phase = 4;
                    }
                    break;
                case 3:
                    shell3.targetY = 100;
                    if (shell3.y == shell3.targetY) {
                        phase = 4;
                    }
                    break;
            }
        } else if (phase == 4) {
            switch (chosen) {
                case 1:
                    shell1.grantReward();
                    break;
                case 2:
                    shell2.grantReward();
                    break;
                case 3:
                    shell3.grantReward();
                    break;
            }
        }

        shell1.update();
        shell2.update();
        shell3.update();
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), (p) -> onClick());
        return bindings;
    }

    @Override
    public String getOption() {
        return CardCrawlGame.languagePack.getEventString(getModID() + "ShellGame").OPTIONS[0];
    }


    public void decideSwap(){
        Collections.shuffle(shellsToRender, AbstractDungeon.cardRng.random);
        if (AbstractDungeon.cardRng.randomBoolean()){
            setShellTarget(shellsToRender.get(0), shellsToRender.get(1), shellsToRender.get(2));
        } else {
            setShellTarget(shellsToRender.get(0), shellsToRender.get(2), shellsToRender.get(1));
        }

    }

    public static void receiveSwapComplete(){
        if (listenForSwap) {
            if (currentSwaps < totalSwaps) {
                listenForSwap = false;
                timeModifier += sppedIncreasePerSwap;
                currentSwaps++;
                timeToBeginNextSwap = 0.25F / timeModifier;
            } else {
                //enable interaction!
            }
        }
    }

    public void setShellTarget(Shell s1, Shell s2, Shell unmoved) {
        shellsToRender.clear();

        listenForSwap = true;

        s1.targetX = s2.x;
        s2.targetX = s1.x;

        s1.startX = s1.x;
        s2.startX = s2.x;

        s1.startY = yMid;
        s2.startY = yMid;

        s1.startScale = 1F;
        s2.startScale = 1F;

        s1.isMoving = true;
        s2.isMoving = true;

        s1.yApexReached = false;
        s2.yApexReached = false;

        s1.moveTimer = 0;
        s2.moveTimer = 0;

        s1.moveTimerY = 0;
        s2.moveTimerY = 0;

        s1.startMoveTimer = baseSpeed / timeModifier;
        s2.startMoveTimer = baseSpeed / timeModifier;

        s1.startMoveTimerY = baseSpeed / timeModifier / 2;
        s2.startMoveTimerY = baseSpeed / timeModifier / 2;

        //Whichever shell is moving left becomes the one rotating left into the backgruond
        //the shell moving right rotates right into the foreground
        if (s1.targetX < s2.targetX){
            s1.targetY = yBackgroundSwap;
            s1.targetScale = scaleBackgroundSwap;
            s2.targetY = yForegroundSwap;
            s2.targetScale = scaleForegroundSwap;
            shellsToRender.add(s1);  //becomes the first one to render (behind others)
            shellsToRender.add(unmoved);  //becomes the second one to render
            shellsToRender.add(s2);  //becomes the last one to render (in front of others)
        } else {
            s2.targetY = yBackgroundSwap;
            s2.targetScale = scaleBackgroundSwap;
            s1.targetY = yForegroundSwap;
            s1.targetScale = scaleForegroundSwap;
            shellsToRender.add(s2); //becomes the first one to render
            shellsToRender.add(unmoved);  //becomes the second one to render
            shellsToRender.add(s1);  //becomes the last one to render (in front of others)
        }

    }


    public void render(SpriteBatch sb) {

        for (Shell s : shellsToRender){
            s.render(sb);
        }
    }
}
