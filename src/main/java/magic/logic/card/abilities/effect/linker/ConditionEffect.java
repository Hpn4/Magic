package magic.logic.card.abilities.effect.linker;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class ConditionEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8296623220437696565L;

	private final Condition condition;

	private final Effect ifTrue;

	private final Effect ifFalse;

	public ConditionEffect(final Condition condition, final Effect ifTrueEffect) {
		this.condition = condition;
		ifTrue = ifTrueEffect;
		ifFalse = null;
	}

	public ConditionEffect(final Condition condition, final Effect ifTrueEffect, final Effect ifFalseEffect) {
		this.condition = condition;
		ifTrue = ifTrueEffect;
		ifFalse = ifFalseEffect;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		final boolean result = condition.test(game, card, new EffectData());
		if (result)
			return ifTrue.canBeExecuted(game, card, object);
		else if (ifFalse != null)
			return ifFalse.canBeExecuted(game, card, object);

		return false;

	}

	@Override
	public void setup(final Game game, final Card card, final Abilities a) {
		ifTrue.setup(game, card, a);
		if (ifFalse != null)
			ifFalse.setup(game, card, a);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		if (condition.test(game, thisCard, effectData))
			return ifTrue.applyEffect(game, thisCard, effectData);
		else if (ifFalse != null)
			return ifFalse.applyEffect(game, thisCard, effectData);

		return effectData;
	}

	@Override
	public void cleanup(final Game game, final Card card) {
		ifTrue.cleanup(game, card);
		if (ifFalse != null)
			ifFalse.cleanup(game, card);
	}

	@Override
	public int hashCode() {
		return Objects.hash(condition, ifFalse, ifTrue);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ConditionEffect other)
			return Objects.equals(condition, other.condition) && Objects.equals(ifFalse, other.ifFalse)
					&& Objects.equals(ifTrue, other.ifTrue);

		return false;
	}

}
