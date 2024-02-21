package magic.logic.card.abilities.effect.inversable;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.CardTypes;

public class AddCardTypeEffect extends AbstractTargetEffect implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7440649719479130111L;

	private final CardTypes types;

	public AddCardTypeEffect(final Selectors select, final TargetType targetType, final CardT... cardTypes) {
		super(select, targetType);
		types = new CardTypes(cardTypes);
	}

	public AddCardTypeEffect(final Selectors select, final TargetType targetType, final CardTypes cardTypes) {
		super(select, targetType);
		types = cardTypes;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		final CardT[] cardTypes = types.get(game, thisCard, effectData);
		types.set(cardTypes);

		game.pushPile(thisCard, AGMEvent.ADD_CARD_TYPE, cardTypes, targets);

		return new EffectData(types, targets);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		game.pushPile(thisCard, AGMEvent.REMOVE_CARD_TYPE, getValue(data), data.getTargets());
	}

	@Override
	public Object getData() {
		return types.get(null, null, null);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + types.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		return types.equals(((AddCardTypeEffect) obj).types);
	}

}
