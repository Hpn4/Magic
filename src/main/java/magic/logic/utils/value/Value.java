package magic.logic.utils.value;

import java.io.Serializable;
import java.util.Arrays;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public abstract class Value<E> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4083649490267196869L;

	protected Object object;

	public Value(final Object e) {
		object = e;
	}

	public abstract E get(final Game game, final Card thisCard, final EffectData data);

	public void set(final E e) {
		object = e;
	}

	public Object getObject() {
		return object;
	}

	public String toString() {
		return object.toString();
	}

	@Override
	public int hashCode() {
		if (object instanceof Object[]o)
			return 31 + Arrays.hashCode(o);

		return 31 + object.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (object instanceof Object[]o)
			return Arrays.equals(o, (Object[]) ((Value<?>) obj).object);

		return object.equals(((Value<?>) obj).object);
	}
}
