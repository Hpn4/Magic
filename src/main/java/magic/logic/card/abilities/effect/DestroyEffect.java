package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class DestroyEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5001649222536472374L;

	public DestroyEffect(final Selectors select, final TargetType targetType) {
		super(select, targetType);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushEvent(Event.DESTROY, thisCard, null, targets);

		return new EffectData(null, targets);
	}
}
