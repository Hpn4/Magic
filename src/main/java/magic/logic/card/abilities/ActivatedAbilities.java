package magic.logic.card.abilities;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.CostEffect;
import magic.logic.card.abilities.effect.CostableEffect;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.LoyaltyCostEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.place.Place;

public class ActivatedAbilities extends Abilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = -62662515928876930L;

	private final CostableEffect cost;

	private final Effect effect;

	private Condition condition;

	private Restriction restriction;

	private Place activationZone;

	private boolean executed;

	public ActivatedAbilities(final CostableEffect cost, final Effect effect) {
		this.cost = cost;
		this.effect = effect;
		this.activationZone = Place.BATTLEFIELD;
	}

	public ActivatedAbilities(final CostableEffect cost, final Effect effect, final Condition condition,
			final Restriction restriction) {
		this.cost = cost;
		this.effect = effect;
		this.condition = condition;
		this.restriction = restriction;
		this.activationZone = Place.BATTLEFIELD;
	}

	public ActivatedAbilities(final CostableEffect cost, final Effect effect, final Condition condition,
			final Restriction restriction, final Place activationZone) {
		this.cost = cost;
		this.effect = effect;
		this.condition = condition;
		this.restriction = restriction;
		this.activationZone = activationZone;
	}

	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		// Si la carte n'est pas dans la bonne zone pour effectuer l'effet, pas bon
		// Ex : Si une créature est en exil ou cimetierre
		if (thisCard.getPlace() != activationZone)
			return false;

		// On vérifie que la carte peut bien activer la capacité
		if (!thisCard.getCardAbilities().can(game, thisCard, Interdiction.ACTIVATE_ABILITY, this))
			return false;

		// Vérifier niveau globale

		if (restriction != null) {
			boolean match = true;
			if (restriction == Restriction.ONLY_AS_A_SORCERY)
				match = game.canPlaySorcery(thisCard);
			else
				match = executed;

			if (!match)
				return false;
		}

		if (condition != null && !condition.test(game, thisCard, new EffectData()))
			return false;

		return cost.canBeExecuted(game, thisCard, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		cost.setup(game, thisCard, a);
		effect.setup(game, thisCard, a);
	}

	public void paidCost(final Game game, final Card thisCard) {
		cost.applyEffect(game, thisCard, new EffectData());
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		if (condition != null && condition.test(game, thisCard, new EffectData()))
			return effect.applyEffect(game, thisCard, new EffectData(condition.getResultedTargets()));

		return effect.applyEffect(game, thisCard, new EffectData());
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		cost.cleanup(game, thisCard);
		effect.cleanup(game, thisCard);
	}

	public void endTurn() {
		executed = false;
	}

	/**
	 * 
	 * @return Le cout d'activation
	 */
	public CostableEffect getCost() {
		return cost;
	}

	/**
	 * 
	 * @return L'effet a éxecuter une fois le coût payé
	 */
	public Effect getEffect() {
		return effect;
	}

	public Place getActivationZone() {
		return activationZone;
	}

	public void setActivationZone(final Place activationPlace) {
		this.activationZone = activationPlace;
	}

	public boolean manaAbilities() {
		return effect.isManaEffect();
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(condition, cost, effect, executed, restriction, activationZone);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof ActivatedAbilities other)
			return Objects.equals(condition, other.condition) && Objects.equals(cost, other.cost)
					&& Objects.equals(effect, other.effect) && executed == other.executed
					&& restriction == other.restriction && activationZone == other.activationZone;

		return false;
	}

	/**
	 * Creer une Capacites déclanché par une modification de la loyaute
	 * (Planeswalker). Le nombre de marqueur a enelever ou a ajouter est définit par
	 * {@code loyalty}. Ensuite l'effet {@code effect} est executé.
	 * 
	 * @param loyalty Le nombre de marqueur de loyauté a gagner ou a perdre pour
	 *                activer l'effet. Ou null si X marqueur a enlever
	 * @param effect  L'effet à executer
	 * @return Une nouvelle ActivatedAbilities
	 */
	public static ActivatedAbilities loyalty(final Integer loyalty, final Effect effect) {
		return new ActivatedAbilities(new LoyaltyCostEffect(loyalty), effect);
	}

	public static ActivatedAbilities cost(final ManaCost cost, final Effect effect) {
		return new ActivatedAbilities(new CostEffect(cost, false, null), effect);
	}

	public static ActivatedAbilities cost(final ManaCost cost, final boolean tap, final Effect effect) {
		return new ActivatedAbilities(new CostEffect(cost, tap, null), effect);
	}

	public static ActivatedAbilities cost(final ManaCost cost, final boolean tap, final Effect additionalCost,
			final Effect effect) {
		return new ActivatedAbilities(new CostEffect(cost, tap, additionalCost), effect);
	}
}
