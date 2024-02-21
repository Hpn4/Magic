package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class AttachCardEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5574129693087989670L;

	public AttachCardEffect(final Selectors select) {
		super(select, TargetType.ALL_CARD);
		select.maxTarget(1);
	}

	public AttachCardEffect(final Selectors select, final TargetType targetType) {
		super(select, targetType);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		final Event event = thisCard.hasType(CardT.ENCHANTMENTS) ? Event.ENCHANT : Event.EQUIP;

		// Pour equiper un artefact, il faut deja le déséquipper
		if (event == Event.EQUIP)
			game.pushEvent(Event.UNATTACHED, thisCard, null, thisCard);

		game.pushPile(thisCard, event, null, targets);

		return new EffectData(null, targets);
	}
}
