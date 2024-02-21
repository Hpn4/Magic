package magic.logic.utils.extractor.number;

import java.util.ArrayList;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class CardCountExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -126380411591945573L;

	private final Selectors select;

	private final boolean includeThis;

	private TargetType targetType;

	public CardCountExtractor(final Selectors select, final TargetType type, final boolean includeThis) {
		this.select = select;
		targetType = type;
		this.includeThis = includeThis;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		final Targetable[] targets = data.getTargets();
		switch (targetType) {
		case ALL_CARD -> {
			final ArrayList<Card> all = new ArrayList<>();
			if (select == null) {
				final int size = all.size();
				return includeThis ? size : size - 1;
			} else
				return select.matchCards(game, thisCard, all).size();
		}
		case RETURNED_TARGET -> {
			if (select == null) {
				if (includeThis) {
					return targets.length;
				} else {
					int i = 0;
					for (final Targetable target : targets)
						if (!(target == thisCard))
							i++;
					return i;
				}
			} else {
				int i = 0;
				final Targetable[] matchesTarget = select.match(game, thisCard, targets).toArray(Targetable[]::new);
				for (final Targetable target : matchesTarget)
					if (includeThis)
						i++;
					else if (!(target == thisCard))
						i++;
				return i;
			}
		}
		case THIS_CARD -> {
			return includeThis ? 1 : 0;
		}
		case ATTACHED_CARD -> {
			if (select == null)
				return thisCard.getAttachedCards().size();
			else
				return select.matchCards(game, thisCard, thisCard.getAttachedCards()).size();
		}
		}

		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (includeThis ? 1231 : 1237);
		result = prime * result + ((select == null) ? 0 : select.hashCode());
		result = prime * result + targetType.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final CardCountExtractor other = (CardCountExtractor) obj;
		if (select == null)
			if (other.select != null)
				return false;

		return includeThis == other.includeThis && select.equals(other.select) && targetType == other.targetType;
	}

}
