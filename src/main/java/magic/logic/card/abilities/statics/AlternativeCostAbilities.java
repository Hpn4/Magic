package magic.logic.card.abilities.statics;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.place.Place;

public class AlternativeCostAbilities extends StaticAbilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4031238367435316358L;

	private final Effect effect;

	private final Condition condition;

	private final Place activationZone;

	/**
	 * La phrase qui sera inscrite lorsque le joueur devra choisir la manière de
	 * lancer le sort.</br>
	 * Ex:cout normal ou kicker cost
	 */
	private final String phrase;

	public AlternativeCostAbilities(final Effect effect, final String phrase) {
		this(effect, null, Place.HAND, phrase);
	}

	public AlternativeCostAbilities(final Effect effect, final Condition condition, final Place activationZone,
			final String phrase) {
		this.effect = effect;
		this.condition = condition;
		this.activationZone = activationZone;
		this.phrase = phrase;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		if (activationZone != card.getPlace())
			return false;

		if (condition != null && !condition.test(game, card, new EffectData(null, card)))
			return false;

		return effect.canBeExecuted(game, card, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities ab) {
		effect.setup(game, thisCard, ab);
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		return applyAbilities(game, thisCard, null);
	}

	public EffectData applyAbilities(final Game game, final Card thisCard, final Object info) {
		// On refait le test (il est possible qu'après que l'event soit mis sur la pile,
		// l'adversaire fasse une aaction en réponse qui emepeche la condition de rester
		// valide
		if (condition != null) {
			if (condition.test(game, thisCard, new EffectData(null, thisCard)))
				return effect.applyEffect(game, thisCard, new EffectData(info, condition.getResultedTargets()));
			else
				return null;
		}

		return effect.applyEffect(game, thisCard, new EffectData(info, thisCard));
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(activationZone, condition, effect, phrase);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof AlternativeCostAbilities other)
			return activationZone == other.activationZone && Objects.equals(condition, other.condition)
					&& Objects.equals(effect, other.effect) && Objects.equals(phrase, other.phrase);

		return false;
	}

	@Override
	public String toString() {
		return phrase == null ? "Alternative" : phrase + " cost";
	}
}
