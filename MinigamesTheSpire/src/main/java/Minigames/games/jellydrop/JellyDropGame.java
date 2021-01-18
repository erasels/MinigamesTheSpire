package Minigames.games.jellydrop;

import Minigames.Minigames;
import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.input.bindings.InputBinding;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.*;

public class JellyDropGame extends AbstractMinigame {
	public static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Minigames.makeID("JellyDrop"));
	public static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(Minigames.makeID("JellyDrop"));

	World world;
	Camera camera;
	PolygonSpriteBatch psb;
	SpriteBatch sb;

	static final float TIME_STEP = 0.0166666666666666666f;

	ArrayList<Jelly> jellies;
	ArrayList<Jelly> jelliesControl;
	ArrayList<Jelly> jelliesNext;
	float rot;

	int waitFrames = 75;
	static final float xSpeed = 0.2f;
	static final float ySpeed = -0.07f;
	static final float rotSpeed = 0.05f;
	static final float interval = 1.5f;

	static final float wallLeft = -9.0f;
	static final float wallRight = 9.0f;
	static final float radius = 1.3f;

	public static String filename = "JellyDropData";
	public static Color titleColor = new Color(0.90f, 0.80f, 0.60f, 1.0f);
	public static Color numColor = new Color(0.68f, 0.85f, 0.90f, 1.0f);
	public static Color finishColor = new Color(1.0f, 0.5f, 1.0f, 1.0f);
	public static Color otherColor = Color.CHARTREUSE;

	public static Texture[] textures;
	BitmapFont font = FontHelper.SCP_cardTitleFont_small;

	Body groundBody;
	Body wallLeftBody;
	Body wallRightBody;

	int score;
	int displayScore;
	int hiscore = 0;
	int scoreAdd;
	int scoreFrames;
	int finishFrames;
	int prevScoreAdd;
	int count;
	boolean gameOver;

	private float accumulator = 0;
	int frames = 0;
	int timeLeft;
	float timeScale;
	Color timeColor;

	boolean directionLeft, directionRight, directionUp, directionDown, keyCtrl;

	public static Properties defaults = new Properties();

	public JellyDropGame() {
		world = new World(new Vector2(0, -10), true);
		JellyDropContactListener listener = new JellyDropContactListener();
		world.setContactListener(listener);
		camera = new OrthographicCamera(40.0f * Settings.WIDTH / SIZE / Settings.scale, 40.0f * Settings.HEIGHT / SIZE / Settings.scale);
		jellies = new ArrayList<>();
		jelliesControl = new ArrayList<>();
		jelliesNext = new ArrayList<>();
		textures = new Texture[]{
				new Texture(Minigames.makeGamePath("JellyDrop/Jelly0.png")),
				new Texture(Minigames.makeGamePath("JellyDrop/Jelly1.png")),
				new Texture(Minigames.makeGamePath("JellyDrop/Jelly2.png")),
				new Texture(Minigames.makeGamePath("JellyDrop/Jelly3.png")),
				new Texture(Minigames.makeGamePath("JellyDrop/Jelly4.png"))
		};
		psb = new PolygonSpriteBatch();
		psb.setProjectionMatrix(camera.combined);
		sb = new SpriteBatch();
		score = 0;
		displayScore = 0;
		scoreAdd = 0;
		scoreFrames = 0;
		prevScoreAdd = 0;
		count = 0;
		directionLeft = directionRight = directionUp = directionDown = keyCtrl = false;

		try {
			SpireConfig config = new SpireConfig(Minigames.getModID(), filename, defaults);
			config.load();
			hiscore = config.getInt("hiscore");
		} catch (Exception e) {
			saveHiScore();
		}
		createObjects();
	}

	void saveHiScore() {
		try {
			SpireConfig config = new SpireConfig(Minigames.getModID(), filename, defaults);
			config.setInt("hiscore", hiscore);
			config.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void createObjects() {
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(0, -24));

		groundBody = world.createBody(groundBodyDef);

		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(40.0f, 10.0f);
		groundBody.createFixture(groundBox, 0.0f);
		groundBox.dispose();

		BodyDef wallBodyDef = new BodyDef();
		wallBodyDef.position.set(new Vector2(wallLeft - 10.0f, -4.0f));

		wallLeftBody = world.createBody(wallBodyDef);

		PolygonShape wallBox = new PolygonShape();
		wallBox.setAsBox(10.0f, 12.0f);
		wallLeftBody.createFixture(wallBox, 0.0f);

		wallBodyDef.position.set(new Vector2(wallRight + 10.0f, -4.0f));

		wallRightBody = world.createBody(wallBodyDef);

		wallRightBody.createFixture(wallBox, 0.0f);

		wallBox.dispose();
	}

	@Override
	public void initialize() {
		super.initialize();
		background = new Texture(Minigames.makeGamePath("JellyDrop/background.png"));
		finishFrames = 0;
		timeLeft = 80;
		timeScale = 1;
		timeColor = Color.WHITE.cpy();
	}

	private boolean collideWithOtherJellies(Jelly j) {
		Vector2 pos = j.centerBody.getPosition();
		for (Jelly other : jellies) {
			if (jelliesControl.contains(other) || jelliesNext.contains(other)) continue;
			Vector2 otherPos = other.centerBody.getPosition();
			if (pos.y > otherPos.y && Vector2.dst(pos.x, pos.y, otherPos.x, otherPos.y) < (j.realSize + other.realSize) * 1.4f) {
				return true;
			}
		}
		return false;
	}

	int getMultiplier(int num) {
		return (num + 3) / 4;
	}

	void checkExplosion(boolean checkAll) {
		boolean soundPlayed = false;
		if (checkAll) {
			HashMap<Jelly, HashSet<Jelly>> groups = new HashMap<>();
			for (Jelly j1 : jellies) {
				if (j1.centerBody.getType() != BodyDef.BodyType.KinematicBody) {
					for (Jelly j2 : j1.contacts) {
						if (j1.color == j2.color) {
							HashSet<Jelly> g1 = groups.get(j1);
							HashSet<Jelly> g2 = groups.get(j2);
							if (g1 == null && g2 == null) {
								g1 = new HashSet<>();
								g1.add(j1);
								g1.add(j2);
								groups.put(j1, g1);
								groups.put(j2, g1);
							} else if (g1 == null) {
								g2.add(j1);
								groups.put(j1, g2);
							} else if (g2 == null) {
								g1.add(j2);
								groups.put(j2, g1);
							} else if (g1 != g2) {
								for (Jelly j : g1) {
									groups.put(j, g2);
								}
								g2.addAll(g1);
								g1.clear();
							}
						}
					}
				}
			}
			for (Map.Entry<Jelly, HashSet<Jelly>> entry : groups.entrySet()) {
				HashSet<Jelly> g = entry.getValue();
				if (g.stream().filter(j -> j.centerBody.getType() != BodyDef.BodyType.KinematicBody).count() >= 4) {
					for (Jelly j : g) {
						if (j.explosionCounter < 0) {
							j.explode();
							scoreAdd++;
							scoreFrames = 135;
							if (!soundPlayed) {
								CardCrawlGame.sound.play("SLIME_BLINK_" + MathUtils.random(1, 4));
								soundPlayed = true;
							}
						}
					}
					g.clear();
				}
			}
		} else {
			for (Jelly j1 : jellies) {
				if (j1.centerBody.getType() != BodyDef.BodyType.KinematicBody) {
					if (j1.explosionCounter < 0) {
						for (Jelly j2 : j1.contacts) {
							if (j2.explosionCounter >= 0 && j1.color == j2.color) {
								j1.explode();
								scoreAdd++;
								scoreFrames = 135;
								if (!soundPlayed) {
									CardCrawlGame.sound.play("SLIME_BLINK_" + MathUtils.random(1, 4));
									soundPlayed = true;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		float frameTime = Math.min(elapsed, 0.25f);
		accumulator += frameTime;
		while (accumulator >= TIME_STEP) {
			if (jelliesNext.isEmpty()) {
				if (!gameOver) {
					int[] size = new int[3];
					for (int i = 0; i < 3; i++) {
						size[i] = 1;
						if (MathUtils.random(0, 119 - 20 * AbstractDungeon.actNum) < count) {
							size[i]++;
						}
					}
					for (int i = 0; i < 3; i++) {
						Jelly j = new Jelly(world, interval * (size[i] * 0.7f + size[1] * 0.7f + 0.6f) * (i - 1), 25.0f, size[i]);
						jellies.add(j);
						jelliesNext.add(j);
					}
					count++;
				}
			} else {
				float y = jelliesNext.get(0).centerBody.getPosition().y;
				if (y > 18.0f) {
					y = Math.max(y - 0.12f, 18.0f);
				}
				for (Jelly j : jelliesNext) {
					j.moveTo(j.centerBody.getPosition().x, y);
				}
			}

			boolean justGameOver = false;
			if (jelliesControl.isEmpty()) {
				if (!gameOver) {
					waitFrames--;
					if (waitFrames <= 0) {
						ArrayList<Jelly> temp = jelliesControl;
						jelliesControl = jelliesNext;
						jelliesNext = temp;

						for (Jelly j : jelliesControl) {
							if (collideWithOtherJellies(j)) {
								justGameOver = true;
								gameOver = true;
							}
						}
					}
					rot = 0.0f;
				}
			} else {
				float dx = 0;
				float dy = ySpeed - count * (0.001f * AbstractDungeon.actNum);

				if (directionRight) {
					dx += xSpeed;
				}
				if (directionLeft) {
					dx -= xSpeed;
				}
				if (dx < 0) {
					for (Jelly j : jelliesControl) {
						if (j.centerBody.getPosition().x < wallLeft + radius * j.realSize) {
							dx = 0;
							break;
						}
					}
				} else if (dx > 0) {
					for (Jelly j : jelliesControl) {
						if (j.centerBody.getPosition().x > wallRight - radius * j.realSize) {
							dx = 0;
							break;
						}
					}
				}
				if (directionDown) {
					dy += 3 * ySpeed;
				}

				float drot = 0.0f;
				if (directionUp) {
					drot += rotSpeed;
				} else if (keyCtrl) {
					drot -= rotSpeed;
				}
				if (drot != 0.0f) {
					boolean stuck = false;
					for (Jelly j : jelliesControl) {
						float x = j.centerBody.getPosition().x;
						if ((drot > 0 && rot % MathUtils.PI > MathUtils.PI / 2 || drot < 0 && rot != 0 && rot % MathUtils.PI < MathUtils.PI / 2)
								&& (x < wallLeft + radius * j.realSize || x > wallRight - radius * j.realSize)) {
							stuck = true;
							break;
						}
					}
					if (!stuck) {
						rot += drot;
						if (rot < 0) rot += MathUtils.PI2;
					}
				}

				boolean shouldLockIn = false;
				for (Jelly j : jelliesControl) {
					Vector2 pos = j.centerBody.getPosition();
					if (pos.y < -12.7f) {
						shouldLockIn = true;
						break;
					}
					if (collideWithOtherJellies(j)) {
						shouldLockIn = true;
						break;
					}
				}
				if (shouldLockIn) {
					for (Jelly j : jelliesControl) {
						j.lockIn();
					}
					jelliesControl.clear();
					waitFrames = 35;
				} else {
					Vector2 prevPos = jelliesControl.get(1).centerBody.getPosition();

					int index = -1;
					for (Jelly j : jelliesControl) {
						j.moveTo(
								prevPos.x + dx + index * interval * (j.realSize + jelliesControl.get(1).realSize) * MathUtils.cos(rot),
								prevPos.y + dy - index * interval * (j.realSize + jelliesControl.get(1).realSize) * MathUtils.sin(rot));
						index++;
					}
				}
			}
			world.step(TIME_STEP, 2, 2);

			checkExplosion(waitFrames < 20);
			if (scoreFrames > 0) {
				scoreFrames--;
				if (scoreAdd > 0 && scoreFrames == 60) {
					score += scoreAdd * getMultiplier(scoreAdd);
					prevScoreAdd = scoreAdd;
					scoreAdd = 0;
				}
			}

			for (int i = 0; i < jellies.size(); i++) {
				Jelly j = jellies.get(i);
				j.update();
				float x = j.centerBody.getPosition().x;
				if (x < wallLeft - 1.0f || x > wallRight + 1.0f) {
					if (!gameOver) {
						justGameOver = true;
						gameOver = true;
					}
				}

				if (j.explosionCounter == 0) {
					if (j.size > 1) {
						Jelly j2 = new Jelly(world, j, j.size - 1);
						jellies.add(j2);
					}
					j.destroy(world);
					jellies.remove(i);
					i--;
				} else if (j.centerBody.getPosition().y < -60.0f) {
					j.destroy(world);
					jellies.remove(i);
					i--;
				}
			}

			if (!gameOver) {
				int prev = timeLeft;
				timeLeft = 80 - (frames / 60);
				if (timeLeft <= 5) {
					if (prev > 5) {
						timeColor = Color.RED.cpy();
					}
					if (timeLeft > 0) {
						timeScale = 1.0f + Interpolation.circleIn.apply((59 - (frames % 60)) / 60.0f);
					}
					if (prev > timeLeft) {
						CardCrawlGame.sound.play("KEY_OBTAIN");
						if (timeLeft == 0) {
							justGameOver = true;
							gameOver = true;
						}
					}
				} else if (timeLeft < 10 && prev > 10) {
					timeColor = Color.YELLOW.cpy();
				}
			}

			if (justGameOver) {
				for (Jelly j : jelliesControl) {
					j.destroy(world);
					jellies.remove(j);
				}
				jelliesControl.clear();
				for (Jelly j : jelliesNext) {
					j.destroy(world);
					jellies.remove(j);
				}
				jelliesNext.clear();
				groundBody.setActive(false);
				wallLeftBody.setActive(false);
				wallRightBody.setActive(false);
				finishFrames = 90;
			}

			frames++;
			if (displayScore < score - 30) {
				displayScore += 10;
			}
			if (displayScore < score - 10) {
				displayScore++;
			} else if (displayScore < score) {
				if (frames % 2 == 0) {
					displayScore++;
				}
			}
			if (finishFrames > 0) {
				finishFrames--;
			}

			accumulator -= TIME_STEP;
		}
		if (gameOver && finishFrames <= 0 && keyCtrl) {
			if (hiscore < score) {
				hiscore = score;
				saveHiScore();
			}
			isDone = true;
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		super.render(sb);
		sb.end();
		psb.begin();
		for (Jelly j : jellies) {
			j.spriteRender(psb);
		}
		psb.end();

		sb.begin();
		FontHelper.renderFontLeft(sb, font, uiStrings.TEXT_DICT.get("HISCORE"),
				Settings.WIDTH / 2.0f + 400.0f * Settings.scale,
				Settings.HEIGHT / 2.0f + 200.0f * Settings.scale,
				titleColor);
		FontHelper.renderFontRightAligned(sb, font, Integer.toString(hiscore),
				Settings.WIDTH / 2.0f + 700.0f * Settings.scale,
				Settings.HEIGHT / 2.0f + 150.0f * Settings.scale,
				numColor);

		FontHelper.renderFontLeft(sb, font, uiStrings.TEXT_DICT.get("SCORE"),
				Settings.WIDTH / 2.0f + 400.0f * Settings.scale,
				Settings.HEIGHT / 2.0f + 50.0f * Settings.scale,
				titleColor);
		FontHelper.renderFontRightAligned(sb, font, Integer.toString(displayScore),
				Settings.WIDTH / 2.0f + 700.0f * Settings.scale,
				Settings.HEIGHT / 2.0f + 0.0f * Settings.scale,
				numColor);

		if (scoreFrames > 0) {
			FontHelper.renderFontRightAligned(sb, font,
					scoreAdd > 0 ? scoreAdd + "X" + getMultiplier(scoreAdd) : prevScoreAdd + "X" + getMultiplier(prevScoreAdd),
					Settings.WIDTH / 2.0f + 700.0f * Settings.scale,
					Settings.HEIGHT / 2.0f + (scoreAdd > 0 ? 40.0f : 110.0f - scoreFrames) * Settings.scale,
					numColor);
		}
		if (gameOver) {
			FontHelper.renderFontCentered(sb, font, uiStrings.TEXT_DICT.get(score > hiscore ? "RECORD" : "FINISH"),
					Settings.WIDTH / 2.0f,
					Settings.HEIGHT / 2.0f + (finishFrames <= 0 ? 30.0f * Settings.scale : 0),
					finishColor);
			if (finishFrames <= 0) {
				FontHelper.renderFontCentered(sb, font, uiStrings.TEXT_DICT.get("CONTINUE"),
						Settings.WIDTH / 2.0f,
						Settings.HEIGHT / 2.0f - 30.0f * Settings.scale,
						finishColor);
			}
		}
		FontHelper.renderFontCentered(sb, FontHelper.energyNumFontPurple,
				Integer.toString(timeLeft),
				Settings.WIDTH / 2.0f - 600.0f * Settings.scale, Settings.HEIGHT / 2.0f + 150.0f * Settings.scale, timeColor, timeScale * 3.0f);

		for (int i = 0; i < 6; i++) {
			String msg = (50 * i + 50) + uiStrings.TEXT_DICT.get("PT");
			switch (i) {
				case 0:
				case 2:
				case 4:
					msg += (i / 2 + 1) * (5 + 5 * AbstractDungeon.actNum) + uiStrings.TEXT_DICT.get("GOLD");
					break;
				case 1:
					msg += uiStrings.TEXT_DICT.get("POTION");
					break;
				case 3:
					msg += uiStrings.TEXT_DICT.get("CARD");
					break;
				case 5:
					msg += uiStrings.TEXT_DICT.get("RELIC");
					break;
			}

			FontHelper.renderFontCentered(sb,
					FontHelper.charDescFont,
					msg,
					Settings.WIDTH / 2.0f - 600.0f * Settings.scale,
					Settings.HEIGHT / 2.0f - 250.0f + 50.0f * i,
					score >= (i + 1) * 50 ? Color.GREEN.cpy() : Color.WHITE.cpy());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Texture t : textures) {
			t.dispose();
		}
	}

	@Override
	protected BindingGroup getBindings() {
		BindingGroup bindings = new BindingGroup();

		bindings.bindDirectional(
				() -> directionUp = true, () -> directionUp = false,
				() -> directionDown = true, () -> directionDown = false,
				() -> directionLeft = true, () -> directionLeft = false,
				() -> directionRight = true, () -> directionRight = false);

		bindings.addBinding(InputBinding.create("Z", new InputBinding.InputInfo(Input.Keys.Z)));

		bindings.bind("Z", () -> keyCtrl = true, null, () -> keyCtrl = false);

		bindings.addBinding(InputBinding.create("X", new InputBinding.InputInfo(Input.Keys.X)));

		bindings.bind("X", () -> directionUp = true, null, () -> directionUp = false);
		return bindings;
	}

	@Override
	public AbstractMinigame makeCopy() {
		return new JellyDropGame();
	}

	@Override
	public String getOption() {
		return eventStrings.NAME;
	}

	@Override
	public void setupInstructionScreen(GenericEventDialog event) {
		event.updateBodyText(eventStrings.DESCRIPTIONS[0]);
		event.setDialogOption(eventStrings.OPTIONS[0]);
	}

	@Override
	public void setupPostgameScreen(GenericEventDialog event) {
		if (score >= 300) {
			event.updateBodyText(eventStrings.DESCRIPTIONS[1]);
			event.setDialogOption(eventStrings.OPTIONS[1]);
		} else if (score >= 50) {
			event.updateBodyText(eventStrings.DESCRIPTIONS[2]);
			event.setDialogOption(eventStrings.OPTIONS[1]);
		} else {
			event.updateBodyText(eventStrings.DESCRIPTIONS[3]);
			event.setDialogOption(eventStrings.OPTIONS[2]);
		}
	}

	@Override
	public boolean postgameButtonPressed(int buttonIndex, GenericEventDialog event) {
		if (score >= 50) {
			AbstractDungeon.getCurrRoom().rewards.clear();
			int multiplier = score >= 250 ? 6 : score >= 150 ? 3 : 1;
			AbstractDungeon.getCurrRoom().addGoldToRewards((5 + 5 * AbstractDungeon.actNum) * multiplier);
			if (score >= 100) {
				RewardItem reward = new RewardItem(PotionHelper.getRandomPotion());
				AbstractDungeon.getCurrRoom().rewards.add(reward);
			}
			if (score >= 200) {
				RewardItem reward = new RewardItem();
				AbstractDungeon.getCurrRoom().rewards.add(reward);
			}
			if (score >= 300) {
				AbstractRelic.RelicTier tier;
				if (AbstractDungeon.actNum == 1) {
					tier = AbstractRelic.RelicTier.COMMON;
				} else if (AbstractDungeon.actNum == 2) {
					tier = AbstractRelic.RelicTier.UNCOMMON;
				} else {
					tier = AbstractRelic.RelicTier.RARE;
				}
				AbstractRelic r = AbstractDungeon.returnRandomScreenlessRelic(tier);
				AbstractDungeon.getCurrRoom().addRelicToRewards(r);
			}
			AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
			AbstractDungeon.combatRewardScreen.open();
		}
		return true;
	}
}
