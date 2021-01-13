package Minigames.events;

import com.megacrit.cardcrawl.events.AbstractImageEvent;

public abstract class AbstractMinigameEvent extends AbstractImageEvent {
    public AbstractMinigameEvent(String title, String body, String imgUrl) {
        super(title, body, imgUrl);
    }
}
