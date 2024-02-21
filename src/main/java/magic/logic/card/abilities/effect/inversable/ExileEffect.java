package magic.logic.card.abilities.effect.inversable;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class ExileEffect extends AbstractTargetEffect implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8109818498454818698L;

	public ExileEffect(final Selectors select, final TargetType targetType) {
		super(select, targetType);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushPile(thisCard, Event.PUT_INTO_EXILE, Place.BATTLEFIELD, targets);

		return new EffectData(null, targets);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		game.pushPile(thisCard, Event.ENTER_BATTLEFIELD, Place.EXILE, data.getTargets());
	}

	@Override
	public Object getData() {
		return null;
	}
}
