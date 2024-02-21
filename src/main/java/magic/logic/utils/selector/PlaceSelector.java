package magic.logic.utils.selector;

import java.util.Arrays;

import magic.logic.card.Card;
import magic.logic.game.Game;
import magic.logic.place.Place;

public class PlaceSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1317773841770685022L;

	private final Place[] places;

	protected PlaceSelector(final Place... places) {
		this.places = places;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		final Place cardPlace = card.getPlace();
		for (final Place place : places)
			if (cardPlace == place)
				return true;

		return false;
	}

	@Override
	public int hashCode() {
		return 31 + Arrays.hashCode(places);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof PlaceSelector other)
			return Arrays.equals(places, other.places);

		return false;
	}

}
