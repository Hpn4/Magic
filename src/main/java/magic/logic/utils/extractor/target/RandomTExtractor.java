package magic.logic.utils.extractor.target;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Targets;

public class RandomTExtractor implements TExtractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5255930249373977905L;

	private final Targets target;

	private final Nmbs nmb;

	public RandomTExtractor(final Targets target, final Nmbs nmb) {
		this.target = target;
		this.nmb = nmb;
	}

	@Override
	public Targetable[] get(final Game game, final Card thisCard, final EffectData data) {
		final int count = nmb.get(game, thisCard, data);

		final Targetable[] targets = target.get(game, thisCard, data), ret = new Targetable[count];

		int i = 0;
		while (i < count) {
			final int index = (int) Math.random() * (targets.length - 1);
			if (targets[index] != null) {
				ret[i++] = targets[index];
				targets[index] = null;
			}
		}

		return ret;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nmb, target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof RandomTExtractor other)
			return Objects.equals(nmb, other.nmb) && Objects.equals(target, other.target);

		return false;
	}

}
