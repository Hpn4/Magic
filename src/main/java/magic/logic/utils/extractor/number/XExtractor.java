package magic.logic.utils.extractor.number;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;

public class XExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5240023504015559036L;

	private boolean fromEffect;

	public XExtractor() {
		this(false);
	}

	public XExtractor(final boolean fromEffect) {
		this.fromEffect = fromEffect;
	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		if (fromEffect) {
			final Object obj = data.getData();
			if (obj instanceof ManaCost mc)
				return mc.getCost(MCType.X);
		}

		return (thisCard.get(Card.MANA_COST) != null) ? thisCard.getPayedCost().getCost(MCType.X) : 0;
	}

	@Override
	public int hashCode() {
		return 31 + (fromEffect ? 1231 : 1237);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return fromEffect == ((XExtractor) obj).fromEffect;
	}

}
