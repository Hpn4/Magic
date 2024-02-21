package magic.logic.utils.extractor.number.operation;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class RoundNumber implements Operations {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3688227612627639306L;

	private final boolean roundedUp;

	public RoundNumber(final boolean roundedUp) {
		this.roundedUp = roundedUp;
	}

	@Override
	public float apply(final float value, final Game game, final Card thisCard, final EffectData effectData) {
		return (float) (roundedUp ? Math.ceil(value) : Math.floor(value));
	}

	@Override
	public int hashCode() {
		return Objects.hash(roundedUp);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof RoundNumber other)
			return roundedUp == other.roundedUp;

		return false;
	}

}
