package magic.logic.card.abilities.condition;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class ThisCardCondition extends Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6895950201719573332L;

	public ThisCardCondition() {

	}

	public boolean test(Game game, Card thisCard, EffectData dataEffect) {
		for (final Targetable target : dataEffect.getTargets())
			if (target instanceof Card card && card == thisCard) {
				targets = new Targetable[] { card };
				return true;
			}
		return false;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj.getClass() == ThisCardCondition.class;
	}
}
