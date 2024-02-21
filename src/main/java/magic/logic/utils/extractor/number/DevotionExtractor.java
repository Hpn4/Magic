package magic.logic.utils.extractor.number;

import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.selector.Selectors;

public class DevotionExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4159386254342082879L;

	private final CColor mana;

	public DevotionExtractor(final CColor mana) {
		this.mana = mana;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		final Selectors select = new Selectors();
		select.color(mana);
		return select.matchCards(game, thisCard, game.getBattlefield(Owner.YOU, thisCard)[0].getAllCards()).size();
	}

	@Override
	public int hashCode() {
		return 31 + mana.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return mana == ((DevotionExtractor) obj).mana;
	}

}
