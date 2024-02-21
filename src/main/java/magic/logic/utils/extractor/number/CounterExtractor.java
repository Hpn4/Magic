package magic.logic.utils.extractor.number;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;

public class CounterExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3340659276064489695L;

	/** La cible de l'extracteur. Soit THIS_CARD ou RETURNED_TARGET */
	private TargetType type;

	/** Le marqueur a recuperer */
	private CounterType counter;

	public CounterExtractor(final TargetType type, final CounterType counterType) {
		this.type = type;
		counter = counterType;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		return switch (type) {
		case RETURNED_TARGET -> (data.getTargets()[0] instanceof Card card) ? card.getCounter(counter) : 0;
		case THIS_CARD -> thisCard.getCounter(counter);
		default -> 0;
		};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + counter.hashCode();
		result = prime * result + type.hashCode();
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

		final CounterExtractor other = (CounterExtractor) obj;
		return counter == other.counter && type != other.type;
	}

}
