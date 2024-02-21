package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.game.Game;

import java.io.Serial;

public class ExcludeThisCardSelector implements CardSelector {
    /**
     *
     */
    @Serial
	private static final long serialVersionUID = -7371988906916023111L;

    public boolean match(final Game game, final Card thisCard, final Card card) {
        return thisCard != card;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == ExcludeThisCardSelector.class;
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }
}
