package magic.logic.utils.value;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.property.CardTypeProperty;

public class CardTypes extends Value<CardT[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4608386799427329947L;

	public CardTypes(final CardT... cardTypes) {
		super(cardTypes);
	}

	public CardTypes(final CardTypeProperty property) {
		super(property);
	}

	@Override
	public CardT[] get(final Game game, final Card thisCard, final EffectData data) {
		if (object instanceof CardT[] types)
			return types;

		return ((CardTypeProperty) object).get();
	}

}
