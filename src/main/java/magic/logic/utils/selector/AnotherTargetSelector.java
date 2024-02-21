package magic.logic.utils.selector;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.game.Game;

public class AnotherTargetSelector implements Selector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8300758686508797151L;

	@Override
	public boolean match(final Game game, final Card thisCard, final Targetable target, final SelectData data) {
		final Integer id = thisCard.getGameID();

		if (data.canTarget())
			// Si la cible est déja ciblée par ce sort, c'est pas bon
			return !target.getTargetsID().contains(id);
		else if (target instanceof Card card)
			// Si la carte à déja été choisie, c'est pas bon
			return !card.getSelectedBy().contains(id);

		// Ne devrait jamais s'éxecuter
		return true;
	}

}
