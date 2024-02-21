package magic.logic.card.abilities.condition;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Step;
import magic.logic.utils.value.Owners;

public class StepCondition extends Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7250195848650984539L;

	private final Step step;

	private final Owners owner;

	public StepCondition(final Step step, final Owners owner) {
		this.step = step;
		this.owner = owner;
	}

	@Override
	public boolean test(final Game game, final Card thisCard, final EffectData dataEffect) {
		// Si le joueur n'est pas précisé, on considère que c'est tout les joueurs

		// Si la phase n'est pas précisé, on considère que c'est bon peut importe la
		// phase
		// Ex : "N'activer cette capacité que durant votre tour"
		final boolean bonStep = step == null ? true : step == game.getStep();
		if (owner == null)
			return bonStep;

		// On récupère le joueur
		final Owner o = owner.get(game, thisCard, dataEffect);
		return bonStep && game.ownerToPlayer(o, thisCard) == game.getPlayingPlayer();
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(owner, step);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof StepCondition other)
			return Objects.equals(owner, other.owner) && step == other.step;

		return false;
	}
}
