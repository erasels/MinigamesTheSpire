package Minigames.games.beatpress;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class BeatPattern {
    private ArrayList<BallInfo> ballInfos = new ArrayList<>();

    public BeatPattern(String pattern) {
        // key:

        // Side: L R ? _ ! =    ?random _fixed random !opposite of last =same as last
        // Type: R B S ? _ =     ?random _fixed random
        // Duration (time until next ball, float)

        String[] tokens = pattern.split(" ");

        for (String s :tokens)
            ballInfos.add(new BallInfo(s));
    }

    public float addBalls(AbstractMinigame parent, float time, PriorityQueue<Ball> balls, ArrayList<Ball> allBalls) {
        //reset();

        boolean lastRight = true;
        Ball.BallType lastType = Ball.BallType.ROLL;

        if (!allBalls.isEmpty())
        {
            lastRight = allBalls.get(allBalls.size() - 1).right;
            lastType = allBalls.get(allBalls.size() - 1).type;
        }

        for (BallInfo info : ballInfos)
        {
            Ball b = generateBall(parent, time, info, lastRight, lastType);

            balls.add(b);
            allBalls.add(b);

            time += info.duration;
        }

        return time;
    }

    public void reset() {
        fixedSide = null;
        fixedBall = null;
    }

    private Ball generateBall(AbstractMinigame parent, float time, BallInfo info, boolean right, Ball.BallType type) {
        switch (info.sideType)
        {
            case RIGHT:
                right = true;
                break;
            case LEFT:
                right = false;
                break;
            case FIXED:
                right = getFixedSide();
                break;
            case RANDOM:
                right = MathUtils.randomBoolean();
                break;
            case OPPOSITE:
                right = !right;
                break;
        }

        switch (info.genType)
        {
            case ROLL:
                type = Ball.BallType.ROLL;
                break;
            case BOUNCE:
                type = Ball.BallType.BOUNCE;
                break;
            case SPEED:
                type = Ball.BallType.SPEED;
                break;
            case RANDOM:
                type = getRandomBall();
                break;
            case FIXED:
                type = getFixedBall();
                break;
        }

        return new Ball(parent, type, time, right);
    }

    private Boolean fixedSide = null;
    private boolean getFixedSide() {
        if (fixedSide == null)
            fixedSide = MathUtils.randomBoolean();

        return fixedSide;
    }

    private Ball.BallType fixedBall = null;
    private Ball.BallType getFixedBall() {
        if (fixedBall == null)
            fixedBall = getRandomBall();

        return fixedBall;
    }
    private Ball.BallType getRandomBall() {
        float f = MathUtils.random();
        if (f < 0.35f)
            return Ball.BallType.ROLL;
        else if (f < 0.75f)
            return Ball.BallType.BOUNCE;

        return Ball.BallType.SPEED;
    }


    private static class BallInfo {
        private enum SideType {
            LEFT,
            RIGHT,
            RANDOM,
            FIXED,
            OPPOSITE,
            REPEAT
        }
        private enum BallGenType {
            ROLL,
            BOUNCE,
            SPEED,
            RANDOM,
            FIXED,
            REPEAT
        }

        protected float duration;
        protected SideType sideType;
        protected BallGenType genType;

        public BallInfo(String info) {
            switch (info.charAt(0)) {
                case '?':
                    sideType = SideType.RANDOM;
                    break;
                case '_':
                    sideType = SideType.FIXED;
                    break;
                case '!':
                    sideType = SideType.OPPOSITE;
                    break;
                case '=':
                    sideType = SideType.REPEAT;
                    break;
                case 'L':
                    sideType = SideType.LEFT;
                    break;
                default:
                    sideType = SideType.RIGHT;
                    break;
            }

            switch (info.charAt(1)) {
                case '?':
                    genType = BallGenType.RANDOM;
                    break;
                case '_':
                    genType = BallGenType.FIXED;
                    break;
                case '=':
                    genType = BallGenType.REPEAT;
                    break;
                case 'R':
                    genType = BallGenType.ROLL;
                    break;
                case 'B':
                    genType = BallGenType.BOUNCE;
                    break;
                default:
                    genType = BallGenType.SPEED;
                    break;
            }

            duration = Float.parseFloat(info.substring(2));
        }
    }
}
