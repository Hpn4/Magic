package magic.logic.utils.extractor.number;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.Utils;

public class ThisExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -791770246358068022L;

	private final NumberType number;

	private final TargetType target;

	public ThisExtractor(final NumberType numberType, final TargetType targetType) {
		number = numberType;
		target = targetType;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		return switch (target) {
		case ALL_CARD -> 0;
		case RETURNED_TARGET -> Utils.getNumber(data.getTargets()[0], number);
		case THIS_CARD -> Utils.getNumber(thisCard, number);
		case ATTACHED_CARD -> Utils.getNumber(thisCard.getAttachedCards().get(0), number);
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(number, target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ThisExtractor other)
			return number == other.number && target == other.target;

		return false;
	}

}
