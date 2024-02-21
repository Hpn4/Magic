package magic.logic.utils.extractor.number;

import java.util.List;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.Utils;
import magic.logic.utils.selector.Selectors;

public class ExtremumExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1508503562747423569L;

	private final Selectors selector;

	private final TargetType targetType;

	private final NumberType type;

	private final boolean highest;

	public ExtremumExtractor(final Selectors selector, final NumberType type, final TargetType targetType,
			final boolean highest) {
		this.selector = selector;
		this.targetType = targetType;
		this.type = type;
		this.highest = highest;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		final Targetable[] targets = data.getTargets();
		Targetable[] allTargets = null;
		switch (targetType) {
		case ALL_CARD:
			final List<Card> cards = game.getAllCardsInGame();
			if (selector == null)
				allTargets = cards.toArray(Targetable[]::new);
			else
				allTargets = selector.matchCards(game, thisCard, cards).toArray(Targetable[]::new);
			break;
		case RETURNED_TARGET:
			allTargets = selector == null ? targets
					: selector.match(game, thisCard, targets).toArray(Targetable[]::new);
			break;
		case THIS_CARD:
			allTargets = new Targetable[] { thisCard };
			break;
		case ATTACHED_CARD:
			if (selector == null)
				allTargets = thisCard.getAttachedCards().toArray(Targetable[]::new);
			else
				allTargets = selector.matchCards(game, thisCard, thisCard.getAttachedCards())
						.toArray(Targetable[]::new);
			break;
		}

		return highest ? getHighest(allTargets) : getLowest(allTargets);
	}

	private int getLowest(final Targetable[] targets) {
		int min = Integer.MAX_VALUE;
		for (final Targetable target : targets) {
			final int tmp = Utils.getNumber(target, type);
			if (tmp < min)
				min = tmp;
		}

		return min;
	}

	private int getHighest(final Targetable[] targets) {
		int max = Integer.MIN_VALUE;
		for (final Targetable target : targets) {
			final int tmp = Utils.getNumber(target, type);
			if (tmp > max)
				max = tmp;
		}

		return max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(highest, selector, targetType, type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ExtremumExtractor other)
			return highest == other.highest && Objects.equals(selector, other.selector)
					&& targetType == other.targetType && type == other.type;

		return false;
	}

}
