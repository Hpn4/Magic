package magic.logic.utils.extractor;

import java.io.Serializable;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;

public class ManaCostExtractor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3504238607592751249L;

	private final TargetType type;

	public ManaCostExtractor(final TargetType type) {
		this.type = type;
	}

	public ManaCostExtractor() {
		type = TargetType.ALL_CARD;
	}

	public ManaCost get(final Game game, final Card thisCard, final EffectData data) {
		return switch (type) {
		case THIS_CARD -> thisCard.getCardCost();
		case RETURNED_TARGET -> data.getTargets()[0] instanceof Card card ? card.getCardCost() : new ManaCost();
		default -> data.getManaCost();
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		return obj instanceof ManaCostExtractor other && type == other.type;
	}

}
