package Minigames.games.jellydrop;

import com.badlogic.gdx.physics.box2d.*;

public class JellyDropContactListener implements ContactListener {
	@Override
	public void beginContact(Contact contact) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();
		if (f1.isSensor() && f2.isSensor()) {
			Object o1 = f1.getUserData();
			Object o2 = f2.getUserData();
			if (o1 instanceof Jelly && o2 instanceof Jelly) {
				Jelly j1 = (Jelly) o1;
				Jelly j2 = (Jelly) o2;
				j1.contacts.add(j2);
				j2.contacts.add(j1);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture f1 = contact.getFixtureA();
		Fixture f2 = contact.getFixtureB();
		if (f1.isSensor() && f2.isSensor()) {
			Object o1 = f1.getUserData();
			Object o2 = f2.getUserData();
			if (o1 instanceof Jelly && o2 instanceof Jelly) {
				Jelly j1 = (Jelly) o1;
				Jelly j2 = (Jelly) o2;
				j1.contacts.remove(j2);
				j2.contacts.remove(j1);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}
