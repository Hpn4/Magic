package magic.logic.card.abilities;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Step;
import magic.logic.place.Place;

public class StepTrigger extends TriggeredAbilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2215575839913122727L;

	private final Step step;

	private final Owner player;

	private final Condition condition;

	private final Effect effect;

	private Place activationZone;

	private boolean executedThisStep;

	public StepTrigger(final Step step, final Owner player, final Condition condition, final Effect effect) {
		this(step, player, condition, effect, Place.BATTLEFIELD);
	}

	public StepTrigger(final Step step, final Owner player, final Condition condition, final Effect effect,
			final Place activationZone) {
		this.step = step;
		this.player = player;
		this.condition = condition;
		this.effect = effect;
		this.executedThisStep = false;
		this.activationZone = activationZone;
	}

	@Override
	public boolean canEventExecuted(final Game game, final Card card, final Object object) {
		if (game.getStep() == step && game.getPlayingPlayer() == game.ownerToPlayer(player, card)
				&& !executedThisStep) {

			if (activationZone != card.getPlace())
				return false;

			if (condition != null && !condition.test(game, card, new EffectData(object, card)))
				return false;

			return true;
		}

		return false;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		if (activationZone != card.getPlace())
			return false;

		if (game.getStep() != step)
			executedThisStep = false;

		return effect.canBeExecuted(game, card, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		effect.setup(game, thisCard, a);
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		// On refait le test (il est possible qu'après que l'event soit mis sur la
		// pile,
		// l'adversaire fasse une aaction en réponse qui emepeche la condition de
		// rester
		// valide
		if (condition != null) {
			if (condition.test(game, thisCard, new EffectData(null, thisCard)))
				return effect.applyEffect(game, thisCard, new EffectData(null, condition.getResultedTargets()));
			else
				return null;
		}

		return effect.applyEffect(game, thisCard, new EffectData(null, thisCard));
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(activationZone, condition, effect, executedThisStep, player, step);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof StepTrigger other)
			return activationZone == other.activationZone && Objects.equals(condition, other.condition)
					&& Objects.equals(effect, other.effect) && executedThisStep == other.executedThisStep
					&& player == other.player && step == other.step;

		return false;
	}

}
