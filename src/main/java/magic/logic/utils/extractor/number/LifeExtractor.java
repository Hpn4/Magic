package magic.logic.utils.extractor.number;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;

public class LifeExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2044564419412651950L;

	private final Owner player;

	public LifeExtractor(final Owner player) {
		this.player = player;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		return game.getPlayer(player, thisCard)[0].getLife();
	}

	@Override
	public int hashCode() {
		return 31 + player.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return player == ((LifeExtractor) obj).player;
	}

}
