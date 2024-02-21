package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class CCMSelector extends AbstractNumberSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4700673394733874551L;

	protected CCMSelector(final Nmbs ccm, final Operator op) {
		super(ccm, op);
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return match(game, thisCard, card, card.getCardCost().getCCM());
	}
}
