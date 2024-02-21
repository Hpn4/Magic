package magic.logic.utils.value;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.property.NumberProperty;

public class Nmbs extends Value<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4725829562941397275L;

	public Nmbs(final Integer nmb) {
		super(nmb);
	}

	public Nmbs(final Nmb nmb) {
		super(nmb);
	}

	public Nmbs(final NumberProperty nmb) {
		super(nmb);
	}

	@Override
	public Integer get(final Game game, final Card card, final EffectData data) {
		if (object instanceof Nmb nmb)
			return nmb.get(game, card, data);

		if (object instanceof Integer nmb)
			return nmb;

		return ((NumberProperty) object).get();
	}

}
