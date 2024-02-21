package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Rarity;
import magic.logic.game.Game;

public class RaritySelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -736454604355943489L;

	private final Rarity rarity;

	public RaritySelector(final Rarity rarity) {
		this.rarity = rarity;
	}

	@Override
	public boolean match(final Game game, final Card thisCard, final Card card) {
		return card.getRarity() == rarity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rarity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		return obj instanceof RaritySelector other && rarity == other.rarity;
	}

}
