package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;

public class ManaCostSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3826307360335501110L;

	private final ManaCost cost;

	protected ManaCostSelector(final ManaCost cost) {
		this.cost = cost;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return card.getCardCost().pay(cost).isEmpty();
	}

	@Override
	public int hashCode() {
		return Objects.hash(cost);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ManaCostSelector other)
			return Objects.equals(cost, other.cost);

		return false;
	}

}
