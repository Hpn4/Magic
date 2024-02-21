package magic.logic.utils.selector;

import java.io.Serializable;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class AbstractNumberSelector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8232996504434535141L;

	protected final Nmbs number;

	protected final Operator op;

	protected AbstractNumberSelector(final Nmbs number, final Operator op) {
		this.number = number;
		this.op = op;
	}

	protected boolean match(final Game game, final Card card, final Targetable target, final int numberToTest) {
		int number = -1;
		if (this.number != null)
			number = this.number.get(game, card, new EffectData(null, target));

		return switch (op) {
		case EQUAL -> numberToTest == number;
		case GREATER -> numberToTest > number;
		case GREATER_EQUAL -> numberToTest >= number;
		case LESS -> numberToTest < number;
		case LESS_EQUAL -> numberToTest <= number;
		case ODD -> numberToTest % 2 == 1;
		case EVEN -> numberToTest % 2 == 0;
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(number, op);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AbstractNumberSelector other)
			return Objects.equals(number, other.number) && op == other.op;

		return false;
	}

}
