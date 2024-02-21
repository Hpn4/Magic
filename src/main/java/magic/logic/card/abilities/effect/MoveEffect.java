package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Owners;

public class MoveEffect extends AbstractTargetEffect implements Effect {

	private final Event event;

	private final Owners who;

	public MoveEffect(final Selectors select, final TargetType target, final Place dst, final Owners who) {
		super(select, target);

		this.who = who;

		event = switch (dst) {
		case GRAVEYARD -> Event.PUT_INTO_GRAVEYARD;
		case HAND -> Event.PUT_INTO_HAND;
		case EXILE -> Event.PUT_INTO_EXILE;
		default -> Event.ENTER_BATTLEFIELD;
		};
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Owner o = who.get(game, thisCard, effectData);

		if (o == null)
			return null;
		else {
			return null;
		}
	}

}
