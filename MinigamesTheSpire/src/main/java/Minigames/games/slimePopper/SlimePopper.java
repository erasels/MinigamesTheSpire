package Minigames.games.slimePopper;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.mastermind.MastermindMinigame;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.IntStream;

import static Minigames.Minigames.makeID;

/**
 * Slime Popper.
 * <p>
 * Game has 2 phases - 1st phase, you lock in a position on a meter, and your player rolls with more fore the closer to
 * the center of the meter you hit. 2nd phase, the remaining slimes go back and forth, and you try to hit as many as you
 * can
 */
public class SlimePopper extends AbstractMinigame {
    public static final String ASSET_PATH = "minigamesResources/img/games/slimePopper/sprites.atlas";
    public static final String BACKGROUND_PATH = "minigamesResources/img/games/slimePopper/background.png";
    public static final AssetManager assetManager = new AssetManager();
    public static TextureAtlas atlas;
    private static Texture background;


    private ArrayList<PopperItem> items;
    private final float minX = x - SIZE * 0.5f * Settings.scale;
    private final float maxX = x + SIZE * 0.5f * Settings.scale - PopperItem.SIZE;
    private final float minY = y - SIZE * 0.5f * Settings.scale;
    private final float maxY = y + SIZE * 0.5f * Settings.scale - PopperItem.SIZE;

    private float meterPercent = 0f;
    private boolean meterUp = false;

    private int popCount = 0;
    private PopperItem louse1;
    private PopperItem louse2;
    private PopperItem louse3;

    private final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("SlimePopper"));
    private final Map<String, String> dict = uiStrings.TEXT_DICT;
    private final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(makeID("SlimePopper"));

    public SlimePopper() {
        hasInstructionScreen = false;
        hasPostgameScreen = true;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!assetManager.isLoaded(ASSET_PATH)) {
            assetManager.load(ASSET_PATH, TextureAtlas.class);
            assetManager.finishLoadingAsset(ASSET_PATH);
        }
        if (!assetManager.isLoaded(BACKGROUND_PATH)) {
            assetManager.load(BACKGROUND_PATH, Texture.class);
            assetManager.finishLoadingAsset(BACKGROUND_PATH);
        }
        atlas = assetManager.get(ASSET_PATH, TextureAtlas.class);
        background = assetManager.get(BACKGROUND_PATH, Texture.class);
        items = new ArrayList<>();
    }

    @Override
    public String getOption() {
        return eventStrings.NAME;
    }

    @Override
    public void setupPostgameScreen(GenericEventDialog event) {
        GenericEventDialog.show();
        event.updateBodyText(eventStrings.DESCRIPTIONS[1]);
        event.setDialogOption(eventStrings.OPTIONS[1]);
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.combatRewardScreen.open();
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase) {
            case 0:
                phase = 1;
                setupBoard1();
                items.forEach(i -> i.update(elapsed));
                CardCrawlGame.sound.play("VO_CULTIST_1A");
                popCount = 0;
                break;
            case 1:
                items.forEach(i -> i.update(elapsed));
                bounceMeter(elapsed);
                break;
            case 2:
                items.forEach(i -> bounce(i, elapsed));
                collisionDetect();
                if (Math.abs(louse1.xVelocity) < 1f && Math.abs(louse1.yVelocity) < 1f
                        && Math.abs(louse2.xVelocity) < 1f && Math.abs(louse2.yVelocity) < 1f) {
                    phase = 3;
                }
                break;
            case 3:
                phase = 4;
                setupBoard2();
                items.forEach(i -> i.update(elapsed));
                CardCrawlGame.sound.play("VO_CULTIST_1A");
                break;
            case 4:
                items.stream().filter(i -> i.type == PopperItem.TYPE.SLIME).forEach(i -> bounce(i, elapsed));
                louse3.hb.move(MathUtils.clamp(InputHelper.mX, minX + PopperItem.SIZE, maxX), minY + PopperItem.SIZE * 2f);
                louse3.update(elapsed);
                break;
            case 5:
                items.stream().filter(i -> i.type == PopperItem.TYPE.SLIME).forEach(i -> bounce(i, elapsed));

                float nextY = louse3.hb.y + louse3.yVelocity * elapsed;
                if (nextY >= maxY) {
                    phase = 6;
                    items.remove(louse3);
                    louse3 = null;
                } else {
                    louse3.hb.moveY(nextY + PopperItem.SIZE / 2f);
                    louse3.update(elapsed);
                }
                collisionDetect();
                break;
            case 6:
                items.forEach(i -> bounce(i, elapsed));
                collisionDetect();
                if (items.stream().noneMatch(item -> item.isDying)) {
                    phase = 7;
                }
                break;
            case 7:
                break;
        }
    }

    private static final float METER_W = 32f * Settings.scale;
    private static final float METER_H = 128f * Settings.scale;
    private static final float NEEDLE_OFFSET = 16f * Settings.scale;

    @Override
    public void render(SpriteBatch sb) {
        if (phase >= 0) {
            // render background
            sb.setColor(Color.WHITE);
            drawTexture(sb, background, 0, 0, background.getWidth());
        }
        super.render(sb);
        if (phase == 1) {
            // render meter
            float x = maxX;
            float y = Interpolation.linear.apply(minY, minY + METER_H, meterPercent);
            TextureAtlas.AtlasRegion meter = atlas.findRegion("meter");
            TextureAtlas.AtlasRegion needle = atlas.findRegion("needle");
            sb.draw(meter, x, minY);
            sb.draw(needle, x - NEEDLE_OFFSET, y - NEEDLE_OFFSET);
        }
        items.forEach(i -> i.render(sb));
        if (phase == 1) {
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("POWER_UP"), x, y - SIZE / 4f, Color.GOLD);
        }
        if (phase == 4) {
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("AIM"), x, y - SIZE / 4f, Color.GOLD);
        }
        if (phase == 7) {
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("GAME_OVER"), x, y, Color.GOLD);
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, String.format(dict.get("SCORE"), popCount), x, y + 64f, Color.GOLD);
        } else if (phase > 1) {
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.smallDialogOptionFont, String.format(dict.get("COUNTER"), popCount), minX, minY - 32f, Color.GOLD);
        }
    }

    private void handleClick(Vector2 clickPos) {
        if (phase == 1) {
            float factor = Math.abs(meterPercent - 0.5f) * 2f;
            float roll = 45f + 45f * MathUtils.random(-factor, factor);
            float xFac = MathUtils.cosDeg(roll);
            float yFac = MathUtils.sinDeg(roll);
            louse1.xVelocity = 1000f + xFac * 500f - 800f * factor;
            louse1.yVelocity = 1000f + yFac * 500f - 800f * factor;
            louse1.setAnimation("louseRoll");
            louse2.xVelocity = -1000f - yFac * 500f + 800f * factor;
            louse2.yVelocity = 1000f + xFac * 500f - 800f * factor;
            louse2.setAnimation("louseRoll");
            CardCrawlGame.sound.play("BLUNT_FAST");
            phase = 2;
        } else if (phase == 4) {
            louse3.yVelocity = 800f;
            louse3.setAnimation("louseRoll");
            CardCrawlGame.sound.play("BLUNT_FAST");
            phase = 5;
        } else if (phase == 7) {
            isDone = true;
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            room.rewards.clear();
            room.addGoldToRewards(5 * popCount);
            if (popCount >= 10) {
                room.addPotionToRewards(AbstractDungeon.returnRandomPotion());
            }
            if (popCount >= 20) {
                AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                room.addRelicToRewards(r);
            }
        }
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), this::handleClick);
        return bindings;
    }

    private void setupBoard1() {
        items.clear();
        louse1 = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        louse1.hb.move(minX + PopperItem.SIZE * 1.5f, minY + PopperItem.SIZE / 2f);
        louse1.friction = true;
        items.add(louse1);

        louse2 = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        louse2.hb.move(maxX - PopperItem.SIZE, minY + PopperItem.SIZE / 2f);
        louse2.friction = true;
        items.add(louse2);

        Float[] col = new Float[7];
        Float[] row = new Float[7];
        IntStream.rangeClosed(-3, 3).forEachOrdered(i -> col[i + 3] = x + PopperItem.SIZE * 1.5f * i);
        IntStream.rangeClosed(-3, 3).forEachOrdered(i -> row[i + 3] = y + PopperItem.SIZE * 2f + PopperItem.SIZE * 1.5f * i);
        for (int i = 0; i < 7; i++) {
            int countForRow = 7 - Math.abs(6 - i * 2);
            int startCol = Math.abs(-3 + i);
            for (int j = 0; j < countForRow; j++) {
                PopperItem slime = new PopperItem(PopperItem.TYPE.SLIME, "slimeIdle");
                slime.hb.move(col[startCol + j], row[i]);
                items.add(slime);
            }
        }
    }

    private void setupBoard2() {
        items.clear();
        louse1 = louse2 = null;

        Float[] col = new Float[7];
        Float[] row = new Float[7];
        IntStream.rangeClosed(-3, 3).forEachOrdered(i -> col[i + 3] = x + PopperItem.SIZE * 1.5f * i);
        IntStream.rangeClosed(-3, 3).forEachOrdered(i -> row[i + 3] = y + PopperItem.SIZE * 2f + PopperItem.SIZE * 1.5f * i);
        for (int i = 0; i < 14; i++) {
            PopperItem slime = new PopperItem(PopperItem.TYPE.SLIME, "slimeIdle");
            slime.hb.move(col[i % 7], row[i % 7]);
            slime.yVelocity = 0f;
            slime.xVelocity = 75f + 25f * (i % 3) * (i % 2 == 0 ? -1 : 1);
            items.add(slime);
        }

        louse3 = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        louse3.hb.move(x, minY + PopperItem.SIZE / 2f);
        items.add(louse3);
    }

    private void bounce(PopperItem i, float elapsed) {
        float minX = (phase == 4) ? x - SIZE / 4f : this.minX;
        float maxX = (phase == 4) ? x + SIZE / 4f : this.maxX;
        float minY = (phase == 4) ? y - SIZE / 4f : this.minY;
        float maxY = (phase == 4) ? y + SIZE / 4f : this.maxY;
        float nextX = i.hb.x + i.xVelocity * elapsed;
        float nextY = i.hb.y + i.yVelocity * elapsed;
        if (nextX <= minX) {
            nextX = minX;
            i.xVelocity *= -1;
            if (i.type == PopperItem.TYPE.LOUSE) i.xVelocity += MathUtils.random(-20f, 20f);
        }
        if (nextX >= maxX) {
            nextX = maxX;
            i.xVelocity *= -1;
            if (i.type == PopperItem.TYPE.LOUSE) i.xVelocity += MathUtils.random(-20f, 20f);
        }
        if (nextY <= minY) {
            nextY = minY;
            i.yVelocity *= -1;
            if (i.type == PopperItem.TYPE.LOUSE) i.yVelocity += MathUtils.random(-20f, 20f);
        }
        if (nextY >= maxY) {
            nextY = maxY;
            i.yVelocity *= -1;
            if (i.type == PopperItem.TYPE.LOUSE) i.yVelocity += MathUtils.random(-20f, 20f);
        }
        i.hb.move(nextX + PopperItem.SIZE / 2f, nextY + PopperItem.SIZE / 2f);
        i.update(elapsed);
    }

    private static final float METER_DELTA = 2f;

    private void bounceMeter(float elapsed) {
        if (meterUp) {
            meterPercent -= elapsed * METER_DELTA;
        } else {
            meterPercent += elapsed * METER_DELTA;
        }
        if (meterPercent <= 0f) {
            meterUp = false;
            meterPercent = 0f;
        } else if (meterPercent >= 1f) {
            meterUp = true;
            meterPercent = 1f;
        }
    }

    private boolean near(PopperItem a, PopperItem b) {
        float dx = a.hb.cX - b.hb.cX;
        float dy = a.hb.cY - b.hb.cY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < 20;
    }

    private void collisionDetect() {
        items.removeIf(item -> {
            if (item.isDead) popCount += 1;
            return item.isDead;
        });
        items.stream().filter(item -> item.type == PopperItem.TYPE.SLIME && !item.isDying)
                .forEach(slime -> {
                    if (louse1 != null && near(louse1, slime)) {
                        slime.isDying = true;
                        slime.setAnimation("slimeDie");
                        louse1.xVelocity = louse1.xVelocity * 0.8f + MathUtils.random(-20f, 20f);
                        louse1.yVelocity = louse1.yVelocity * 0.8f + MathUtils.random(-20f, 20f);
                        CardCrawlGame.sound.play("MONSTER_SLIME_ATTACK");
                    } else if (louse2 != null && near(louse2, slime)) {
                        slime.isDying = true;
                        slime.setAnimation("slimeDie");
                        louse2.xVelocity = louse2.xVelocity * 0.8f + MathUtils.random(-20f, 20f);
                        louse2.yVelocity = louse2.yVelocity * 0.8f + MathUtils.random(-20f, 20f);
                        CardCrawlGame.sound.play("MONSTER_SLIME_ATTACK");
                    } else if (louse3 != null && near(louse3, slime)) {
                        slime.isDying = true;
                        slime.setAnimation("slimeDie");
                        CardCrawlGame.sound.play("MONSTER_SLIME_ATTACK");
                    }
                });
    }

    public AbstractMinigame makeCopy(){ return new SlimePopper(); }

}
