package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class PowerSelector extends AbstractNumberSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6042937866180171037L;

	protected PowerSelector(final Nmbs power, final Operator op) {
		super(power, op);
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return match(game, thisCard, card, card.getPower());
	}
}
