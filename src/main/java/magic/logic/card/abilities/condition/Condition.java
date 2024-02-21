package magic.logic.card.abilities.condition;

import java.io.Serializable;
import java.util.Arrays;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public abstract class Condition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6060978455939512168L;

	protected transient Targetable[] targets;

	public abstract boolean test(final Game game, final Card thisCard, final EffectData dataEffect);

	public boolean test(final Game game, final Card thisCard, final Targetable[] targets) {
		return test(game, thisCard, new EffectData(null, targets));
	}

	public Targetable[] getResultedTargets() {
		return targets;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return Arrays.equals(targets, ((Condition) obj).targets);
	}

}
