package magic.logic.utils.selector;

import java.util.Arrays;

import magic.logic.card.Card;
import magic.logic.card.abilities.Capacities;
import magic.logic.game.Game;

public class CapacitiesSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3159518547995112288L;

	private final Capacities[] capacities;

	public CapacitiesSelector(final Capacities... capacities) {
		this.capacities = capacities;
	}

	@Override
	public boolean match(final Game game, final Card thisCard, final Card card) {
		for (final Capacities capacity : capacities)
			if (card.getCardAbilities().getCapacities().contains(capacity))
				return true;

		return false;
	}

	@Override
	public int hashCode() {
		return 31 + Arrays.hashCode(capacities);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final CapacitiesSelector other = (CapacitiesSelector) obj;
		return Arrays.equals(capacities, other.capacities);
	}

}