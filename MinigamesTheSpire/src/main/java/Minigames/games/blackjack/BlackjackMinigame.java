package Minigames.games.blackjack;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Collections;

public class BlackjackMinigame extends AbstractMinigame {
    protected static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(Minigames.makeID("Blackjack"));
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Minigames.makeID("BlackjackText"));
    protected String[] TEXT = uiStrings.TEXT;
    protected String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    protected String[] OPTIONS = eventStrings.OPTIONS;
    private GenericEventDialog event;

    private Player player;
    private Dealer dealer;
    private ArrayList<PokerCard> deck = new ArrayList<>();
    private HitButton hitButton;
    private StandButton standButton;
    private BetButton betButton;
    private LeaveButton leaveButton;
    private PlayAgainButton playAgainButton;
    private String middleText = "";
    private int playerHandValue = 0;
    private int dealerHandValue = 0;

    public static final int BETTING = 0;
    public static final int PLAYER_TURN = 1;
    public static final int DEALER_TURN = 2;
    public static final int FINISHED = 3;
    public static final int LEAVE = 4;

    public static final int BUST_THRESHOLD = 21;
    public static final int MIN_BET = 50;
    public static final int MAX_BET = 100;
    private static final int payOutMultiplier = 3;
    private static final int MAX_PLAYS = 3;
    public int numPlays = 0;
    public int bet;

    public BlackjackMinigame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        player = new Player(this);
        dealer = new Dealer(this);
        hitButton = new HitButton(400.0f * Settings.scale, 200.0f * Settings.scale, this);
        standButton = new StandButton(200.0f * Settings.scale, 200.0f * Settings.scale, this);
        betButton = new BetButton(300.0f * Settings.scale, 200.0f * Settings.scale, this);
        leaveButton = new LeaveButton(200.0f * Settings.scale, 200.0f * Settings.scale, this);
        playAgainButton = new PlayAgainButton(400.0f * Settings.scale, 200.0f * Settings.scale, this);
        createNewDeck();
        bet = 0;
        phase = BETTING;
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase)
        {
            case BETTING:
                betButton.update();
                break;
            case PLAYER_TURN:
                hitButton.update();
                standButton.update();
                break;
            case DEALER_TURN:
                dealer.update(elapsed);
                break;
            case FINISHED:
                leaveButton.update();
                playAgainButton.update();
                break;
            case LEAVE:
                isDone = true;
                break;
        }
    }

    public void setPhase(int newPhase) {
        phase = newPhase;
    }

    public void setBet(int bet) {
        this.bet = bet;
        AbstractDungeon.player.loseGold(bet);
        setPhase(BlackjackMinigame.PLAYER_TURN);
        dealInitialCards();
    }

    public void startDealerTurn() {
        this.setPhase(BlackjackMinigame.DEALER_TURN);
        dealer.flipUpCard();
        //dealer.takeTurn();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (phase == BETTING) {
            betButton.render(sb);
        }
        if (phase == PLAYER_TURN) {
            hitButton.render(sb);
            standButton.render(sb);
        }
        if (phase == FINISHED) {
            leaveButton.render(sb);
            playAgainButton.render(sb);
            FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, middleText, (float)1920 / 2 * Settings.scale, (float)1080 / 2 * Settings.scale, Color.WHITE.cpy());
            if (!player.busted) {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, TEXT[9] + dealerHandValue, (float)1920 / 2 * Settings.scale, ((float)1080 / 2  + 50.0f) * Settings.scale, Color.WHITE.cpy());
            }
        }
        if (phase > BETTING) {
            FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, TEXT[8] + playerHandValue, (float)1920 / 2 * Settings.scale, ((float)1080 / 2  - 50.0f) * Settings.scale, Color.WHITE.cpy());
        }
        player.render(sb);
        dealer.render(sb);
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
        dealer.dispose();
//        betButton.dispose();
//        hitButton.dispose();
//        standButton.dispose();
//        leaveButton.dispose();
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        //bindings.addMouseBind((x, y, pointer)->this.isWithinArea(x, y) && pointer == 0, (p)->player.setTargetPoint(p));

        return bindings;
    }

    public void createNewDeck() {
        addAllCardsOfSuite(PokerCard.Suite.Clubs);
        addAllCardsOfSuite(PokerCard.Suite.Diamonds);
        addAllCardsOfSuite(PokerCard.Suite.Hearts);
        addAllCardsOfSuite(PokerCard.Suite.Spades);
        Collections.shuffle(deck, AbstractDungeon.eventRng.random);
    }

    public void addAllCardsOfSuite(PokerCard.Suite suite) {
        for (int i = 2; i <= 14; i++) {
            PokerCard card;
            if (i == 14) {
                card = new PokerCard(i, suite, true, this);
            } else {
                card = new PokerCard(i, suite, this);
            }
            deck.add(card);
        }
    }

    public void dealInitialCards() {
        numPlays++;
        player.busted = false;
        dealer.busted = false;

        PokerCard card = deck.remove(0);
        player.addToHand(card);
        card = deck.remove(0);
        player.addToHand(card);

        card = deck.remove(0);
        card.flipOver();
        dealer.addToHand(card);
        card = deck.remove(0);
        dealer.addToHand(card);

        middleText = "";
        playerHandValue = player.getHandValue();
    }

    public void playAgain() {
        player.clearHand();
        dealer.clearHand();
        betButton.setBet();
        setPhase(BETTING);
    }

    public void hit(AbstractBlackjackPlayer person) {
        PokerCard card = deck.remove(0);
        person.addToHand(card);
        int randomSound = AbstractDungeon.eventRng.random(1, 3);
        CardCrawlGame.sound.playV(Minigames.makeID("cardPlace" + randomSound), 8.0f);
        if (bust(person)) {
            person.busted = true;
            if (person == player) {
                playerLose();
            } else {
                dealerHandValue = dealer.getHandValue();
                playerWin();
            }
        }
    }

    public void playerHit() {
        hit(player);
        playerHandValue = player.getHandValue();
    }

    public void playerWin() {
        setPhase(FINISHED);
        AbstractDungeon.player.gainGold(bet * payOutMultiplier);
        middleText = TEXT[5];
    }

    public void playerLose() {
        setPhase(FINISHED);
        middleText = TEXT[6];
    }

    public void playerTie() {
        setPhase(FINISHED);
        AbstractDungeon.player.gainGold(bet);
        middleText = TEXT[7];
    }

    public void compareHands() {
        dealerHandValue = dealer.getHandValue();
        if (player.getHandValue() > dealer.getHandValue()) {
            playerWin();
        } else if (dealer.getHandValue() > player.getHandValue()) {
            playerLose();
        } else {
            playerTie();
        }
    }

    public boolean bust(AbstractBlackjackPlayer player) {
        return player.getHandValue() > BUST_THRESHOLD;
    }

    public boolean canPlayAgain() {
        if (numPlays < MAX_PLAYS && AbstractDungeon.player.gold >= MIN_BET) {
            return true;
        }
        return false;
    }

    public void setupInstructionScreen(GenericEventDialog event) {
        this.event = event;
        event.updateBodyText("UPDATE BODY TEXT");
        event.setDialogOption("This event has no instructions!");
    }
    public boolean instructionsButtonPressed(int buttonIndex) {
        //If you wanna do fancy stuff, you can track pages in your event and have multiple pages of instructions.
        //Return true here to start the game.
        return true;
    }
}
