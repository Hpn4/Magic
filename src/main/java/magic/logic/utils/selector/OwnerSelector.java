package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.value.Owners;

public class OwnerSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8943027271355463410L;

	private final Owners owner;

	/**
	 * 
	 * @param owner one of : YOU, OPPONENT, EACH
	 */
	protected OwnerSelector(final Owners owner) {
		this.owner = owner;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		// PLAYER1 or PLAYER2
		final Owner thisOwner = thisCard.getOwner(), owner = card.getOwner();
		final Owner e = this.owner.get(game, thisCard, new EffectData(null, card));

		return switch (e) {
		case YOU -> owner == thisOwner;
		case OPPONENT -> (owner == Owner.PLAYER1 && thisOwner == Owner.PLAYER2)
				|| (owner == Owner.PLAYER2 && thisOwner == Owner.PLAYER1);
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
		if (obj instanceof OwnerSelector other)
			return Objects.equals(owner, other.owner);

		return false;
	}

}
