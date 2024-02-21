package magic.logic.utils.extractor;

import java.io.Serializable;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Player;

public class OwnerExtractor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3072354248657389058L;

	private final OwnerTarget target;

	public OwnerExtractor(final OwnerTarget target) {
		this.target = target;
	}

	public Owner get(final Game game, final Card card, final EffectData data) {
		return switch (target) {
		case FROM_EFFECT -> {
			if (data.getTargets().length > 0) {
				final Targetable targ = data.getTargets()[0];
				if (targ instanceof Card c) {
					final Owner o = c.getController();
					if (o == null)
						yield c.getOwner();

					yield o;
				} else if (targ instanceof Player p)
					yield p.getPlayer();
			}

			yield null;
		}
		case PLAYING_PLAYER -> game.getPlayingPlayer();
		default -> game.choosePlayer(card);
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof OwnerExtractor other)
			return target == other.target;

		return false;
	}

	public enum OwnerTarget {
		FROM_EFFECT, CHOOSE_PLAYER, PLAYING_PLAYER;
	}
}
