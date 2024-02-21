package magic.logic.card.abilities.utils;

import java.io.Serializable;

import magic.logic.card.Counter;
import magic.logic.card.Stat;
import magic.logic.card.Targetable;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game.Flags;
import magic.logic.place.Place;

public class EffectData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7790000204239058656L;

	private final Targetable[] targets;

	private final Object data;

	private Flags flag = Flags.NONE;

	public EffectData(final Targetable... targetables) {
		data = null;
		targets = targetables;
	}

	public EffectData(final Object data, final Targetable... targetables) {
		this.data = data;
		targets = targetables;
	}

	public void setFlag(final Flags flag) {
		this.flag = flag;
	}

	public Flags getFlag() {
		return flag;
	}

	public Targetable[] getTargets() {
		return targets;
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
}
