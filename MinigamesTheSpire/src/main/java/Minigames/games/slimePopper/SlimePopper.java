package Minigames.games.slimePopper;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
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
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    private final float scale = Settings.scale;
    private final float SIZE = 512f;
    private final float HALF_SIZE = 256f;
    private final float GUTTER_SIZE = 64f;
    private final float HALF_GUTTER_SIZE = 32f;
    private final float SIZE_SCALED = 512 * scale;
    private final float minX = x - SIZE_SCALED * 0.5f;
    private final float maxX = x + SIZE_SCALED * 0.5f - PopperItem.SIZE;
    private final float minY = y - SIZE_SCALED * 0.5f;
    private final float maxY = y + SIZE_SCALED * 0.5f - PopperItem.SIZE;

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

    private final Color color = new Color(0xffffff00);
    private final float fadeTime = Settings.FAST_MODE ? 0.7f : 2f;
    private float fadeTimeElapsed = 0f;

    private static final float METER_H = 128f * Settings.scale;

    //These methods draw/scale/rotate whatever they are passed based on the scale/position/angle of the minigame.
    private void draw(SpriteBatch sb, Texture t, float cX, float cY, int size) {
        draw(sb, t, cX, cY, 0, size, size, false, false);
    }

    private void draw(SpriteBatch sb, Texture t, float cX, float cY, float angle, int baseWidth, int baseHeight, boolean flipX, boolean flipY) {
        sb.draw(t, x + cX - baseWidth / 2.0f, y + cY - baseHeight / 2.0f, -(cX - baseWidth / 2.0f), -(cY - baseHeight / 2.0f), baseWidth, baseHeight, scale, scale, this.angle + angle, 0, 0, baseWidth, baseHeight, flipX, flipY);
    }

    private void draw(SpriteBatch sb, TextureAtlas.AtlasRegion r, float cX, float cY) {
        float w = r.originalWidth;
        float h = r.originalHeight;
        float w2 = w / 2f;
        float h2 = h / 2f;
        sb.draw(r, x + cX - w2, y + cY - h2, -(cX - w2), -(cY - h2), w, h, scale, scale, 0f);
    }

    @Override
    public void render(SpriteBatch sb) {
        // render background
        sb.setColor(color);
        draw(sb, background, 0, 0, background.getWidth());
        if (phase == 1) {
            // render meter
            float x = (HALF_SIZE + HALF_GUTTER_SIZE) * scale;
            float y = Interpolation.linear.apply(-x, -x + METER_H, meterPercent);
            TextureAtlas.AtlasRegion meter = atlas.findRegion("meter");
            TextureAtlas.AtlasRegion needle = atlas.findRegion("needle");
            draw(sb, meter, x + HALF_GUTTER_SIZE / 2f, -(HALF_SIZE + HALF_GUTTER_SIZE) + METER_H / 2f);
            draw(sb, needle, x, y);
        }
        items.forEach(i -> i.render(sb));
        if (phase == 1) {
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("POWER_UP"), x, y - SIZE_SCALED / 4f, Color.GOLD);
        }
        if (phase == 4) {
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("AIM"), x, y - SIZE_SCALED / 4f, Color.GOLD);
        }
        if (phase == 99) {
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("GAME_OVER"), x, y, Color.GOLD);
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, String.format(dict.get("SCORE"), popCount), x, y + 64f, Color.GOLD);
        } else if (phase > 1) {
            //TODO: render rewards in left gutter
            FontHelper.renderFontLeftTopAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    String.format(dict.get("COUNTER"), popCount),
                    x - (HALF_SIZE + GUTTER_SIZE / 2f) * scale,
                    y - (HALF_SIZE + GUTTER_SIZE / 2f) * scale,
                    Color.GOLD);
            FontHelper.renderFontLeftTopAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    "W",
                    minX,
                    minY,
                    Color.GOLD);
            FontHelper.renderFontLeftDownAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    "X",
                    maxX,
                    maxY,
                    Color.GOLD);
        }
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        float nextY;
        switch (phase) {
            case -2: //fallthrough
            case -1:
                fadeTimeElapsed += elapsed;
                color.a = Interpolation.fade.apply(fadeTimeElapsed / fadeTime);
            case 0:
                color.a = 1f;
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

                nextY = louse3.hb.cY + louse3.yVelocity * elapsed;
                if (nextY + PopperItem.SIZE / 2f >= maxY) {
                    phase = 6;
                    items.remove(louse3);
                    louse3 = null;
                } else {
                    louse3.hb.moveY(nextY);
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
                phase = 8;
                setupBoard3();
                time = 0;
                items.forEach(i -> i.update(elapsed));
                CardCrawlGame.sound.play("DARKLING_REGROW_1");
                break;
            case 8:
                if (louse3.isDying) {
                    nextY = louse3.hb.cY + louse3.yVelocity * elapsed;
                    if (nextY + PopperItem.SIZE / 2f >= maxY) {
                        louse3.isDying = false;
                    } else {
                        louse3.hb.moveY(nextY);
                    }
                } else {
                    louse3.hb.move(MathUtils.clamp(InputHelper.mX, minX + PopperItem.SIZE, maxX), minY + PopperItem.SIZE * 2f);
                }
                items.forEach(i -> i.update(elapsed));
                collisionDetectBoss();
                break;
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
        } else if (phase == 8) {
            louse3.yVelocity = 800f;
            louse3.setAnimation("louseRoll");
            louse3.isDying = true;
            CardCrawlGame.sound.play("BLUNT_FAST");
        } else if (phase == 99) {
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
        louse1.hb.move(x - (256f - 64f) * scale, y - (256f - 64f) * scale);
        louse1.friction = true;
        items.add(louse1);

        louse2 = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        louse2.hb.move(x + (256f - 64f) * scale, y - (256f - 64f) * scale);
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
        louse3.hb.move(x, minY + PopperItem.SIZE);
        items.add(louse3);
    }

    private void setupBoard3() {
        items.clear();
        louse3 = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        louse3.hb.move(x, minY + PopperItem.SIZE);
        items.add(louse3);

        PopperBoss popperBoss = new PopperBoss("bossIdle");
        popperBoss.hb.move(x, y + 128f);
        popperBoss.yVelocity = -10f;
        popperBoss.xVelocity = 50f * MathUtils.randomSign();
        items.add(popperBoss);
    }

    private void bounce(PopperItem i, float elapsed) {
        float minX = (phase == 4) ? x - SIZE_SCALED / 4f : this.minX + PopperItem.SIZE / 2f;
        float maxX = (phase == 4) ? x + SIZE_SCALED / 4f : this.maxX - PopperItem.SIZE / 2f;
        float minY = (phase == 4) ? y - SIZE_SCALED / 4f : this.minY + PopperItem.SIZE / 2f;
        float maxY = (phase == 4) ? y + SIZE_SCALED / 4f : this.maxY - PopperItem.SIZE / 2f;
        float nextX = i.hb.cX + i.xVelocity * elapsed;
        float nextY = i.hb.cY + i.yVelocity * elapsed;
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
        i.hb.move(nextX, nextY);
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
        return a.hb.intersects(b.hb);
    }

    private void collisionDetect() {
        items.removeIf(item -> {
            if (item.isDead) popCount += 1;
            return item.isDead;
        });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.SLIME && !item.isDying)
                .forEach(slime -> {
                    Stream.of(louse1, louse2, louse3)
                            .forEachOrdered(louse -> {
                                if (louse != null && near(louse, slime)) {
                                    slime.startDeath();
                                    if (louse.friction) {
                                        louse.xVelocity = louse.xVelocity * 0.8f + MathUtils.random(-30f, 30f);
                                        louse.yVelocity = louse.yVelocity * 0.8f + MathUtils.random(-30f, 30f);
                                    }
                                }
                            });
                });
    }

    private void collisionDetectBoss() {
        items.removeIf(item -> {
            if (item.isDead) popCount += 1;
            return item.isDead;
        });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.BOSS && !item.isDying)
                .map(item -> ((PopperBoss)item))
                .forEach(boss -> {
                        if (louse3 != null && louse3.isDying && near(louse3, boss)) {
                            boss.dealDamage(louse3);
                            louse3.isDying = false;
                        }
                    });
    }

    public AbstractMinigame makeCopy() {
        return new SlimePopper();
    }

}
