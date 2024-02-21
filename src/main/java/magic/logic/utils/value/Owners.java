package magic.logic.utils.value;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.extractor.OwnerExtractor;
import magic.logic.utils.property.OwnerProperty;

public class Owners extends Value<Owner> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6130381496358893323L;

	public Owners(final Owner owner) {
		super(owner);
	}

	public Owners(final OwnerExtractor owner) {
		super(owner);
	}

	public Owners(final OwnerProperty owner) {
		super(owner);
	}

	@Override
	public Owner get(final Game game, final Card card, final EffectData data) {
		if (object instanceof OwnerExtractor extractor)
			return extractor.get(game, card, data);

		if (object instanceof Owner owner)
			return owner;

		return ((OwnerProperty) object).get();
	}

}
