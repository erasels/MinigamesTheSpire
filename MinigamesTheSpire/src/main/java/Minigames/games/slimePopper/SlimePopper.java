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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static Minigames.Minigames.makeID;

/**
 * Slime Popper.
 * <p>
 *     Shoot down slimes in an old-school arcade shooter
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
    private final float SIZE_SCALED = SIZE * scale;
    private final float minX = x - SIZE_SCALED * 0.5f;
    private final float maxX = x + SIZE_SCALED * 0.5f;
    private final float minY = y - SIZE_SCALED * 0.5f;
    private final float maxY = y + SIZE_SCALED * 0.5f;

    private int GOLD_MULTIPLIER;
    private boolean POTION_EARNED;
    private int popCount = 0;
    private boolean bossPopped = false;
    private AbstractPotion rewardPotion;
    AbstractRelic rewardRelic;

    private PopperLouse shootLouse;

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
        POTION_EARNED = false;
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
        if (phase > 0) {
            drawScaledAndRotated(sb, ImageMaster.TP_GOLD, this.x + x, this.y + y, scale, 0f);
            x += 2 * spacer;
            String goldDisplay = String.format("= %d", popCount * GOLD_MULTIPLIER);
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, goldDisplay, this.x + x, this.y + y, Color.GOLD);
            x += FontHelper.getWidth(FontHelper.tipBodyFont, goldDisplay, scale) + spacer * 2;
        } else if (phase >= 0) {
            sb.setColor(faded);
            drawScaledAndRotated(sb, ImageMaster.TP_GOLD, this.x + x, this.y + y, scale, 0f);
            x += 2 * spacer;
            String goldDisplay = String.format("= %d x", GOLD_MULTIPLIER);
            FontHelper.renderFontLeft(sb, FontHelper.tipBodyFont, goldDisplay, this.x + x, this.y + y, Color.GOLD);
            x += FontHelper.getWidth(FontHelper.tipBodyFont, goldDisplay, scale) + spacer;
            draw(sb, atlas.findRegion("slimeIdle", 0), x, y, (int) (32 * scale));
            x += spacer * 3;
            sb.setColor(orig);
        }

        // potion reward
        AbstractPotion p = rewardPotion.makeCopy();
        p.posX = this.x + x;
        p.posY = this.y + y;
        p.scale = 0.5f * scale;
        if (POTION_EARNED) {
            p.render(sb);
        } else {
            p.renderOutline(sb, faded);
        }
        x += spacer * 3;

        // relic reward
        if (bossPopped && bossSlimes.isEmpty()) {
            sb.setColor(orig);
        } else {
            sb.setColor(faded);
        }
        draw(sb, atlas.findRegion("bossIdle", 0), x, y, (int) (32 * scale));
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
        items.forEach(i -> i.render(sb));
        if (phase == 99) {
            float yOff = GUTTER_SIZE * scale;
            float spacer = 16f * scale;
            if (bossPopped && bossSlimes.isEmpty()) {
                FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("YOU_WIN"), x, y + yOff, Color.GOLD);
            } else {
                FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, dict.get("GAME_OVER"), x, y + yOff, Color.GOLD);
            }
            yOff -= spacer * 2;
            if (tickUpGold < popCount * GOLD_MULTIPLIER - 3) {
                tickUpGold = Interpolation.linear.apply(tickUpGold, popCount * GOLD_MULTIPLIER, time * 3f);
                time = 0f;
            } else {
                tickUpGold = popCount * GOLD_MULTIPLIER;
            }
            String goldText = String.format(dict.get("GOLD"), MathUtils.round(tickUpGold));
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, goldText, x, y + yOff, Color.GOLD);
            yOff -= spacer * 2;

            // potion fade
            Color faded;
            String potionText;
            if (POTION_EARNED) {
                faded = Color.GOLD.cpy();
                potionText = dict.get("POTION");
            } else {
                faded = Color.DARK_GRAY.cpy();
                potionText = dict.get("NO_POTION");
            }
            faded.a = Interpolation.fade.apply(MathUtils.clamp(time / 0.8f, 0f, 1f));
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, potionText, x, y + yOff, faded);
            yOff -= spacer * 2;

            // relic fade
            if (bossPopped && bossSlimes.isEmpty()) {
                faded = rainbow(time, Interpolation.fade.apply(MathUtils.clamp(time / 0.8f - 2f, 0f, 1f)));
            } else {
                faded = Color.DARK_GRAY.cpy();
                faded.a = Interpolation.fade.apply(MathUtils.clamp(time / 0.8f - 2f, 0f, 1f));
            }
            sb.setColor(faded);
            String relicText = String.format(dict.get("RELIC"), rewardRelic.name);
            FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, relicText, x, y + yOff, faded);
        } else if (phase >= 1) {
            FontHelper.renderFontLeftTopAligned(sb,
                    FontHelper.smallDialogOptionFont,
                    String.format(dict.get("COUNTER"), popCount),
                    x - (HALF_SIZE + GUTTER_SIZE / 2f) * scale,
                    y - (HALF_SIZE + GUTTER_SIZE / 2f) * scale,
                    Color.GOLD);
        }
    }

    private final List<SlimeSpawnTimer> spawnTimers = new ArrayList<>();
    private void updateSpawnTimers(float elapsed) {
        spawnTimers.forEach(slimeSpawnTimer -> slimeSpawnTimer.update(elapsed));
        spawnTimers.removeIf(slimeSpawnTimer -> slimeSpawnTimer.isDone);
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        float nextX;
        float nextY;
        switch (phase) {
            case -2: //fallthrough
            case -1:
                fadeTimeElapsed += elapsed;
                color.a = Interpolation.fade.apply(fadeTimeElapsed / fadeTime);
            case 0:
                color.a = 1f;
                phase = 1;
                setupBoard();
                items.forEach(i -> i.update(elapsed));
                popCount = 0;
                break;
            case 1:
                updateSpawnTimers(elapsed);
                if (shootLouse.isFiring) {
                    nextY = shootLouse.hb.cY + shootLouse.yVelocity * elapsed;
                    if (nextY + PopperItem.SIZE / 2f >= maxY) {
                        shootLouse.startReset();
                        if (shootLouse.yVelocity > 0) {
                            shootLouse.yVelocity *= -1;
                        }
                    } else {
                        shootLouse.hb.moveY(nextY);
                    }
                } else if (shootLouse.isResetting) {
                    nextY = shootLouse.hb.cY + shootLouse.yVelocity * elapsed;
                    nextX = shootLouse.hb.cX + shootLouse.xVelocity * elapsed;
                    if (nextX <= minX) {
                        nextX = minX;
                        shootLouse.xVelocity *= -1;
                    } else if (nextX >= maxX) {
                        nextX = maxX;
                        shootLouse.xVelocity *= -1;
                    }
                    shootLouse.hb.move(nextX, nextY);
                } else {
                    shootLouse.hb.move(MathUtils.clamp(InputHelper.mX, minX + PopperItem.SIZE, maxX), minY);
                }
                items.stream().filter(i -> i.type != PopperItem.TYPE.LOUSE).forEach(i -> bounce(i, elapsed));
                items.forEach(i -> i.update(elapsed));
                collisionDetect(elapsed);
                if (
                        items.stream().noneMatch(i -> i.type != PopperItem.TYPE.LOUSE && !i.isDead) &&
                                spawnTimers.stream().allMatch(slimeSpawnTimer -> slimeSpawnTimer.isDone)

                )
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
        if (endClicked) {
            isDone = true;
        }
        slimeSoundtimer -= elapsed;
    }

    private boolean endClicked = false;

    private void handleClick(Vector2 clickPos) {
        if (phase == 1) {
            if (!shootLouse.isFiring) {
                shootLouse.yVelocity = 800f;
                shootLouse.setAnimation("louseRoll");
                shootLouse.isFiring = true;
            }
        } else if (phase == 99) {
            if (!endClicked && time > 0.5f) {
                endClicked = true;
                AbstractRoom room = AbstractDungeon.getCurrRoom();
                room.rewards.clear();
                room.addGoldToRewards(popCount * GOLD_MULTIPLIER);
                if (POTION_EARNED) {
                    room.addPotionToRewards(rewardPotion);
                }
                if (bossPopped && bossSlimes.isEmpty()) {
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
            nextY = minY;
            i.isDead = true;
        }
        if (nextY + h2 >= maxY) {
            nextY = maxY - h2;
            i.yVelocity *= -1;
            if (i.friction) i.yVelocity += MathUtils.random(-20f, 20f);
        }
        i.hb.move(nextX, nextY);
        i.update(elapsed);
    }

    private boolean near(PopperItem a, PopperItem b) {
        return a.hb.intersects(b.hb);
    }

    private List<PopperItem> bossSlimes = new ArrayList<>();

    private void setupBoard() {
        items.clear();
        Collections.sort(items);
        Consumer<PopperItem> slimeSpawned = item -> {
            float xPos = MathUtils.random(minX, maxX);
            item.hb.move(xPos, maxY);
            items.add(item);
        };
        IntStream.rangeClosed(0, 5).forEachOrdered(i -> {
            SlimeSpawnTimer timer = new SlimeSpawnTimer(SlimeSpawnTimer.TIMER_TYPE.SLIME, 1.86f * i, slimeSpawned);
            spawnTimers.add(timer);
        });
        spawnTimers.add(new SlimeSpawnTimer(SlimeSpawnTimer.TIMER_TYPE.MED_SLIME, slimeSpawned));

        Consumer<PopperItem> bossSpawned = item -> {
            item.hb.move((maxX - minX) / 2f, maxY);
            items.add(item);
            bossSlimes.add(item);
        };
        spawnTimers.add(new SlimeSpawnTimer(SlimeSpawnTimer.TIMER_TYPE.BOSS, slimeSpawned));
        spawnTimers.add(new SlimeSpawnTimer(SlimeSpawnTimer.TIMER_TYPE.POTION, slimeSpawned));

        shootLouse = new PopperLouse("louseIdle");
        shootLouse.hb.move(x, minY + PopperItem.SIZE);
        items.add(shootLouse);
    }

    private void checkAndSplitBoss(float elapsed) {
        ArrayList<PopperItem> toAdd = new ArrayList<>();
        items.stream().filter(item -> item.type == PopperItem.TYPE.BOSS && item.isDead)
                .map(item -> ((PopperBoss) item))
                .forEach(boss -> {
                    bossPopped = true;
                    bossSlimes.remove(boss);
                    PopperMed med1 = new PopperMed();
                    med1.hb.move(boss.hb.cX - boss.hb.width / 2f, boss.hb.cY);
                    med1.xVelocity = -boss.xVelocity;
                    med1.yVelocity = boss.yVelocity - 15f;
                    med1.update(elapsed);
                    toAdd.add(med1);
                    bossSlimes.add(med1);

                    PopperMed med2 = new PopperMed();
                    med2.hb.move(boss.hb.cX + boss.hb.width / 2f, boss.hb.cY);
                    med2.xVelocity = boss.xVelocity;
                    med2.yVelocity = boss.yVelocity - 12f;
                    med2.update(elapsed);
                    toAdd.add(med2);
                    bossSlimes.add(med2);
                });
        items.addAll(toAdd);
    }

    private void checkAndSplitMed(float elapsed) {
        ArrayList<PopperItem> toAdd = new ArrayList<>();
        items.stream().filter(item -> item.type == PopperItem.TYPE.MED && item.isDead)
                .map(item -> ((PopperMed) item))
                .forEach(med -> {
                    boolean isBoss = bossSlimes.contains(med);
                    if (isBoss) {
                        bossSlimes.remove(med);
                    }
                    PopperItem sm1 = new PopperSlime();
                    sm1.hb.move(med.hb.cX - med.hb.width / 2f, med.hb.cY);
                    sm1.xVelocity = -med.xVelocity;
                    sm1.yVelocity = med.yVelocity - 10f;
                    sm1.update(elapsed);
                    toAdd.add(sm1);
                    if (isBoss) {
                        bossSlimes.add(sm1);
                    }

                    PopperItem sm2 = new PopperSlime();
                    sm2.hb.move(med.hb.cX + med.hb.width / 2f, med.hb.cY);
                    sm2.xVelocity = med.xVelocity;
                    sm2.yVelocity = med.yVelocity - 15f;
                    toAdd.add(sm2);
                    sm2.update(elapsed);
                    if (isBoss) {
                        bossSlimes.add(sm2);
                    }
                });
        items.addAll(toAdd);
    }

    private void collisionDetect(float elapsed) {
        checkAndSplitBoss(elapsed);
        checkAndSplitMed(elapsed);
        items.removeIf(item -> {
            if (item.isDead) {
                if (item.type == PopperItem.TYPE.POTION) {
                    POTION_EARNED = true;
                }
            }
            return item.isDead;
        });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.POTION && !item.isDying)
                .forEach(potion -> Stream.of(shootLouse)
                        .forEachOrdered(louse -> {
                            if (louse != null && louse.isFiring && near(louse, potion)) {
                                potion.startDeath();
                                shootLouse.startReset();
                                spawnTimers.forEach(slimeSpawnTimer -> {
                                    // If we hit a potion, don't spawn another at low ascension
                                    if (slimeSpawnTimer.type == SlimeSpawnTimer.TIMER_TYPE.POTION) {
                                        slimeSpawnTimer.isDone = true;
                                    }
                                });
                            }
                        }));
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.BOSS && !item.isDying)
                .map(item -> ((PopperBoss) item))
                .forEach(boss -> {
                    if (shootLouse != null && shootLouse.isFiring && near(shootLouse, boss)) {
                        boss.dealDamage(shootLouse);
                        shootLouse.startReset();
                    }
                });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.MED && !item.isDying)
                .map(item -> ((PopperMed) item))
                .forEach(med -> {
                    if (shootLouse != null && shootLouse.isFiring && near(shootLouse, med)) {
                        med.dealDamage(shootLouse);
                        shootLouse.startReset();
                    }
                });
        items.stream()
                .filter(item -> item.type == PopperItem.TYPE.SLIME && !item.isDying)
                .forEach(slime -> Stream.of(shootLouse)
                        .forEachOrdered(louse -> {
                            if (louse != null && louse.isFiring && near(louse, slime)) {
                                popCount += 1;
                                slime.startDeath();
                                shootLouse.startReset();
                                bossSlimes.remove(slime);
                            }
                        }));
    }

    private static float slimeSoundtimer = 0;

    public static void playSlimeSoundRegulated() {
        if (slimeSoundtimer <= 0) {
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
