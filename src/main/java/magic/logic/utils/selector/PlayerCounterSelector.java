package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.value.Nmbs;

public class PlayerCounterSelector extends AbstractNumberSelector implements Selector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 428708368133510815L;

	private final CounterType counter;

	public PlayerCounterSelector(final CounterType counter, final Nmbs number, final Operator op) {
		super(number, op);
		this.counter = counter;
	}

	public boolean match(final Game game, final Card thisCard, final Targetable target, final SelectData data) {
		if (target instanceof Player player) {
			final Integer count = player.getCounter(counter);
			return count == null ? false : match(game, thisCard, target, count);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + counter.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;

		return counter == ((PlayerCounterSelector) obj).counter;
	}

}
