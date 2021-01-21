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
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;
import java.util.Map;
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
@SuppressWarnings("LibGDXStaticResource")
public class SlimePopper extends AbstractMinigame {
    public static final String ASSET_PATH = "minigamesResources/img/games/slimePopper/sprites.atlas";
    public static final String BACKGROUND_PATH = "minigamesResources/img/games/slimePopper/background.png";
    public static final AssetManager assetManager = new AssetManager();
    private static final float SLIMED_ATK_LENGTH = 1.64f;
    public static TextureAtlas atlas;
    private static Texture background;


    private ArrayList<PopperItem> items;
    private final float scale = Settings.scale;
    private final float SIZE = 512f;
    private final float HALF_SIZE = 256f;
    private final float GUTTER_SIZE = 64f;
    private final float HALF_GUTTER_SIZE = 32f;
    private final float SIZE_SCALED = SIZE * scale;
    private final float minX = x - SIZE_SCALED * 0.5f;
    private final float maxX = x + SIZE_SCALED * 0.5f;
    private final float minY = y - SIZE_SCALED * 0.5f;
    private final float maxY = y + SIZE_SCALED * 0.5f;

    private float meterPercent = 0f;
    private boolean meterUp = false;

    private int GOLD_MULTIPLIER;
    private int POTION_GOAL;
    private int popCount = 0;
    private int bowlingPopped = 0;
    private int lineupPopped = 0;
    private int bossPopped = 0;
    private AbstractPotion rewardPotion;
    AbstractRelic rewardRelic;

    private PopperItem bowlingLouse;
    private PopperItem previewLouse1;
    private PopperItem previewLouse2;
    private PopperItem previewLouse3;
    private PopperItem shootLouse;

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
        GOLD_MULTIPLIER = AbstractDungeon.ascensionLevel >= 15 ? 6 : 10;
        POTION_GOAL = AbstractDungeon.ascensionLevel >= 15 ? 7 : 5;
        rewardPotion = AbstractDungeon.returnRandomPotion();
        rewardRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
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

    private void drawScaledAndRotated(SpriteBatch sb, Texture texture, float cX, float cY, float scale, float rotation) {
        float w = texture.getWidth();
        float h = texture.getHeight();
        float halfW = w / 2f;
        float halfH = h / 2f;
        sb.draw(texture,
                cX - halfW,
                cY - halfH,
                halfW,
                halfH,
                w,
                h,
                scale * Settings.scale,
                scale * Settings.scale,
                rotation,
                0,
                0,
                (int) w,
                (int) h,
                false,
                false);
    }

    private void draw(SpriteBatch sb, Texture t, float cX, float cY, float angle, int baseWidth, int baseHeight, boolean flipX, boolean flipY) {
        sb.draw(t, x + cX - baseWidth / 2.0f, y + cY - baseHeight / 2.0f, -(cX - baseWidth / 2.0f), -(cY - baseHeight / 2.0f), baseWidth, baseHeight, scale, scale, this.angle + angle, 0, 0, baseWidth, baseHeight, flipX, flipY);
    }

    private void draw(SpriteBatch sb, TextureAtlas.AtlasRegion r, float cX, float cY) {
        float w = r.originalWidth;
        float h = r.originalHeight;
        float w2 = w / 2f;
        float h2 = h / 2f;
        sb.draw(r, x + cX - w2, y + cY - h2, w, h);
    }

    private void draw(SpriteBatch sb, TextureAtlas.AtlasRegion r, float cX, float cY, int size) {
        float w = (float) size;
        float h = (float) size;
        float w2 = w / 2f;
        float h2 = h / 2f;
        sb.draw(r, x + cX - w2, y + cY - h2, w, h);
    }

    private Color rainbow(float time, float a) {
        float r = MathUtils.sinDeg(time * 230f) / 2f + 0.5f;
        float g = MathUtils.sinDeg(time * 230f + 360f / 3f) / 2f + 0.5f;
        float b = MathUtils.sinDeg(time * 230f + 720f / 3f) / 2f + 0.5f;
        return new Color(r, g, b, a);
    }

    private void renderRewards(SpriteBatch sb) {
        Color orig = sb.getColor();
        Color faded = orig.cpy();
        faded.a *= 0.3;
        float x = -1 * (HALF_SIZE + GUTTER_SIZE / 4f) * Settings.scale;
        float y = (HALF_SIZE + GUTTER_SIZE / 4f) * Settings.scale;
        float spacer = 16f; // * Settings.scale;
        // gold reward
        if (phase > 1) {
            drawScaledAndRotated(sb, ImageMaster.TP_GOLD, this.x + x, this.y + y, scale, 0f);
            x += 2 * spacer;
            String goldDisplay = String.format("= %d", (phase == 2 ? popCount : bowlingPopped) * GOLD_MULTIPLIER);
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, goldDisplay, this.x + x, this.y + y, Color.GOLD);
            x += FontHelper.getWidth(FontHelper.tipBodyFont, goldDisplay, scale) + spacer * 2;
        } else if (phase >= 0) {
            sb.setColor(faded);
            drawScaledAndRotated(sb, ImageMaster.TP_GOLD, this.x + x, this.y + y, scale, 0f);
            x += 2 * spacer;
            String goldDisplay = String.format("= %d x", GOLD_MULTIPLIER);
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, goldDisplay, this.x + x, this.y + y, Color.GOLD);
            x += FontHelper.getWidth(FontHelper.tipBodyFont, goldDisplay, scale) + spacer;
            draw(sb, atlas.findRegion("slimeIdle", 0), x, y, (int)(32 * scale));
            x += spacer * 3;
            sb.setColor(orig);
        }

        // potion reward
        AbstractPotion p = rewardPotion.makeCopy();
        p.posX = this.x + x;
        p.posY = this.y + y;
        p.scale = 0.5f * scale;
        if (lineupPopped < POTION_GOAL) {
            p.renderOutline(sb, faded);
        } else {
            p.render(sb);
        }
        x += spacer * 3;

        // relic reward
        if (bossPopped < 7) {
            sb.setColor(faded);
        } else {
            sb.setColor(orig);
        }
        draw(sb, atlas.findRegion("bossIdle", 0), x, y, (int)(32 * scale));
        x += spacer;
        String equals = "=";
        FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, equals, this.x + x, this.y + y, Color.GOLD);
        x += FontHelper.getWidth(FontHelper.tipBodyFont, equals, Settings.scale) + spacer;
        AbstractRelic r = rewardRelic.makeCopy();
        r.currentX = this.x + x;
        r.currentY = this.y + y;
        r.scale = 0.5f * scale;
        r.render(sb);
        sb.setColor(orig);
    }

    private float tickUpGold = 0f;

    @Override
    public void render(SpriteBatch sb) {
        // render background
        sb.setColor(color);
        draw(sb, background, 0, 0, background.getWidth());
        renderRewards(sb);
        if (phase == 1) {
            // render meter
            float x = (HALF_SIZE + HALF_GUTTER_SIZE) * scale;
            float y = Interpolation.linear.apply(-x, -x + METER_H, meterPercent);
            TextureAtlas.AtlasRegion meter = atlas.findRegion("meter");
            TextureAtlas.AtlasRegion needle = atlas.findRegion("needle");
            draw(sb, meter, x + HALF_GUTTER_SIZE / 2f, -(x) + METER_H / 2f);
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
            float yOff = GUTTER_SIZE * scale;
            float spacer = 16f * scale;
            if (bossPopped < 7) {
                FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("GAME_OVER"), x, y + yOff, Color.GOLD);
            } else {
                FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("YOU_WIN"), x, y + yOff, Color.GOLD);
            }
            yOff -= spacer * 2;
            if (tickUpGold < bowlingPopped * GOLD_MULTIPLIER - 3) {
                tickUpGold = Interpolation.linear.apply(tickUpGold, bowlingPopped * GOLD_MULTIPLIER, time * 3f);
                time = 0f;
            } else {
                tickUpGold = bowlingPopped * GOLD_MULTIPLIER;
            }
            String goldText = String.format(dict.get("GOLD"), MathUtils.round(tickUpGold));
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, goldText, x, y + yOff, Color.GOLD);
            yOff -= spacer * 2;
            Color faded = Color.GOLD.cpy();

            // potion fade
            if (lineupPopped < POTION_GOAL) {
                faded = Color.DARK_GRAY.cpy();
            }
            faded.a = Interpolation.fade.apply(MathUtils.clamp(time / 0.8f, 0f, 1f));
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("POTION"), x, y + yOff, faded);
            yOff -= spacer * 2;

            // relic fade
            if (bossPopped < 7) {
                faded = Color.DARK_GRAY.cpy();
                faded.a = Interpolation.fade.apply(MathUtils.clamp(time / 0.8f - 2f, 0f, 1f));
            } else {
                faded = rainbow(time, Interpolation.fade.apply(MathUtils.clamp(time / 0.8f - 2f, 0f, 1f)));
            }
            sb.setColor(faded);
            String relicText = String.format(dict.get("RELIC"), rewardRelic.name);
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, relicText, x, y + yOff, faded);
        } else if (phase > 1) {
            FontHelper.renderFontLeftTopAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    String.format(dict.get("COUNTER"), popCount),
                    x - (HALF_SIZE + GUTTER_SIZE / 2f) * scale,
                    y - (HALF_SIZE + GUTTER_SIZE / 2f) * scale,
                    Color.GOLD);
/*
            FontHelper.renderFontLeftTopAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    "X",
                    minX,
                    minY,
                    Color.GOLD);
            FontHelper.renderFontLeftDownAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    "X",
                    maxX,
                    maxY,
                    Color.GOLD);
*/
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
                //CardCrawlGame.sound.play("VO_CULTIST_1A");
                popCount = 0;
                break;
            case 1:
                bowlingLouse.hb.moveX(MathUtils.clamp(InputHelper.mX, minX + PopperItem.SIZE, maxX));
                float previewAngle = MathUtils.atan2(previewLouse1.hb.cY - bowlingLouse.hb.cY, previewLouse1.hb.cX - bowlingLouse.hb.cX);
                float offsetX = 64f * scale * MathUtils.cos(previewAngle);
                previewLouse2.hb.moveX(previewLouse1.hb.cX + offsetX);
                previewLouse3.hb.moveX(previewLouse1.hb.cX - offsetX);
                items.forEach(i -> i.update(elapsed));
                bounceMeter(elapsed);
                break;
            case 2:
                items.forEach(i -> bounce(i, elapsed));
                collisionDetect();
                if (Math.abs(bowlingLouse.xVelocity) < 3f && Math.abs(bowlingLouse.yVelocity) < 3f) {
                    phase = 3;
                }
                break;
            case 3:
                phase = 4;
                bowlingPopped = popCount;
                popCount = 0;
                setupBoard2();
                items.forEach(i -> i.update(elapsed));
                //CardCrawlGame.sound.play("VO_CULTIST_1A");
                break;
            case 4:
                items.stream().filter(i -> i.type == PopperItem.TYPE.SLIME).forEach(i -> bounce(i, elapsed));
                shootLouse.hb.move(MathUtils.clamp(InputHelper.mX, minX + PopperItem.SIZE, maxX), minY);
                shootLouse.update(elapsed);
                break;
            case 5:
                items.stream().filter(i -> i.type == PopperItem.TYPE.SLIME).forEach(i -> bounce(i, elapsed));

                nextY = shootLouse.hb.cY + shootLouse.yVelocity * elapsed;
                if (nextY + PopperItem.SIZE / 2f >= maxY && !shootLouse.isDying) {
                    shootLouse.friction = true;
                    shootLouse.yVelocity *= -1;
                } else if ((nextY <= minY || shootLouse.yVelocity > 3) && shootLouse.friction) {
                    phase = 6;
                    items.remove(shootLouse);
                    shootLouse = null;
                } else {
                    shootLouse.hb.moveY(nextY);
                    shootLouse.update(elapsed);
                }
                collisionDetect();
                break;
            case 6:
                items.forEach(i -> bounce(i, elapsed));
                collisionDetect();
                if (items.stream().noneMatch(item -> item.isDying)) {
                    phase = 7;
                    lineupPopped = popCount;
                    popCount = 0;
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
                if (shootLouse.isDying) {
                    nextY = shootLouse.hb.cY + shootLouse.yVelocity * elapsed;
                    if (nextY + PopperItem.SIZE / 2f >= maxY) {
                        shootLouse.isDying = false;
                    } else {
                        shootLouse.hb.moveY(nextY);
                    }
                } else {
                    shootLouse.hb.move(MathUtils.clamp(InputHelper.mX, minX + PopperItem.SIZE, maxX), minY);
                }
                items.stream().filter(i -> i.type != PopperItem.TYPE.LOUSE).forEach(i -> bounce(i, elapsed));
                items.forEach(i -> i.update(elapsed));
                collisionDetectBoss(elapsed);
                collisionDetect();
                if (
                        items.stream().anyMatch(i -> i.type != PopperItem.TYPE.LOUSE && i.hb.y <= minY) ||
                                items.stream().noneMatch(i -> i.type != PopperItem.TYPE.LOUSE && !i.isDead)
                ) {
                    items.clear();
                    phase = 99;
                    time = 0;
                }
                break;
            case 99:
                time += elapsed;
                break;
        }
        if(endClicked) {
            isDone = true;
        }
        slimeSoundtimer -= elapsed;
    }

    private boolean endClicked = false;
    private void handleClick(Vector2 clickPos) {
        if (phase == 1) {
            float factor = Interpolation.pow2In.apply(1 - 2f * Math.abs(meterPercent - 0.5f));
                    //1 - Math.abs(meterPercent - 0.5f) * 2f;
            float angle = MathUtils.atan2(previewLouse1.hb.cY - bowlingLouse.hb.cY, previewLouse1.hb.cX - bowlingLouse.hb.cX);
            float xFac = MathUtils.cos(angle);
            float yFac = MathUtils.sin(angle);
            float velocity = 200f + 4000f * factor;
            bowlingLouse.xVelocity = velocity * xFac;
            bowlingLouse.yVelocity = velocity * yFac;
            bowlingLouse.setAnimation("louseRoll");
            items.remove(previewLouse1);
            items.remove(previewLouse2);
            items.remove(previewLouse3);
            previewLouse1 = null;
            previewLouse2 = null;
            previewLouse3 = null;
            CardCrawlGame.sound.play("BLUNT_FAST");
            phase = 2;
        } else if (phase == 4) {
            shootLouse.yVelocity = 800f;
            shootLouse.setAnimation("louseRoll");
            CardCrawlGame.sound.play("BLUNT_FAST");
            phase = 5;
        } else if (phase == 8) {
            if (!shootLouse.isDying) {
                shootLouse.yVelocity = 800f;
                shootLouse.setAnimation("louseRoll");
                shootLouse.isDying = true;
            }
        } else if (phase == 99) {
            if(!endClicked) {
                endClicked = true;
                AbstractRoom room = AbstractDungeon.getCurrRoom();
                room.rewards.clear();
                room.addGoldToRewards(bowlingPopped * GOLD_MULTIPLIER);
                if (lineupPopped >= POTION_GOAL) {
                    room.addPotionToRewards(rewardPotion);
                }
                if (bossPopped >= 7) {
                    room.addRelicToRewards(rewardRelic);
                }
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
        bowlingLouse = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        bowlingLouse.hb.move(x, y - (HALF_SIZE) * scale);
        bowlingLouse.friction = true;
        items.add(bowlingLouse);

        previewLouse1 = new PopperItem(PopperItem.TYPE.LOUSE, "louseRoll");
        previewLouse1.hb.move(x, y - 128f * scale);
        previewLouse1.isPreview = true;
        items.add(previewLouse1);

        previewLouse2 = new PopperItem(PopperItem.TYPE.LOUSE, "louseRoll");
        previewLouse2.hb.move(x, y - 96f * scale);
        previewLouse2.isPreview = true;
        items.add(previewLouse2);

        previewLouse3 = new PopperItem(PopperItem.TYPE.LOUSE, "louseRoll");
        previewLouse3.hb.move(x, y - 160f * scale);
        previewLouse3.isPreview = true;
        items.add(previewLouse3);

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
        bowlingLouse = previewLouse1 = null;

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

        shootLouse = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        shootLouse.hb.move(x, minY + PopperItem.SIZE);
        items.add(shootLouse);
    }

    private void setupBoard3() {
        items.clear();
        shootLouse = new PopperItem(PopperItem.TYPE.LOUSE, "louseIdle");
        shootLouse.hb.move(x, minY + PopperItem.SIZE);
        items.add(shootLouse);

        PopperBoss popperBoss = new PopperBoss();
        popperBoss.hb.move(x, y + 128f);
        popperBoss.yVelocity = -10f;
        popperBoss.xVelocity = 50f * MathUtils.randomSign();
        items.add(popperBoss);
    }

    private void bounce(PopperItem i, float elapsed) {
        float minX = (phase == 4) ? x - SIZE_SCALED / 4f : this.minX;
        float maxX = (phase == 4) ? x + SIZE_SCALED / 4f : this.maxX;
        float minY = (phase == 4) ? y - SIZE_SCALED / 4f : this.minY;
        float maxY = (phase == 4) ? y + SIZE_SCALED / 4f : this.maxY;
        float nextX = i.hb.cX + i.xVelocity * elapsed;
        float nextY = i.hb.cY + i.yVelocity * elapsed;
        float w2 = i.hb.width / 2f;
        float h2 = i.hb.height / 2f;
        if (nextX - w2 <= minX) {
            nextX = minX + w2;
            i.xVelocity *= -1;
            if (i.friction) i.xVelocity += MathUtils.random(-20f, 20f);
        }
        if (nextX + w2 >= maxX) {
            nextX = maxX - w2;
            i.xVelocity *= -1;
            if (i.friction) i.xVelocity += MathUtils.random(-20f, 20f);
        }
        if (nextY - h2 <= minY) {
            nextY = minY + h2;
            i.yVelocity *= -1;
            if (i.friction) i.yVelocity += MathUtils.random(-20f, 20f);
        }
        if (nextY + h2 >= maxY) {
            nextY = maxY - h2;
            i.yVelocity *= -1;
            if (i.friction) i.yVelocity += MathUtils.random(-20f, 20f);
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
                .forEach(slime -> Stream.of(bowlingLouse, previewLouse1, shootLouse)
                        .forEachOrdered(louse -> {
                            if (louse != null && near(louse, slime)) {
                                slime.startDeath();
                            }
                        }));
    }

    private void checkAndSplitBoss(float elapsed) {
        ArrayList<PopperItem> toAdd = new ArrayList<>();
        items.stream().filter(item -> item.type == PopperItem.TYPE.BOSS && item.isDead)
                .map(item -> ((PopperBoss) item))
                .forEach(boss -> {
                    PopperMed med1 = new PopperMed();
                    med1.hb.move(boss.hb.cX - boss.hb.width / 2f, boss.hb.cY);
                    med1.xVelocity = -boss.xVelocity;
                    med1.yVelocity = boss.yVelocity - 5f;
                    med1.update(elapsed);
                    toAdd.add(med1);

                    PopperMed med2 = new PopperMed();
                    med2.hb.move(boss.hb.cX + boss.hb.width / 2f, boss.hb.cY);
                    med2.xVelocity = boss.xVelocity;
                    med2.yVelocity = boss.yVelocity - 5f;
                    med2.update(elapsed);
                    toAdd.add(med2);
                });
        items.addAll(toAdd);
    }

    private void checkAndSplitMed(float elapsed) {
        ArrayList<PopperItem> toAdd = new ArrayList<>();
        items.stream().filter(item -> item.type == PopperItem.TYPE.MED && item.isDead)
                .map(item -> ((PopperMed) item))
                .forEach(med -> {
                    PopperItem sm1 = new PopperItem(PopperItem.TYPE.SLIME, "slimeIdle");
                    sm1.hb.move(med.hb.cX - med.hb.width / 2f, med.hb.cY);
                    sm1.xVelocity = -med.xVelocity;
                    sm1.yVelocity = med.yVelocity - 5f;
                    sm1.update(elapsed);
                    toAdd.add(sm1);

                    PopperItem sm2 = new PopperItem(PopperItem.TYPE.SLIME, "slimeIdle");
                    sm2.hb.move(med.hb.cX + med.hb.width / 2f, med.hb.cY);
                    sm2.xVelocity = med.xVelocity;
                    sm2.yVelocity = med.yVelocity - 5f;
                    toAdd.add(sm2);
                    sm2.update(elapsed);
                });
        items.addAll(toAdd);
    }

    private void collisionDetectBoss(float elapsed) {
        checkAndSplitBoss(elapsed);
        checkAndSplitMed(elapsed);
        items.removeIf(item -> {
            if (item.isDead) bossPopped += 1;
            return item.isDead;
        });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.BOSS && !item.isDying)
                .map(item -> ((PopperBoss) item))
                .forEach(boss -> {
                    if (shootLouse != null && shootLouse.isDying && near(shootLouse, boss)) {
                        boss.dealDamage(shootLouse);
                        shootLouse.isDying = false;
                    }
                });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.MED && !item.isDying)
                .map(item -> ((PopperMed) item))
                .forEach(med -> {
                    if (shootLouse != null && shootLouse.isDying && near(shootLouse, med)) {
                        med.dealDamage(shootLouse);
                        shootLouse.isDying = false;
                    }
                });
    }

    private static float slimeSoundtimer = 0;
    public static void playSlimeSoundRegulated() {
        if(slimeSoundtimer <= 0) {
            slimeSoundtimer = SLIMED_ATK_LENGTH;
            CardCrawlGame.sound.play("MONSTER_SLIME_ATTACK");
        } else {
            slimeSoundtimer /= 2f;
        }
    }

    public AbstractMinigame makeCopy() {
        return new SlimePopper();
    }

}
