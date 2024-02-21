package magic.logic.card.abilities.effect.inversable;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class AddAbilitiesEffect extends AbstractTargetEffect implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5417815328559676029L;

	private final Abilities abilities;

	public AddAbilitiesEffect(final Selectors select, final TargetType targetType, final Abilities abilities) {
		super(select, targetType);
		this.abilities = abilities;
	}

	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushEvent(AGMEvent.ADD_ABILITIES, thisCard, abilities, targets);

		return new EffectData(abilities, targets);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		game.pushEvent(AGMEvent.REMOVE_ABILITIES, thisCard, getValue(data), data.getTargets());
	}

	@Override
	public Object getData() {
		return abilities;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + abilities.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		return abilities.equals(((AddAbilitiesEffect) obj).abilities);
	}

}
