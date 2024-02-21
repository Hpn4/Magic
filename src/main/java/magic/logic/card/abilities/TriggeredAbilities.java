package magic.logic.card.abilities;

import magic.logic.card.Card;
import magic.logic.game.Game;

public abstract class TriggeredAbilities extends Abilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5016159636562748076L;

	public abstract boolean canEventExecuted(final Game game, final Card card, final Object object);
}
