package Minigames.games.blackjack;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.Collections;

public class BlackjackMinigame extends AbstractMinigame {
    private Player player;
    private Dealer dealer;
    private ArrayList<PokerCard> deck = new ArrayList<>();
    private HitButton hitButton;
    private StandButton standButton;
    private BetButton betButton;
    private int payOutMultiplier = 3;

    public static final int BETTING = 0;
    public static final int PLAYER_TURN = 1;
    public static final int DEALER_TURN = 2;
    public static final int FINISHED = 3;

    public static final int BUST_THRESHOLD = 21;
    private int bet;

    public BlackjackMinigame() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        player = new Player(this);
        dealer = new Dealer(this);
        hitButton = new HitButton(-AbstractMinigame.SIZE / 3.0f, 0.0f, this);
        standButton = new StandButton(-AbstractMinigame.SIZE / 3.0f, 0.0f, this);
        betButton = new BetButton(0.0f, 0.0f, this);
        createNewDeck();
        bet = 0;
        phase = BETTING;
        System.out.println(deck);
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
                isDone = true;
                break;
        }
    }

    public void setPhase(int newPhase) {
        phase = newPhase;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        System.out.println(phase);
        System.out.println(betButton);
        if (phase == BETTING) {
            betButton.render(sb);
        }
        if (phase == PLAYER_TURN) {
            hitButton.render(sb);
            standButton.render(sb);
        }
        player.render(sb);
        dealer.render(sb);
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
        dealer.dispose();
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
            PokerCard card = new PokerCard(i, suite, this);
            deck.add(card);
        }
    }

    public void dealInitialCards() {
        PokerCard card = deck.remove(0);
        player.addToHand(card);
        card = deck.remove(0);
        player.addToHand(card);

        card = deck.remove(0);
        dealer.addToHand(card);
        card = deck.remove(0);
        card.flipOver();
        dealer.addToHand(card);
    }

    public void hit(AbstractBlackjackPlayer person) {
        PokerCard card = deck.remove(0);
        person.addToHand(card);
        if (bust(person)) {
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
        System.out.println("YOU WIN");
        AbstractDungeon.player.gainGold(bet * payOutMultiplier);
    }

    public void playerLose() {
        setPhase(FINISHED);
        System.out.println("YOU LOSE");
    }

    public void playerTie() {
        setPhase(FINISHED);
        System.out.println("YOU TIED");
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
        if (player.getHandValue() > BUST_THRESHOLD) {
            return true;
        }
        return false;
    }
}
