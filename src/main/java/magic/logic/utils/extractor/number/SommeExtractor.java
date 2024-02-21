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

public class SommeExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4104094764333491854L;

	private final Selectors selector;

	private final TargetType targetType;

	private final NumberType type;

	public SommeExtractor(final Selectors selector, final TargetType targetType, final NumberType type) {
		this.selector = selector;
		this.targetType = targetType;
		this.type = type;
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

		int somme = 0;
		for (final Targetable target : allTargets)
			somme += Utils.getNumber(target, type);

		return somme;
	}

	@Override
	public int hashCode() {
		return Objects.hash(selector, targetType, type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SommeExtractor other)
			return Objects.equals(selector, other.selector) && targetType == other.targetType && type == other.type;

		return false;
	}

}
