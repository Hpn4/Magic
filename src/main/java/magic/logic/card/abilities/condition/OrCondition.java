package magic.logic.card.abilities.condition;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class OrCondition extends Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5641569665946422481L;

	private final Condition firstCondition;

	private final Condition secondCondition;

	public OrCondition(final Condition firstCondition, final Condition secondCondition) {
		this.firstCondition = firstCondition;
		this.secondCondition = secondCondition;
	}

	public boolean test(final Game game, final Card thisCard, final EffectData data) {
		final boolean one = firstCondition.test(game, thisCard, data),
				second = secondCondition.test(game, thisCard, data);

		if (one && second) {
			if (firstCondition.getResultedTargets() != null)
				targets = firstCondition.getResultedTargets();
			else
				targets = secondCondition.getResultedTargets();

			return true;
		} else if (one) {
			targets = firstCondition.getResultedTargets();
			return true;
		} else if (second) {
			targets = secondCondition.getResultedTargets();
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + firstCondition.hashCode();
		result = prime * result + secondCondition.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		final OrCondition other = (OrCondition) obj;
		return firstCondition.equals(other.firstCondition) && secondCondition.equals(other.secondCondition);
	}

}
