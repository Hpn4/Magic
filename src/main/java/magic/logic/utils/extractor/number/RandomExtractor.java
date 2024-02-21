package magic.logic.utils.extractor.number;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class RandomExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7033466600846142175L;

	private final Nmbs min;

	private final Nmbs max;

	public RandomExtractor(final Nmbs min, final Nmbs max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public float get(final Game game, final Card thisCard, final EffectData data) {
		final int mi = min.get(game, thisCard, data), ma = max.get(game, thisCard, data);

		return (int) Math.random() * (ma - mi) + mi;
	}

	@Override
	public int hashCode() {
		return Objects.hash(max, min);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof RandomExtractor other)
			return Objects.equals(max, other.max) && Objects.equals(min, other.min);

		return false;
	}

}
