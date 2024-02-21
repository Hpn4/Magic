package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Strings;

public class NameSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9167837777461407474L;

	private final Strings name;

	private final boolean contains;

	public NameSelector(final Strings name, final boolean contains) {
		this.name = name;
		this.contains = contains;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		final String nom = name.get(game, thisCard, new EffectData(null, card));

		return contains ? card.getName().contains(nom) : card.getName().equals(nom);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contains, name);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof NameSelector other)
			return contains == other.contains && Objects.equals(name, other.name);

		return false;
	}

}
