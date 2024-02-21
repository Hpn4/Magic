package magic.logic.card.abilities.statics;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.place.Place;

public class AsLongAsAbilities extends StaticAbilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2552181501090377124L;

	private final Condition condition;

	private final InversableEffect effect;

	private Place activationZone;

	private EffectData saved;

	private boolean effectApplied;

	public AsLongAsAbilities(final Condition condition, final InversableEffect effect) {
		this(condition, effect, Place.BATTLEFIELD);
	}

	public AsLongAsAbilities(final Condition condition, final InversableEffect effect, final Place activationZone) {
		this.condition = condition;
		this.effect = effect;
		effectApplied = false;
		this.activationZone = activationZone;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		return activationZone == card.getPlace() && condition.test(game, card, new EffectData());
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		effect.setup(game, thisCard, a);

	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		if (condition.test(game, thisCard, new EffectData(null, thisCard))) {
			effectApplied = true;
			final EffectData ed = effect.applyEffect(game, thisCard,
					new EffectData(null, condition.getResultedTargets()));

			saved = ed;

			return ed;
		}

		return null;
	}

	public void invert(final Game game, final Card card) {
		if (effectApplied) {
			effectApplied = false;
			effect.invertEffect(game, card, saved);
			saved = null;
		}
	}

	public boolean isEffectApplyed() {
		return effectApplied;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(activationZone, condition, effect, effectApplied);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof AsLongAsAbilities other)
			return activationZone == other.activationZone && Objects.equals(condition, other.condition)
					&& Objects.equals(effect, other.effect) && effectApplied == other.effectApplied
					&& Objects.equals(saved, other.saved);

		return false;
	}

}
