package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class TapEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4661099451693533020L;

	private final boolean tap;

	public TapEffect(final Selectors select, final TargetType targetType, final boolean tap) {
		super(select, targetType);
		this.tap = tap;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushPile(thisCard, tap ? Event.TAPPED : Event.UNTAPPED, null, targets);

		return new EffectData(effectData.getData(), targets);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + (tap ? 1231 : 1237);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		return tap == ((TapEffect) obj).tap;
	}

}
