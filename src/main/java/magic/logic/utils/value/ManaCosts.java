package magic.logic.utils.value;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.utils.extractor.ManaCostExtractor;
import magic.logic.utils.property.ManaCostProperty;

public class ManaCosts extends Value<ManaCost> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2931371676441307801L;

	public ManaCosts(final ManaCost mc) {
		super(mc);
	}

	public ManaCosts(final ManaCostExtractor extractor) {
		super(extractor);
	}

	public ManaCosts(final ManaCostProperty property) {
		super(property);
	}

	@Override
	public ManaCost get(final Game game, final Card card, final EffectData data) {
		if (object instanceof ManaCostExtractor extractor)
			return extractor.get(game, card, data);

		if (object instanceof ManaCost mc)
			return mc;

		return ((ManaCostProperty) object).get();
	}

}
