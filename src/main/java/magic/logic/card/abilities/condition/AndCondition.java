package magic.logic.card.abilities.condition;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class AndCondition extends Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4300220457554704726L;

	private final Condition firstCondition;

	private final Condition secondCondition;

	private final boolean sendTargets;

	public AndCondition(final Condition firstCondition, final Condition secondCondition) {
		this.firstCondition = firstCondition;
		this.secondCondition = secondCondition;
		sendTargets = false;
	}

	public AndCondition(final Condition firstCondition, final Condition secondCondition, final boolean sendTargets) {
		this.firstCondition = firstCondition;
		this.secondCondition = secondCondition;
		this.sendTargets = sendTargets;
	}

	public boolean test(final Game game, final Card thisCard, EffectData data) {
		// We do the first condition
		final boolean one = firstCondition.test(game, thisCard, data);

		// If we need to transfer the result of the first condition to the second
		if (sendTargets)
			data = new EffectData(data.getData(), firstCondition.getResultedTargets());
		final boolean second = secondCondition.test(game, thisCard, data);

		// Then the second condition
		if (one && second) {
			if (firstCondition.getResultedTargets() != null)
				targets = firstCondition.getResultedTargets();
			else
				targets = secondCondition.getResultedTargets();

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(firstCondition, secondCondition, sendTargets);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof AndCondition other)
			return Objects.equals(firstCondition, other.firstCondition)
					&& Objects.equals(secondCondition, other.secondCondition) && sendTargets == other.sendTargets;

		return false;
	}

}
