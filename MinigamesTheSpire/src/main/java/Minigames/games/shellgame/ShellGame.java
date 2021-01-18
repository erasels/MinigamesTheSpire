package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.util.HelperClass;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Injury;
import com.megacrit.cardcrawl.cards.curses.Normality;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.Collections;

import static Minigames.Minigames.makeID;

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

    public static final String ID = makeID("ShellGame");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private Shell shell1;
    private Shell shell2;
    private Shell shell3;

    private int chosen = -1;

    private static float timeModifier = 1F;

    private static final float xpos1 = Settings.WIDTH * 0.4F;
    private static final float xpos2 = Settings.WIDTH * 0.5F;
    private static final float xpos3 = Settings.WIDTH * 0.6F;

    public static final float offscreenShellHeight = 195F;

    private static float yBackgroundSwap = Settings.HEIGHT * 0.575F;
    private static float yForegroundSwap = Settings.HEIGHT * 0.425F;
    public static float yMid = Settings.HEIGHT * 0.5F;

    private static final float scaleForegroundSwap = 1.25F;
    private static final float scaleBackgroundSwap = 0.75F;

    public static final float cardScaleStart = 0.05F;
    public static final float cardScalePeak = 1.15F;
    public static final float cardScaleNorm = 1F;
    public static final float cardScaleCup = .3F;

    public static final float relicScaleStart = 0.05F;
    public static final float relicScalePeak = 13F;
    public static final float relicScaleNorm = 11F;
    public static final float relicScaleCup = 3F;

    private static final float baseSpeed = .75F;

    private int subPhase = 0;

    private static swapType lastSwap;

    private static int totalSwaps = 0;
    private static int currentSwaps = 0;

    private static boolean listenForSwap = false;

    private static float sppedIncreasePerSwap = 0.2F;

    private static float timeToBeginNextSwap;

    private float timer = 1F;

    private ArrayList<Shell> shellsToRender = new ArrayList<>();

    private static int difficultyMode = 0;

    public static boolean gotCurse = false;

    @Override
    public void setupInstructionScreen(GenericEventDialog event) {

        event.updateBodyText(DESCRIPTIONS[0]);

        event.setDialogOption(OPTIONS[0]);
        event.setDialogOption(OPTIONS[1]);
        event.setDialogOption(OPTIONS[2]);

        event.loadImage("images/events/ballAndCup.jpg");
        CardCrawlGame.music.playTempBgmInstantly("minigames:carnivalMusic", true);
    }

    @Override
    public void setupPostgameScreen(GenericEventDialog event) {

        for (Shell s : shellsToRender) {
            s.heldCard = null;
            s.heldRelic = null;
        }

        if (ShellGame.gotCurse) {
            event.updateBodyText(DESCRIPTIONS[1]);
            event.setDialogOption(OPTIONS[3]);
        } else {
            event.updateBodyText(DESCRIPTIONS[2]);
            event.setDialogOption(OPTIONS[3]);
        }
    }

    @Override
    public boolean instructionsButtonPressed(int buttonIndex) {
        switch (buttonIndex) {
            case 0: {
                ShellGame.difficultyMode = 0;
                break;
            }
            case 1: {
                ShellGame.difficultyMode = 1;
                break;
            }
            case 2: {
                ShellGame.difficultyMode = 2;
                break;
            }
            default:
                break;
        }
        return true;
    }


    public String getOption() {
        return eventStrings.NAME;
    }

    @Override
    public void initialize() {
        super.initialize();

        AbstractRelic rewardRelic = null;
        AbstractCard rewardCard = null;
        AbstractCard nastyCurse = null;

        gotCurse = false;

        switch (difficultyMode) {
            case 0: {
                rewardRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.COMMON);
                rewardCard = AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON);
                nastyCurse = new Injury();
                totalSwaps = 10;
                sppedIncreasePerSwap = 0.2F;
                break;
            }
            case 1: {
                rewardRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.UNCOMMON);
                rewardCard = AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON);
                nastyCurse = new Regret();
                totalSwaps = 17;
                sppedIncreasePerSwap = 0.21F;
                break;
            }
            case 2: {
                rewardRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.RARE);
                rewardCard = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE);
                nastyCurse = new Normality();
                totalSwaps = 25;
                sppedIncreasePerSwap = 0.22F;
                break;
            }
        }

        //yMid + some offset to get them to start above at the beginning
        shell1 = new Shell(this, xpos2, yMid, rewardCard);
        shell2 = new Shell(this, xpos2, yMid, rewardRelic);
        shell3 = new Shell(this, xpos2, yMid, nastyCurse);

        shell1.heldCard.drawScale = shell1.heldCard.targetDrawScale = cardScaleStart;
        shell2.heldRelic.scale = relicScaleStart;
        shell3.heldCard.drawScale = shell3.heldCard.targetDrawScale = cardScaleStart;

        shell1.heldCard.current_x = shell1.heldCard.target_x = xpos2;
        shell2.heldRelic.currentX = shell2.heldRelic.targetX = xpos2;
        shell3.heldCard.current_x = shell3.heldCard.target_x = xpos2;

        shellsToRender.add(shell1);
        shellsToRender.add(shell2);
        shellsToRender.add(shell3);

        timeToBeginNextSwap = 1F;

        timer = 1F;
        phase = 0;
        subPhase = 0;

        shell1.shellOffsetY = offscreenShellHeight;
        shell2.shellOffsetY = offscreenShellHeight;
        shell3.shellOffsetY = offscreenShellHeight;

        gotCurse = false;
        listenForSwap = false;
        currentSwaps = 0;

        lastSwap = swapType.NONE;
    }


    private void onClick() {
        switch (phase) {
            case 3:
                if (shell1.hb.hovered) {
                    chosen = 1;
                } else if (shell2.hb.hovered) {
                    chosen = 2;
                } else if (shell3.hb.hovered) {
                    chosen = 3;
                }

                switch (chosen) {
                    case 1:
                        CardCrawlGame.sound.playAV("SHOP_PURCHASE", .1F, 2F);
                        shell1.currentPhase = Shell.animPhase.SHELLOUTRO;
                        shell1.startMoveTimerY = 0.5F;
                        shell1.moveTimerY = 0F;
                        break;
                    case 2:
                        CardCrawlGame.sound.playAV("SHOP_PURCHASE", .1F, 2F);
                        shell2.currentPhase = Shell.animPhase.SHELLOUTRO;
                        shell2.startMoveTimerY = 0.5F;
                        shell2.moveTimerY = 0F;
                        break;
                    case 3:
                        CardCrawlGame.sound.playA("INTIMIDATE", .1F);
                        shell3.currentPhase = Shell.animPhase.SHELLOUTRO;
                        shell3.startMoveTimerY = 0.5F;
                        shell3.moveTimerY = 0F;
                        break;
                }

                phase = 4;
                timer = 1F;
        }
    }


    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        if (phase != 3)
            timer -= elapsed * timeModifier;
        switch (phase) {
            case 0: {
                /**
                 Phase 0: Show each Reward animating in and getting into its proper position.
                 Subphase 0: Left Reward
                 Subphase 1: Right Reward
                 Subphsae 2: Center Reward
                 */
                if (timer <= 0F) {
                    switch (subPhase) {
                        case 0: {
                            shell1.currentPhase = Shell.animPhase.REWARDINTRO;
                            CardCrawlGame.sound.playAV("HEAL_1", -.2F, 2F);
                            shell1.moveTimer = 0F;
                            shell1.startMoveTimer = .5F;
                            shell1.targetX = xpos1;
                            timer = 1.25F;  //Wait time for next Reward to animate in and get into place
                            subPhase = 1;
                            break;
                        }
                        case 1: {
                            //Shell 3 second, since the right one needs to animate first or it will be covered by the middle's anim
                            shell3.currentPhase = Shell.animPhase.REWARDINTRO;
                            CardCrawlGame.sound.playAV("HEAL_2", -.2F, 2F);
                            shell3.moveTimer = 0F;
                            shell3.startMoveTimer = .5F;
                            shell3.targetX = xpos3;
                            timer = 1.25F;  //Wait time for next Reward to animate in and get into place
                            subPhase = 2;
                            break;
                        }
                        case 2: {
                            shell2.currentPhase = Shell.animPhase.REWARDINTRO;
                            CardCrawlGame.sound.playAV("HEAL_3", -.2F, 2F);
                            shell2.moveTimer = 0F;
                            shell2.startMoveTimer = .5F;
                            shell2.targetX = xpos2;
                            timer = 1.5F;  //Wait time for next Reward to animate in and get into place
                            subPhase = 0;
                            phase = 1;
                            break;
                        }
                    }
                }
                break;
            }
            case 1: {
                /**
                 Phase 1: Show each Shell animating from the top, covering the reward
                 Subphase 0: Left Reward
                 Subphase 1: Center Reward
                 Subphsae 2: Right Reward
                 **/
                if (timer <= 0F) {
                    switch (subPhase) {
                        case 0: {
                            shell1.currentPhase = Shell.animPhase.SHELLINTRO;
                            CardCrawlGame.sound.playAV("BLOCK_GAIN_1", .15F, .4F);
                            //shell1.targetY = yMid;
                            shell1.moveTimerY = 0F;
                            shell1.startMoveTimerY = 0.5F;  //Time it takes for the Shell to drop in
                            timer = .25F;  //Wait time before showing next Shell
                            subPhase = 1;
                            break;
                        }
                        case 1: {
                            shell2.currentPhase = Shell.animPhase.SHELLINTRO;
                            CardCrawlGame.sound.playAV("BLOCK_GAIN_2", .15F, .4F);
                            //shell2.targetY = yMid;
                            shell2.moveTimerY = 0F;
                            shell2.startMoveTimerY = 0.5F;  //Time it takes for the Shell to drop in
                            timer = .25F;  //Wait time before showing next Shell
                            subPhase = 2;
                            break;
                        }
                        case 2: {
                            shell3.currentPhase = Shell.animPhase.SHELLINTRO;
                            CardCrawlGame.sound.playAV("BLOCK_GAIN_3", .15F, .4F);
                            //shell3.targetY = yMid;
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
            case 2: {
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
                        phase = 3;
                        shell1.currentPhase = Shell.animPhase.WAITINGFORPLAYER;
                        shell1.setEnclosedLocations();
                        shell2.currentPhase = Shell.animPhase.WAITINGFORPLAYER;
                        shell2.setEnclosedLocations();
                        shell3.currentPhase = Shell.animPhase.WAITINGFORPLAYER;
                        shell3.setEnclosedLocations();
                        timeModifier = 1F;  //Reset time modifier back to normal so the timers aren't still going at lightning speed
                    } else if (timeToBeginNextSwap > 0F) {
                        timeToBeginNextSwap -= elapsed * timeModifier;
                        if (timeToBeginNextSwap <= 0F) {
                            decideSwap();
                        }
                    }
                }
                break;
            }
            case 3: {
                /**
                 Phase 3: Wait for interactivity.  When a Shell is selected,
                 animate it up and offscreen.  Controlled in the onClick method.
                 **/

                break;
            }
            case 4: {
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
            }
            case 5: {
                /**
                 Phase 5: Reveal the other rewards.
                 Subphase 0: Reveal First reward not chosen.
                 Subphase 1: Reveal Second reward not chosen.
                 **/
                if (timer <= 0F) {
                    switch (chosen) {
                        case 1: {
                            switch (subPhase) {
                                case 0: {
                                    shell2.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell2.moveTimerY = 0F;
                                    shell2.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                                case 1: {
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
                            switch (subPhase) {
                                case 0: {
                                    shell1.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell1.moveTimerY = 0F;
                                    shell1.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                                case 1: {
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
                            switch (subPhase) {
                                case 0: {
                                    shell1.currentPhase = Shell.animPhase.SHELLOUTRO;
                                    shell1.moveTimerY = 0F;
                                    shell1.startMoveTimerY = 0.5F;  //Time it takes for the Shell to fly out
                                    phase5Settings();
                                    break;
                                }
                                case 1: {
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
            }
            case 6: {
                /**
                 Phase 6: End the game.
                 **/
                if (timer <= 0F) {
                    isDone = true;
                }

            }
        }

        shell1.update(elapsed);
        shell2.update(elapsed);
        shell3.update(elapsed);
    }

    public void phase5Settings() {
        CardCrawlGame.sound.playAV("CARD_POWER_WOOSH", .8F, 5F);
        if (subPhase == 0) {
            subPhase = 1;
            timer = 0.25F;
        } else {
            phase = 6;
            timer = 2F;
        }
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), (p) -> onClick());
        return bindings;
    }

    public void decideSwap() {
        ArrayList<swapType> validswaps = new ArrayList<>();
        if (lastSwap != swapType.FIRSTANDSECOND) validswaps.add(swapType.FIRSTANDSECOND);
        if (lastSwap != swapType.FIRSTANDTHIRD) validswaps.add(swapType.FIRSTANDTHIRD);
        if (lastSwap != swapType.SECONDANDTHIRD) validswaps.add(swapType.SECONDANDTHIRD);

        Collections.shuffle(validswaps);

        lastSwap = validswaps.get(0);
        //This is used to ensure no swap happens twice in a row, making the difficulty more consistent.  It's easy to follow the same swap happening repeatedly.
        switch (validswaps.get(0)){
            case FIRSTANDSECOND:{
                setShellTarget(shell1, shell2, shell3);
                break;
            }
            case SECONDANDTHIRD:{
                setShellTarget(shell2, shell3, shell1);
                break;
            }
            case FIRSTANDTHIRD:{
                setShellTarget(shell1, shell3, shell2);
                break;
            }
        }

        CardCrawlGame.sound.playA("ATTACK_WHIFF_1", .3F * (timeModifier / 5F));

    }

    public static void receiveSwapComplete() {
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
        if (s1.targetX < s2.targetX) {
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
        super.render(sb);

        //Debugging text renders
        // FontHelper.renderFontLeft(sb, FontHelper.menuBannerFont, String.valueOf(timer), Settings.HEIGHT / 2F, Settings.WIDTH / 2F, Color.RED.cpy());
        //FontHelper.renderFontLeft(sb, FontHelper.menuBannerFont, String.valueOf(phase), Settings.HEIGHT / 2F, Settings.WIDTH / 2F - (50 * Settings.scale), Color.RED.cpy());
        // FontHelper.renderFontLeft(sb, FontHelper.menuBannerFont, String.valueOf(subPhase), Settings.HEIGHT / 2F, Settings.WIDTH / 2F - (100 * Settings.scale), Color.RED.cpy());

        //Shell render order is important and is reset with every swap.
        //The shell rotating in the foreground is rendered above the rest.
        //The shell rotating in the background is rendered behind the rest.
        for (Shell s : shellsToRender) {
            s.render(sb);
        }
    }

    public AbstractMinigame makeCopy() {
        return new ShellGame();
    }

    public enum swapType {
        NONE,
        FIRSTANDTHIRD,
        FIRSTANDSECOND,
        SECONDANDTHIRD;

        swapType() {
        }
    }
}
