package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.State;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.statics.AlternativeCostAbilities;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.utils.Counterable;
import magic.logic.utils.value.ManaCosts;

public class CostEffect implements CostableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4303989837621335830L;

	private final ManaCosts cost;

	private final boolean tap;

	private final Effect additionalCost;

	private transient Targetable parent;

	private transient ManaCost tmpCost;

	public CostEffect(final ManaCost cost, final boolean tap, final Effect additionalCost) {
		this(new ManaCosts(cost), tap, additionalCost);
	}

	public CostEffect(final ManaCosts cost, final boolean tap, final Effect additionalCost) {
		this.cost = cost;
		this.tap = tap;
		this.additionalCost = additionalCost;
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		if (additionalCost != null)
			additionalCost.setup(game, thisCard, a);

		// Si c'est un cout alterantif ou additionel, c'est sur la carte ou on va tester
		// la depense de mana (ne depens� ce mana que pour lancer un sort de cr�ature)
		// TODO AdditionnalCost
		if (a instanceof AlternativeCostAbilities)
			parent = thisCard;
		else
			parent = a;

		if (cost != null) {
			tmpCost = new ManaCost(cost.get(game, thisCard, new EffectData(null, thisCard)));
			tmpCost.setup(game, (Counterable) parent);
		}
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		// Si le cout nécessite à la carte de s'engager et que la carte est déjà
		// engage,
		// pas bon
		if (tap && thisCard.hasState(State.TAPPED))
			return false;

		if (cost != null) {
			// On recupere le mana de la reserve et on en cree une copie
			final ManaCost mc = new ManaCost(game.getPlayer(Owner.YOU, thisCard)[0].getReserve());
			if (mc.isEmpty())
				return false;

			// On paye le prix et on test si le joueur peut bien payer
			if (mc.payBasic(tmpCost, thisCard, parent, game) == null)
				return false;
		}

		// On test le cout addtionel
		if (additionalCost != null)
			return additionalCost.canBeExecuted(game, thisCard, "cost");

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		if (cost != null) {
			if (!(cost.getObject() instanceof ManaCost))
				tmpCost = cost.get(game, thisCard, effectData);
			game.pushEvent(Event.SPEND_MANA, thisCard, tmpCost, game.getPlayer(thisCard.getOwner(), thisCard)[0],
					parent);
		}
		if (tap)
			game.pushEvent(Event.TAPPED, thisCard, null, thisCard);

		final EffectData data = new EffectData(tmpCost, effectData.getTargets());
		if (additionalCost != null)
			return additionalCost.applyEffect(game, thisCard, data);
		return data;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		if (additionalCost != null)
			additionalCost.cleanup(game, thisCard);

		parent = null;
		tmpCost = null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(additionalCost, cost, tap);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CostEffect other)
			return Objects.equals(additionalCost, other.additionalCost) && Objects.equals(cost, other.cost)
					&& tap == other.tap;

		return false;
	}

}
