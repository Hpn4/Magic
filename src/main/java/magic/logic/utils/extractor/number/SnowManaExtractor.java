package magic.logic.utils.extractor.number;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.mana.AbstractMana;
import magic.logic.game.Game;

public class SnowManaExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7912124865645048886L;

	@Override
	public float get(final Game game, final Card card, final EffectData data) {
		int snow = 0;
		for (final AbstractMana mana : card.getPayedCost().getListOfCosts())
			if (mana.isSnowMana())
				snow++;

		return snow;
	}

}
