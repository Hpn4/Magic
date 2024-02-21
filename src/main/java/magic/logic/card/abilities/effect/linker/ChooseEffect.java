package magic.logic.card.abilities.effect.linker;

import java.util.Arrays;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class ChooseEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7653741976750657915L;

	private final Effect afterChoosing;

	private final Effect[] effects;

	private Effect[] choosedEffects;

	private final int min;

	private final int max;

	public ChooseEffect(final Effect afterChoosing, final int min, final int max, final Effect... effects) {
		this.afterChoosing = afterChoosing;
		this.min = min;
		this.max = max;
		this.effects = effects;
	}

	@Override
	public void setup(final Game game, final Card card, final Abilities a) {
		choosedEffects = game.chooseModal(min, max, a, effects);
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		for (int i = 0; i < choosedEffects.length; i++)
			if (!choosedEffects[i].canBeExecuted(game, card, object))
				return false;

		if (afterChoosing != null)
			return afterChoosing.canBeExecuted(game, card, object);

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final int size = choosedEffects.length;

		EffectData data = new EffectData(size);
		if (afterChoosing != null)
			data = afterChoosing.applyEffect(game, thisCard, data);

		for (int i = 0; i < size; i++)
			data = choosedEffects[i].applyEffect(game, thisCard, data);

		return data;
	}

	@Override
	public void cleanup(final Game game, final Card card) {
		// On clean tout les autres effets
		for (int i = 0; i < choosedEffects.length; i++)
			choosedEffects[i].cleanup(game, card);

		if (afterChoosing != null)
			afterChoosing.cleanup(game, card);

		// On vide la liste des effets choisies
		choosedEffects = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + Arrays.hashCode(effects);
		return prime * result + Objects.hash(afterChoosing, max, min);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ChooseEffect other)
			return Objects.equals(afterChoosing, other.afterChoosing) && Arrays.equals(effects, other.effects)
					&& max == other.max && min == other.min;

		return false;
	}

}
