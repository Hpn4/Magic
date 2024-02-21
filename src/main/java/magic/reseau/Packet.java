package magic.reseau;

import java.io.Serializable;

import magic.logic.card.Counter;
import magic.logic.card.Stat;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.utils.TokenData;
import magic.logic.card.mana.ManaCost;
import magic.logic.place.Place;
import magic.logic.utils.GameEvent;

public class Packet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4350035772009200507L;

	private GameEvent event;

	private Object data;

	private int[] gameIDs;

	private int idThrower;

	public Packet(final int idThrower, final GameEvent event, final int... gameIDs) {
		this(idThrower, event, null, gameIDs);
	}

	public Packet(final int idThrower, final GameEvent event, final Object data, final int... gameIDs) {
		this.event = event;
		this.data = data;
		this.gameIDs = gameIDs;
		this.idThrower = idThrower;
	}

	public int getThrower() {
		return idThrower;
	}

	public void setThrower(final int idThrower) {
		this.idThrower = idThrower;
	}

	public GameEvent getEvent() {
		return event;
	}

	public void setEvent(final GameEvent event) {
		this.event = event;
	}

	public int[] getGameIDs() {
		return gameIDs;
	}

	public void setTargets(final int... gameIDs) {
		this.gameIDs = gameIDs;
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

	public Stat getStat() {
		return (Stat) data;
	}

	public TokenData getTokenData() {
		return (TokenData) data;
	}

	public Place getPlace() {
		return (Place) data;
	}

	public InversableEffect getInvEffect() {
		return (InversableEffect) data;
	}

	public void setData(final Object obj) {
		data = obj;
	}

	public String toString() {
		String ret = "event:" + event + ", throws by:" + idThrower + ", data:[" + data + "] [";
		for (int i : gameIDs)
			ret += i + ",";

		return ret + "]";
	}

}
