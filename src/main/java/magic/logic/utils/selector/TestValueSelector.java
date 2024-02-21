package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.game.Game;

public class TestValueSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8619778557366561244L;

	private final String key;

	private final Object value;

	public TestValueSelector(final String key, final Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean match(final Game game, final Card thisCard, final Card card) {
		final Object obj = card.get(key);
		if(obj != null)
			card.get(key).equals(value);

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof TestValueSelector other)
			return Objects.equals(key, other.key) && Objects.equals(value, other.value);

		return false;
	}

}
