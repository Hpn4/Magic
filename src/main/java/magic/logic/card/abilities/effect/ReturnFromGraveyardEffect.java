package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class ReturnFromGraveyardEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5839330805646182131L;

	private final Event event;

	public ReturnFromGraveyardEffect(final Selectors select, final TargetType targetType, final Place place) {
		super(select.place(Place.GRAVEYARD), targetType);

		event = switch (place) {
		case BATTLEFIELD -> Event.ENTER_BATTLEFIELD;
		case EXILE -> Event.PUT_INTO_EXILE;
		case HAND -> Event.PUT_INTO_HAND;
		default -> Event.PUT_INTO_GRAVEYARD;
		};
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushPile(thisCard, event, Place.GRAVEYARD, targets);

		return null;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + event.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		return event == ((ReturnFromGraveyardEffect) obj).event;
	}

}
