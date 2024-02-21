package magic.logic.utils.extractor.number.operation;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class NegateNumber implements Operations {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4976948880733879353L;

	public float apply(final float value, final Game game, final Card thisCard, final EffectData data) {
		return -value;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj.getClass() == NegateNumber.class;
	}

	@Override
	public int hashCode() {
		return getClass().getName().hashCode();
	}
}
