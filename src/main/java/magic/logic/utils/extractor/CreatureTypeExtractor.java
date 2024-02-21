package magic.logic.utils.extractor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;

public class CreatureTypeExtractor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3340420766753555387L;

	private final TargetType targetType;

	public CreatureTypeExtractor(final TargetType targetType) {
		this.targetType = targetType;
	}

	public ArrayList<CreatureT> get(final Game game, final Card thisCard, final EffectData data) {
		return switch (targetType) {
		case THIS_CARD -> thisCard.getCreatureType();
		case RETURNED_TARGET -> (data.getTargets()[0] instanceof Card card) ? card.getCreatureType()
				: new ArrayList<CreatureT>();
		default -> new ArrayList<CreatureT>();
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(targetType);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CreatureTypeExtractor))
			return false;

		return targetType == ((CreatureTypeExtractor) obj).targetType;
	}

}
