package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class SacrificeEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6194667194308179561L;

	public SacrificeEffect(final Selectors select, final TargetType targetType) {
		super(select, targetType);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushPile(thisCard, Event.SACRIFICE, null, targets);

		return new EffectData(null, targets);
	}
	
	@Override
	public String toString() {
		return "Sacrifice a permanent";
	}
}
