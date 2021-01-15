package Minigames.games.blackjack;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Collections;

public class BlackjackMinigame extends AbstractMinigame {
    protected static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Minigames.makeID("BlackjackText"));
    protected String[] TEXT = uiStrings.TEXT;

    private Player player;
    private Dealer dealer;
    private ArrayList<PokerCard> deck = new ArrayList<>();
    private HitButton hitButton;
    private StandButton standButton;
    private BetButton betButton;
    private LeaveButton leaveButton;
    private String finishText = null;

    public static final int BETTING = 0;
    public static final int PLAYER_TURN = 1;
    public static final int DEALER_TURN = 2;
    public static final int FINISHED = 3;
    public static final int LEAVE = 4;

    public static final int BUST_THRESHOLD = 21;
    public static final int MAX_BET = 100;
    private static final int payOutMultiplier = 3;
    public int bet;

    public BlackjackMinigame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        player = new Player(this);
        dealer = new Dealer(this);
        hitButton = new HitButton(400.0f, 200.0f, this);
        standButton = new StandButton(200.0f, 200.0f, this);
        betButton = new BetButton(300.0f, 200.0f, this);
        leaveButton = new LeaveButton(300.0f, 200.0f, this);
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
                break;
            case FINISHED:
                leaveButton.update();
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
        dealer.takeTurn();
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
            FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, finishText, (float)1920 / 2 * Settings.scale, (float)1080 / 2 * Settings.scale, Color.WHITE.cpy());
        }
        player.render(sb);
        dealer.render(sb);
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
        dealer.dispose();
        betButton.dispose();
        hitButton.dispose();
        standButton.dispose();
        leaveButton.dispose();
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
    }

    public void hit(AbstractBlackjackPlayer person) {
        PokerCard card = deck.remove(0);
        person.addToHand(card);
        if (bust(person)) {
            person.busted = true;
            if (person == player) {
                playerLose();
            } else {
                playerWin();
            }
        }
    }

    public void playerHit() {
        hit(player);
    }

    public void playerWin() {
        setPhase(FINISHED);
        System.out.println("player hand value: " + player.getHandValue());
        System.out.println("dealer hand value: " + dealer.getHandValue());
        System.out.println("YOU WIN");
        AbstractDungeon.player.gainGold(bet * payOutMultiplier);
        finishText = TEXT[5];
    }

    public void playerLose() {
        setPhase(FINISHED);
        System.out.println("player hand value: " + player.getHandValue());
        System.out.println("dealer hand value: " + dealer.getHandValue());
        System.out.println("YOU LOSE");
        finishText = TEXT[6];
    }

    public void playerTie() {
        setPhase(FINISHED);
        System.out.println("player hand value: " + player.getHandValue());
        System.out.println("dealer hand value: " + dealer.getHandValue());
        System.out.println("YOU TIED");
        AbstractDungeon.player.gainGold(bet);
        finishText = TEXT[7];
    }

    public void compareHands() {
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
}
