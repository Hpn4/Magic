package magic.logic.card.abilities;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class SpellAbilities extends Abilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4249083725413942104L;

	private final Effect effect;

	public SpellAbilities(final Effect effect) {
		this.effect = effect;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		return effect.canBeExecuted(game, card, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		effect.setup(game, thisCard, a);
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		return effect.applyEffect(game, thisCard, new EffectData());
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(effect);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof SpellAbilities other)
			return Objects.equals(effect, other.effect);

		return false;
	}

}
