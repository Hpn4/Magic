package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class ToughnessSelector extends AbstractNumberSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2666353957194634841L;

	protected ToughnessSelector(final Nmbs toughness, final Operator op) {
		super(toughness, op);
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return match(game, thisCard, card, card.getToughness());
	}

}
