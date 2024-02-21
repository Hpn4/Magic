package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.value.Owners;

public class ControllerSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7953086133393963409L;

	private final Owners owner;

	/**
	 * 
	 * @param owner one of : YOU, OPPONENT, EACH
	 */
	protected ControllerSelector(final Owners owner) {
		this.owner = owner;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		// PLAYER1 or PLAYER2
		final Owner thisController = thisCard.getController(), owner = card.getController();
		final Owner e = this.owner.get(game, thisCard, new EffectData(null, card));

		return switch (e) {
		case YOU -> owner == thisController;
		case OPPONENT -> (owner == Owner.PLAYER1 && thisController == Owner.PLAYER2)
				|| (owner == Owner.PLAYER2 && thisController == Owner.PLAYER1);
		default -> e == Owner.EACH;
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ControllerSelector other)
			return Objects.equals(owner, other.owner);

		return false;
	}

}
