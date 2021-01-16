package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
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

    private int subPhase = 0;

    private static int totalSwaps = 10;
    private static int currentSwaps = 0;

    public static float baseSpeed = .75F;

    private static boolean listenForSwap = false;

    private static float sppedIncreasePerSwap = 0.25F;

    public static float timeToBeginNextSwap;

    private float timer = 1F;

    private ArrayList<Shell> shellsToRender = new ArrayList<>();

    @Override
    public void initialize() {
        super.initialize();


        //TODO - Rare Relic, Rare Card, and specifically Regret (worst curse)
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
            case 3:
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

                switch (chosen) {
                    case 1:
                        shell1.currentPhase = Shell.animPhase.SHELLOUTRO;
                        shell1.targetY = 100;
                        break;
                    case 2:
                        shell2.currentPhase = Shell.animPhase.SHELLOUTRO;
                        shell2.targetY = 100;
                        break;
                    case 3:
                        shell3.currentPhase = Shell.animPhase.SHELLOUTRO;
                        shell3.targetY = 100;
                        break;
                }

                phase = 4;
                timer = 1F;
        }
    }

    @Override
    public void update(float elapsed) {
        timer -= Gdx.graphics.getDeltaTime() * timeModifier;
        switch (phase){
            case 0:{
                /**
                 Phase 0: Show each Reward animating in and getting into its proper position.
                 Subphase 0: Left Reward
                 Subphase 1: Right Reward
                 Subphsae 2: Center Reward
                 */
                if (timer <= 0F){
                    switch(subPhase){
                        case 0:{
                            shell1.currentPhase = Shell.animPhase.REWARDINTRO;
                            timer = 1F;  //Wait time for next Reward to animate in and get into place
                            subPhase = 1;
                            break;
                        }
                        case 1:{
                            //Shell 3 second, since the right one needs to animate first or it will be covered by the middle's anim
                            shell3.currentPhase = Shell.animPhase.REWARDINTRO;
                            timer = 1F;  //Wait time for next Reward to animate in and get into place
                            subPhase = 2;
                            break;
                        }
                        case 2:{
                            shell2.currentPhase = Shell.animPhase.REWARDINTRO;
                            timer = 1F;  //Wait time for next Reward to animate in and get into place
                            subPhase = 0;
                            phase = 1;
                            break;
                        }
                    }
                }
                break;
            }
            case 1:{
                /**
                 Phase 1: Show each Shell animating from the top, covering the reward
                 Subphase 0: Left Reward
                 Subphase 1: Center Reward
                 Subphsae 2: Right Reward
                 **/
                if (timer <= 0F){
                    switch(subPhase){
                        case 0:{
                            shell1.currentPhase = Shell.animPhase.SHELLINTRO;
                            shell1.moveTimerY = 0F;
                            shell1.startMoveTimerY = 0.5F;  //Time it takes for the Shell to drop in
                            timer = .25F;  //Wait time before showing next Shell
                            subPhase = 1;
                            break;
                        }
                        case 1:{
                            shell2.currentPhase = Shell.animPhase.SHELLINTRO;
                            shell2.moveTimerY = 0F;
                            shell2.startMoveTimerY = 0.5F;  //Time it takes for the Shell to drop in
                            timer = .25F;  //Wait time before showing next Shell
                            subPhase = 2;
                            break;
                        }
                        case 2:{
                            shell3.currentPhase = Shell.animPhase.SHELLINTRO;
                            shell3.moveTimerY = 0F;
                            shell3.startMoveTimerY = 0.5F;  //Time it takes for the Shell to drop in
                            timer = 1F;  //Wait time before starting the Swaps
                            subPhase = 0;
                            phase = 2;
                            break;
                        }
                    }
                }
                break;
            }
            case 2:{
                /**
                 Phase 2: Animate the swaps.  Controlled mostly in decideSwap() and linked
                 functions within.
                 Subphase 0: Waiting to start the Switcheroo.
                 Subphase 1: Switcheroo has begun.
                 **/
                if (timer <= 0F) {
                    if (subPhase == 0) {
                        shell1.currentPhase = Shell.animPhase.SWITCHEROO;
                        shell2.currentPhase = Shell.animPhase.SWITCHEROO;
                        shell3.currentPhase = Shell.animPhase.SWITCHEROO;
                        subPhase = 1;
                    }
                    if (currentSwaps >= totalSwaps) {
                        //TODO - enable interaction!  Show interactivity somehow
                        phase = 4;
                        shell1.currentPhase = Shell.animPhase.WAITINGFORPLAYER;
                        shell2.currentPhase = Shell.animPhase.WAITINGFORPLAYER;
                        shell3.currentPhase = Shell.animPhase.WAITINGFORPLAYER;
                        timeModifier = 1F;  //Reset time modifier back to normal so the timers aren't still going at lightning speed
                    } else if (timeToBeginNextSwap > 0F) {
                        timeToBeginNextSwap -= Gdx.graphics.getDeltaTime() * timeModifier;
                        if (timeToBeginNextSwap <= 0F) {
                            decideSwap();
                        }
                    }
                }
                break;
            }
            case 3:{
                /**
                 Phase 3: Wait for interactivity.  When a Shell is selected,
                 animate it up and offscreen.  Controlled in the onClick method.
                 **/

                break;
            }
            case 4:{
                /**
                 Phase 4: Grant the Reward.
                 **/
                if (timer <= 0F) {
                    switch (chosen) {
                        case 1: {
                            shell1.grantReward();
                            break;
                        }
                        case 2: {
                            shell2.grantReward();
                            break;
                        }
                        case 3: {
                            shell3.grantReward();
                            break;
                        }
                    }
                    phase = 5;
                    subPhase = 0;
                    timer = 1F;
                }
                break;
            }case 5:{
                /**
                 Phase 5: Reveal the other rewards.
                 Subphase 0: Reveal First reward not chosen.
                 Subphase 1: Reveal Second reward not chosen.
                 **/
                if (timer <= 0F) {
                    switch (chosen) {
                        case 1: {
                            switch (subPhase){
                                case 0:{
                                    shell2.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell2.moveTimerY = 0F;
                                    shell2.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                                case 1:{
                                    shell3.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell3.moveTimerY = 0F;
                                    shell3.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (subPhase){
                                case 0:{
                                    shell1.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell1.moveTimerY = 0F;
                                    shell1.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                                case 1:{
                                    shell3.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell3.moveTimerY = 0F;
                                    shell3.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (subPhase){
                                case 0:{
                                    shell1.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell1.moveTimerY = 0F;
                                    shell1.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                                case 1:{
                                    shell2.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell2.moveTimerY = 0F;
                                    shell2.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                            }
                            break;
                        }
                    }

                }
                break;
            }case 6: {
                /**
                 Phase 6: End the game.
                 **/
                if (timer <= 0F) {
                    //TODO - End the game.  It's all over.
                }

            }
        }

        shell1.update();
        shell2.update();
        shell3.update();
    }

    public void phase5Settings(){
        if (subPhase == 0) {
            subPhase = 1;
            timer = 0.25F;
        } else {
            phase = 6;
            timer = 1F;
        }
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
        //Shuffle the arraylist.  The first index always gets picked to swap.
        Collections.shuffle(shellsToRender, AbstractDungeon.cardRng.random);

        //Random bool to decide who is the other shell to get swapped with - index 1 or index 2.
        //3rd parameter is the Shell that is not moving this swap.
        if (AbstractDungeon.cardRng.randomBoolean()){
            setShellTarget(shellsToRender.get(0), shellsToRender.get(1), shellsToRender.get(2));
        } else {
            setShellTarget(shellsToRender.get(0), shellsToRender.get(2), shellsToRender.get(1));
        }

    }

    public static void receiveSwapComplete(){
        //Listener for when a shell swap has concluded.  Uses listenForSwap to prevent
        //two Shells from triggering this method twice on the same swap.  Whoever finishes first
        // (likely on the same frame) triggers receiveSwapComplete, and the second one gets blocked.
        if (listenForSwap) {
            if (currentSwaps < totalSwaps) {
                listenForSwap = false;
                timeModifier += sppedIncreasePerSwap;
                currentSwaps++;
                timeToBeginNextSwap = 0.25F / timeModifier;
            }
        }
    }

    public void setShellTarget(Shell s1, Shell s2, Shell unmoved) {
        //Render order is cleared and recreated at the end.
        shellsToRender.clear();

        listenForSwap = true;

        //Set each shell's Target X to the other's current X.
        s1.targetX = s2.x;
        s2.targetX = s1.x;

        //Set a bunch of startup variables for the anim
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

        //Whichever shell is moving left becomes the one rotating into the background
        //the shell moving right rotates into the foreground
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

        //Shell render order is important and is reset with every swap.
        //The shell rotating in the foreground is rendered above the rest.
        //The shell rotating in the background is rendered behind the rest.
        for (Shell s : shellsToRender){
            s.render(sb);
        }
    }
}
