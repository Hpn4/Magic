package magic.logic.utils.extractor.number;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.selector.Selectors;

public class SharesCreatureTExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2606043728796802641L;

	private final Selectors select;

	public SharesCreatureTExtractor(final Selectors select) {
		this.select = select;
	}

	@Override
	public float get(final Game game, final Card thisCard, final EffectData data) {
		final List<Card> cards = select.matchCards(game, thisCard, game.getAllCardsInGame());
		final HashMap<CreatureT, Integer> types = new HashMap<>();

		for (final Card card : cards)
			for (final CreatureT type : card.getCreatureType())
				types.merge(type, 1, (i, j) -> i + j);

		int max = 0;
		for (final int value : types.values())
			if (value > max)
				max = value;

		return max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(select);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SharesCreatureTExtractor other)
			return Objects.equals(select, other.select);

		return false;
	}

}
