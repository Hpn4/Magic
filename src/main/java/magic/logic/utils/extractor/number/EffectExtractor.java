package magic.logic.utils.extractor.number;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.Utils;

public class EffectExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5095143513636227608L;

	public EffectExtractor() {

	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		final Object obj = Utils.getData(data.getData());

		if (obj instanceof Integer i)
			return i;

		return 0;
	}

	@Override
	public int hashCode() {
		return getClass().getName().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj.getClass() == EffectExtractor.class;
	}
}
