package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;

public class ShuffleLibrary implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4009750328673870862L;

	private final boolean shuffleThisCard;

	public ShuffleLibrary(final boolean shuffleThisCard) {
		this.shuffleThisCard = shuffleThisCard;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable target = shuffleThisCard ? thisCard : null;

		game.pushPile(thisCard, AGMEvent.SHUFFLE_LIBRARY, null, target);

		return new EffectData(null, target);
	}

	@Override
	public int hashCode() {
		return 31 + (shuffleThisCard ? 1231 : 1237);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return shuffleThisCard == ((ShuffleLibrary) obj).shuffleThisCard;
	}

}
