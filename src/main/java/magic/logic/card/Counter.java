package magic.logic.card;

import java.io.Serializable;

import magic.logic.card.abilities.utils.CounterType;

public class Counter implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3223852733128125018L;

	private final CounterType type;

	private final int count;

	public Counter(final CounterType type) {
		this(type, 1);
	}

	public Counter(final CounterType type, final int number) {
		this.type = type;
		count = number;
	}

	public CounterType getType() {
		return type;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return type + ":" + count;
	}

	@Override
	public Counter clone() {
		try {
			return (Counter) super.clone();
		} catch (final CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + type.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Counter other = (Counter) obj;
		return count == other.count && type == other.type;
	}

}
