package Minigames.util;

public class QueuedSound implements Comparable<QueuedSound> {
    public String key;
    public float time; //if time > this time, sfx will be played and removed from queue.

    public QueuedSound(String key, float time) {
        this.key = key;
        this.time = time;
    }

    @Override
    public int compareTo(QueuedSound o) {
        return Float.compare(time, o.time);
    }
}
