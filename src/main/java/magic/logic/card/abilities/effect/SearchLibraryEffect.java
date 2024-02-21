package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class SearchLibraryEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7459613005060145008L;

	private final Place place;

	private final boolean reveal;

	private final boolean tapped;

	public SearchLibraryEffect(final Selectors select, final Place place, final boolean reveal, final boolean tapped) {
		super(select.place(Place.DECK), TargetType.ALL_CARD);
		this.place = place;
		this.reveal = reveal;
		this.tapped = tapped;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		// On recup les cartes
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		if (reveal)
			game.reveal(thisCard.getOwner(), targets);

		if (tapped)
			game.pushPile(thisCard, Event.TAPPED, null, targets);

		switch (place) {
		case BATTLEFIELD:
			game.pushPile(thisCard, Event.ENTER_BATTLEFIELD, Place.DECK, targets);
		case EXILE:
			game.pushPile(thisCard, Event.PUT_INTO_EXILE, Place.DECK, targets);
		case GRAVEYARD:
			game.pushPile(thisCard, Event.PUT_INTO_GRAVEYARD, Place.DECK, targets);
		case HAND:
			game.pushPile(thisCard, Event.PUT_INTO_HAND, Place.DECK, targets);
		default:
			break;
		}

		return new EffectData(null, targets);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(place, reveal, tapped);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof SearchLibraryEffect other)
			return place == other.place && reveal == other.reveal && tapped == other.tapped;

		return false;
	}

}
