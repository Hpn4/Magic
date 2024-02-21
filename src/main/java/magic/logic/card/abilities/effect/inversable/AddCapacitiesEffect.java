package magic.logic.card.abilities.effect.inversable;

import java.util.Arrays;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class AddCapacitiesEffect extends AbstractTargetEffect implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5837507697333961451L;

	private final Capacities[] capacities;

	public AddCapacitiesEffect(final Selectors select, final TargetType target, final Capacities... evergreens) {
		super(select, target);
		capacities = evergreens;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		game.pushEvent(AGMEvent.ADD_CAPACITIES, thisCard, capacities, targets);

		return new EffectData(capacities, targets);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		game.pushEvent(AGMEvent.REMOVE_CAPACITIES, thisCard, data.getData(), data.getTargets());
	}

	@Override
	public Object getData() {
		return capacities;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Arrays.hashCode(capacities);
	}

	@Override
	public String toString() {
		final StringBuilder build = new StringBuilder("gains ");

		for (final Capacities c : capacities)
			build.append(c.name().toLowerCase() + " ");

		return build.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		return Arrays.equals(capacities, ((AddCapacitiesEffect) obj).capacities);
	}

}