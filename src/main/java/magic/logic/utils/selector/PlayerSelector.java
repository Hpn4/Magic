package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.value.Owners;

public class PlayerSelector implements Selector {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5811163142998677810L;

	private final Owners owner;

	public PlayerSelector(final Owners owner) {
		this.owner = owner;
	}

	@Override
	public boolean match(final Game game, final Card thisCard, final Targetable target, final SelectData data) {
		if (target instanceof Player player) {
			Owner o = Owner.EACH;
			if (owner != null)
				o = owner.get(game, thisCard, null);

			return player.getPlayer() == game.ownerToPlayer(o, thisCard);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof PlayerSelector other)
			return Objects.equals(owner, other.owner);

		return false;
	}

}
