package Minigames.games.beatpress;

public class Ball implements Comparable<Ball> {
    private enum BallType {
        BOUNCE,
        ROLL
    }

    private BallType type;

    public float startTime; //The time that this ball will start rolling or bouncing.
    public float hitTime; //The time that this ball should be hit, relative to the start of the game.


    private float getDuration()
    {
        if (type == BallType.BOUNCE) {
            return 1;
        }
        return 2;
    }

    @Override
    public int compareTo(Ball o) {
        return Float.compare(startTime, o.startTime);
    }
}