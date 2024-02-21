package magic.logic.card.mana;

import java.io.Serializable;
import java.util.Objects;

import magic.logic.utils.selector.Selectors;

public abstract class AbstractMana implements Comparable<Object>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4011069592512763753L;

	protected MCType mana;

	private Object data;

	protected AbstractMana(final MCType type) {
		this.mana = type;
	}

	public MCType getMana() {
		return mana;
	}

	public Object getData() {
		return data;
	}

	public boolean isSnowMana() {
		return data instanceof Boolean bol && bol;
	}

	public Selectors getRestriction() {
		return data instanceof Selectors select ? select : null;
	}

	public void setData(final Object obj) {
		this.data = obj;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, mana);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AbstractMana other)
			return Objects.equals(data, other.data) && mana == other.mana;

		return false;
	}

}
