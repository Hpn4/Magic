package magic.logic.game;

import java.io.Serializable;

import magic.logic.card.Card;
import magic.logic.card.Counter;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.mana.ManaCost;
import magic.logic.place.Place;
import magic.logic.utils.GameEvent;

public class PilePart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GameEvent event;

	private Object data;

	private Targetable[] targets;

	private Card thrower;

	public PilePart(final Card thrower, final GameEvent event, Targetable... targetables) {
		this(thrower, event, null, targetables);
	}

	public PilePart(final Card thrower, final GameEvent event, final Object data, Targetable... targetables) {
		this.event = event;
		this.data = data;
		targets = targetables;
		this.thrower = thrower;
	}

	public Card getThrower() {
		return thrower;
	}

	public void setThrower(final Card thrower) {
		this.thrower = thrower;
	}

	public GameEvent getEvent() {
		return event;
	}

	public void setEvent(final Event event) {
		this.event = event;
	}

	public Targetable[] getTargets() {
		return targets;
	}

	public void setTargets(final Targetable... targetables) {
		targets = targetables;
	}

	public Object getData() {
		return data;
	}

	public int getInt() {
		return (int) data;
	}

	public float getFloat() {
		return (float) data;
	}

	public String getString() {
		return (String) data;
	}

	public ManaCost getManaCost() {
		return (ManaCost) data;
	}

	public Counter getCounter() {
		return (Counter) data;
	}

	public Place getPlace() {
		return (Place) data;
	}

	public void setData(final Object obj) {
		data = obj;
	}

	public String toString() {
		return "event:" + event + ", throws by:" + thrower + ", data:[" + data + "]";
	}
}
