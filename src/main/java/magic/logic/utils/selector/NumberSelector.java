package magic.logic.utils.selector;

import java.io.Serializable;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class NumberSelector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7431079176132890540L;

	private final Nmbs number;

	private final Operator op;

	private final String key;

	protected NumberSelector(final String key, final Nmbs number, final Operator op) {
		this.number = number;
		this.op = op;
		this.key = key;
	}

	public boolean match(final Game game, final Card card, final Targetable target) {
		final int numberToTest = card.getInt(key);
		final int number = this.number.get(game, card, new EffectData(null, target));
		return switch (op) {
		case EQUAL -> numberToTest == number;
		case GREATER -> numberToTest > number;
		case GREATER_EQUAL -> numberToTest >= number;
		case LESS -> numberToTest < number;
		case LESS_EQUAL -> numberToTest <= number;
		default -> false;
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, number, op);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof NumberSelector other)
			return Objects.equals(key, other.key) && Objects.equals(number, other.number) && op == other.op;

		return false;
	}

}
