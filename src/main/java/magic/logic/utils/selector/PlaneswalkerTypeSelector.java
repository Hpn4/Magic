package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.PT;
import magic.logic.game.Game;

public class PlaneswalkerTypeSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7746894544182696545L;

	private final PT type;

	public PlaneswalkerTypeSelector(final PT type) {
		this.type = type;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return card.getPlaneswalker() == type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof PlaneswalkerTypeSelector other)
			return type == other.type;

		return false;
	}

}
