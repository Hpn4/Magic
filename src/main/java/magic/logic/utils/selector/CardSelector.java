package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.game.Game;

public interface CardSelector extends Selector {

	default boolean match(final Game game, final Card thisCard, final Targetable target, final SelectData data) {
		return (target instanceof Card card) && match(game, thisCard, card);
	}

	boolean match(final Game game, final Card thisCard, final Card card);
}
