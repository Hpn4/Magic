package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class CounterSelector extends AbstractNumberSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6354888521193913311L;

	private final CounterType counter;

	protected CounterSelector(final CounterType counter, final Nmbs number, final Operator op) {
		super(number, op);
		this.counter = counter;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return match(game, thisCard, card, card.getCounter(counter));
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + counter.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;

		return counter == ((CounterSelector) obj).counter;
	}

}
