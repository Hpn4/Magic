package magic.logic.card.abilities.effect.linker;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class MayEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2300778195855989973L;

	private Effect mayEffect;

	private Effect ifEffectDo;

	private Effect ifEffectNotDo;

	private byte wantToDo;

	public MayEffect(final Effect mayEffect, final Effect ifEffectDo) {
		this.mayEffect = mayEffect;
		this.ifEffectDo = ifEffectDo;
	}

	public MayEffect(final Effect mayEffect, final Effect ifEffectDo, final Effect ifEffectNotDo) {
		this.mayEffect = mayEffect;
		this.ifEffectDo = ifEffectDo;
		this.ifEffectNotDo = ifEffectNotDo;
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		wantToDo = game.playerWantToDo(mayEffect, a) ? (byte) 1 : 0;

		if (wantToDo == 1) {
			mayEffect.setup(game, thisCard, a);

			if (ifEffectDo != null)
				ifEffectDo.setup(game, thisCard, a);
		} else if (wantToDo == 0 && ifEffectNotDo != null)
			ifEffectNotDo.setup(game, thisCard, a);

	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		if (wantToDo == 1) {
			final boolean test = mayEffect.canBeExecuted(game, thisCard, object);

			if (ifEffectDo != null)
				return test && ifEffectDo.canBeExecuted(game, thisCard, object);

			return test;
		} else if (wantToDo == 0 && ifEffectNotDo != null)
			return ifEffectNotDo.canBeExecuted(game, thisCard, object);

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		if (wantToDo == 1) {
			EffectData effect = mayEffect.applyEffect(game, thisCard, effectData);
			if (ifEffectDo != null)
				effect = ifEffectDo.applyEffect(game, thisCard, effect);

			return effect;
		} else if (ifEffectNotDo != null)
			return ifEffectNotDo.applyEffect(game, thisCard, effectData);

		return effectData;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		mayEffect.cleanup(game, thisCard);

		if (ifEffectDo != null)
			ifEffectDo.cleanup(game, thisCard);

		if (ifEffectNotDo != null)
			ifEffectNotDo.cleanup(game, thisCard);

		wantToDo = -1;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ifEffectDo, ifEffectNotDo, mayEffect, wantToDo);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof MayEffect other)
			return Objects.equals(ifEffectDo, other.ifEffectDo) && Objects.equals(ifEffectNotDo, other.ifEffectNotDo)
					&& Objects.equals(mayEffect, other.mayEffect) && wantToDo == other.wantToDo;

		return false;
	}

}
