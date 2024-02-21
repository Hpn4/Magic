package magic.logic.utils.extractor.number;

import java.util.ArrayList;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.mana.AbstractMana;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.utils.Utils;

public class ConvergeExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6975583166597431372L;

	public ConvergeExtractor() {

	}

	public float get(final Game game, final Card thisCard, final EffectData data) {
		if (thisCard.get(Card.MANA_COST) != null) {
			final ManaCost payedMC = thisCard.getPayedCost();
			final MCType[] colors = { MCType.WHITE, MCType.GREEN, MCType.BLUE, MCType.DARK, MCType.RED,
					MCType.COLORLESS };
			final ArrayList<MCType> retainedColors = new ArrayList<>();

			for (final AbstractMana mana : payedMC.getCosts()) {
				final MCType color = mana.getMana();
				if (Utils.contains(colors, color) && !retainedColors.contains(color))
					retainedColors.add(color);
			}

			return retainedColors.size();
		}
		return 0;
	}

	@Override
	public int hashCode() {
		return getClass().getName().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj.getClass() == ConvergeExtractor.class;
	}
}
