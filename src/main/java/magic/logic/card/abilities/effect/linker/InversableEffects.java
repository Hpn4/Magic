package magic.logic.card.abilities.effect.linker;

import java.util.ArrayList;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class InversableEffects implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9007716178445639840L;

	private final ArrayList<InversableEffect> effects;

	public InversableEffects() {
		effects = new ArrayList<>();
	}

	public InversableEffects(final InversableEffect effect) {
		effects = new ArrayList<>();
		effects.add(effect);
	}

	public InversableEffects add(final InversableEffect effect) {
		effects.add(effect);
		return this;
	}

	public int getSize() {
		return effects.size();
	}

	public InversableEffect get(final int index) {
		return effects.get(index);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		for (final InversableEffect effect : effects)
			effect.setup(game, thisCard, a);
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		for (final InversableEffect effect : effects)
			if (!effect.canBeExecuted(game, thisCard, object))
				return false;

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		EffectData data = effectData;
		for (final InversableEffect effect : effects)
			data = effect.applyEffect(game, thisCard, data);

		return data;
	}

	public void cleanup(final Game game, final Card thisCard) {
		for (final InversableEffect effect : effects)
			effect.cleanup(game, thisCard);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		final Object[] datas = (Object[]) data.getData();
		int i = 0;
		for (final InversableEffect effect : effects) {
			// On incremente l'index
			datas[0] = i++;
			effect.invertEffect(game, thisCard, new EffectData(datas, data.getTargets()));
		}
	}

	// Retourne un tableau des donné de tout les effets inversable
	@Override
	public Object getData() {
		final Object[] datas = new Object[effects.size() + 1];
		for (int i = 1; i < datas.length; i++)
			datas[i] = effects.get(i - 1).getData();
		datas[0] = 0;
		return datas;
	}

	@Override
	public int hashCode() {
		return Objects.hash(effects);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof InversableEffects other)
			return Objects.equals(effects, other.effects);

		return false;
	}

}
