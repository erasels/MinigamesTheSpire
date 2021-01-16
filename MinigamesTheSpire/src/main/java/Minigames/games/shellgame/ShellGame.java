package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

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

    @Override
    public void initialize() {
        super.initialize();
        AbstractRelic rewardRelic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier());
        rewardRelic.currentX = rewardRelic.targetX = 100;
        rewardRelic.currentY = rewardRelic.targetY = 0;
        AbstractCard rewardCard = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE);
        rewardCard.current_x = rewardCard.target_x = 200;
        rewardCard.current_y = rewardCard.target_y = 0;
        AbstractCard nastyCurse = CardLibrary.getCurse();
        nastyCurse.current_x = nastyCurse.target_x = 300;
        nastyCurse.current_y = nastyCurse.target_y = 0;
        shell1 = new Shell(100, 100, rewardCard);
        shell2 = new Shell(200, 100, rewardRelic);
        shell3 = new Shell(300, 100, nastyCurse);
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
    public void render(SpriteBatch sb) {
        shell1.render(sb);
        shell2.render(sb);
        shell3.render(sb);
    }

    private void updateShellWhyIsPhaseProtected(Shell shell) {
        if (phase == 0) {
            shell.targetY = 0;
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
        }
        else if (phase == 4) {
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
        updateShellWhyIsPhaseProtected(shell1);
        shell1.update(elapsed);
        updateShellWhyIsPhaseProtected(shell2);
        shell2.update(elapsed);
        updateShellWhyIsPhaseProtected(shell3);
        shell3.update(elapsed);
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), (p) -> onClick());
        return bindings;
    }
}
