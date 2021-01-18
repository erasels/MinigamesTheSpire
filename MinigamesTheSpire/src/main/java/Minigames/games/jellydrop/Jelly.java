package Minigames.games.jellydrop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

import java.util.ArrayList;
import java.util.HashSet;

public class Jelly {
	Body centerBody;
	ArrayList<Body> outerBodies;
	Fixture sensor;

	public HashSet<Jelly> contacts;
	public int color;
	public int explosionCounter;
	public int size;
	public float realSize;

	public Jelly(World world, Jelly orig, int size) {
		this(world, orig.centerBody.getPosition().x, orig.centerBody.getPosition().y, size);

		color = orig.color;
		centerBody.setType(BodyDef.BodyType.DynamicBody);

		float ratio = realSize / (orig.size * 0.7f + 0.3f);

		Vector2 centerPos = centerBody.getPosition();
		for (int i = 0; i < outerBodies.size(); i++) {
			Vector2 v = orig.outerBodies.get(i).getPosition();

			outerBodies.get(i).setTransform(
					(v.x - centerPos.x) * ratio + centerPos.x,
					(v.y - centerPos.y) * ratio + centerPos.y,
					orig.outerBodies.get(i).getAngle());

			outerBodies.get(i).setType(BodyDef.BodyType.DynamicBody);
		}
	}

	public Jelly(World world, float x, float y, int size) {
		color = MathUtils.random(0, 4);
		this.size = size;
		realSize = size * 0.7f + 0.3f;
		explosionCounter = -1;

		short group = (short) MathUtils.random(-32768, -1);
		centerBody = createBody(world, x, y, 0, 0, 0.7f * realSize, 0.5f / realSize, group);

		CircleShape circle = new CircleShape();
		circle.setRadius(1.4f * realSize);
		FixtureDef sensorFixtureDef = new FixtureDef();
		sensorFixtureDef.shape = circle;
		sensorFixtureDef.isSensor = true;
		sensorFixtureDef.density = 0.5f;
		sensorFixtureDef.friction = 0.8f;
		sensorFixtureDef.restitution = 0.6f; // Bounce
		sensorFixtureDef.filter.groupIndex = group;
		sensor = centerBody.createFixture(sensorFixtureDef);
		sensor.setUserData(this);
		circle.dispose();

		int from = -1;
		int to = 1;
		float step = 0.6f * realSize;
		float end = 1.0f * realSize;

		outerBodies = new ArrayList<>();
		for (int i = from; i <= to; i++) {
			outerBodies.add(createBody(world, x, y, i * step, -end, 0.3f * realSize, 0.5f / realSize, group));
		}
		for (int i = from; i <= to; i++) {
			outerBodies.add(createBody(world, x, y, end, i * step, 0.3f * realSize, 0.5f / realSize, group));
		}
		for (int i = from; i <= to; i++) {
			outerBodies.add(createBody(world, x, y, -i * step, end, 0.3f * realSize, 0.5f / realSize, group));
		}
		for (int i = from; i <= to; i++) {
			outerBodies.add(createBody(world, x, y, -end, -i * step, 0.3f * realSize, 0.5f / realSize, group));
		}

		for (int i = 0; i < outerBodies.size(); i++) {
			DistanceJointDef jointDef = new DistanceJointDef();
			jointDef.length = 0;
			jointDef.initialize(centerBody, outerBodies.get(i), centerBody.getPosition(), outerBodies.get(i).getPosition());
			jointDef.frequencyHz = 10.0f;
			jointDef.dampingRatio = 0.03f;
			world.createJoint(jointDef);

			jointDef = new DistanceJointDef();
			jointDef.length = 0;
			jointDef.initialize(outerBodies.get(i), outerBodies.get((i + 1) % outerBodies.size()), outerBodies.get(i).getPosition(), outerBodies.get((i + 1) % outerBodies.size()).getPosition());
			jointDef.frequencyHz = 0.0f;
			jointDef.dampingRatio = 0.01f;
			world.createJoint(jointDef);
		}
		contacts = new HashSet<>();
	}

	Body createBody(World world, float baseX, float baseY, float x, float y, float radius, float density, short group) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(baseX + x, baseY + y);

		Body body = world.createBody(bodyDef);

		CircleShape circle = new CircleShape();
		circle.setRadius(radius);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = density;
		fixtureDef.friction = 0.8f;
		fixtureDef.restitution = 0.5f; // Bounce
		fixtureDef.filter.groupIndex = group;
		Fixture fixture = body.createFixture(fixtureDef);

		circle.dispose();
		return body;
	}


	public void moveTo(float x, float y) {
		Vector2 v = centerBody.getPosition();
		for (Body b : outerBodies) {
			Vector2 v2 = b.getPosition();
			b.setTransform(x + v2.x - v.x, y + v2.y - v.y, 0);
		}
		centerBody.setTransform(x, y, 0);
	}

	public void lockIn() {
		if (centerBody.getType() == BodyDef.BodyType.KinematicBody) {
			centerBody.setType(BodyDef.BodyType.DynamicBody);
			for (Body b : outerBodies) {
				b.setType(BodyDef.BodyType.DynamicBody);
			}
		}
	}

	public void explode() {
		centerBody.setType(BodyDef.BodyType.StaticBody);
		for (Body b : outerBodies) {
			b.setType(BodyDef.BodyType.StaticBody);
		}
		explosionCounter = 50;
	}

	public void update() {
		if (explosionCounter > 0) {
			explosionCounter--;
		}
	}

	public void destroy(World world) {
		world.destroyBody(centerBody);
		for (Body b : outerBodies) {
			world.destroyBody(b);
		}
	}

	public void spriteRender(PolygonSpriteBatch psb) {
		float c = Color.WHITE.toFloatBits();
		float[] vertices = new float[65];
		Vector2 cPos = centerBody.getPosition();
		vertices[0] = cPos.x;
		vertices[1] = cPos.y;
		vertices[2] = c;
		vertices[3] = 0.5f;
		vertices[4] = 0.5f;

		float scale = 1.2f;
		if (explosionCounter > 0 && explosionCounter < 20) {
			if (size > 1) {
				scale *= (0.7f * (explosionCounter / 20.0f + size - 1) + 0.3f) / realSize;
			} else {
				scale = scale * explosionCounter / 20;
			}
		}

		for (int i = 0; i < 12; i++) {
			Vector2 v = outerBodies.get(i).getPosition();
			vertices[5 * i + 5] = (v.x - cPos.x) * scale + cPos.x;
			vertices[5 * i + 6] = (v.y - cPos.y) * scale + cPos.y;
			vertices[5 * i + 7] = c;
			if (i < 3) {
				vertices[5 * i + 8] = 0.2f + 0.3f * i;
				vertices[5 * i + 9] = 0.0f;
			} else if (i < 6) {
				vertices[5 * i + 8] = 1.0f;
				vertices[5 * i + 9] = 0.2f + 0.3f * (i - 3);
			} else if (i < 9) {
				vertices[5 * i + 8] = 0.8f - 0.3f * (i - 6);
				vertices[5 * i + 9] = 1.0f;
			} else {
				vertices[5 * i + 8] = 0.0f;
				vertices[5 * i + 9] = 0.8f - 0.3f * (i - 9);
			}
		}
		short[] triangles = new short[]{
				0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 5, 0, 5, 6, 0, 6, 7, 0, 7, 8, 0, 8, 9, 0, 9, 10, 0, 10, 11, 0, 11, 12, 0, 12, 1
		};
		if (explosionCounter > 20) {
			psb.setShader(GlowShader.glowShader);
		}
		psb.draw(JellyDropGame.textures[color], vertices, 0, 65, triangles, 0, 36);
		if (explosionCounter > 20) {
			psb.setShader(null);
		}
	}
}
