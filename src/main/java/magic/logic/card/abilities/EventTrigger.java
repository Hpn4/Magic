package magic.logic.card.abilities;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.condition.SelectorCondition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.game.PilePart;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class EventTrigger extends TriggeredAbilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2085534731361507678L;

	private final Event event;

	private final Condition condition;

	private final Effect effect;

	private Place activationZone;

	private transient EffectData saved;

	public EventTrigger(final Event event, final Condition condition, final Effect effect, final Place activationZone) {
		this.event = event;
		this.condition = condition;
		this.effect = effect;
		this.activationZone = activationZone;
	}

	public EventTrigger(final Event event, final Condition condition, final Effect effect) {
		this.event = event;
		this.condition = condition;
		this.effect = effect;
		activationZone = Place.BATTLEFIELD;
	}

	public EventTrigger(final Event event, final Selectors selector, final Effect effect) {
		this.event = event;
		this.condition = new SelectorCondition(selector, TargetType.RETURNED_TARGET);
		this.effect = effect;
		activationZone = Place.BATTLEFIELD;
	}

	public EventTrigger(final Event event, final Effect effect) {
		this.event = event;
		this.condition = null;
		this.effect = effect;
		activationZone = Place.BATTLEFIELD;
	}

	@Override
	public boolean canEventExecuted(final Game game, final Card card, final Object object) {
		// Pour optimiser les calculs on le met en premier car c'est la condition qui à
		// le plus de chance d'echouer (carte du cimetiere, exile)
		if (activationZone != card.getPlace())
			return false;

		final PilePart part = (PilePart) object;
		if (part != null) {
			if (part.getEvent() != event)
				return false;

			saved = new EffectData(part.getData(), part.getTargets());
			if (condition != null && !condition.test(game, card, saved))
				return false;
		}

		return true;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		if (activationZone != card.getPlace())
			return false;

		return effect.canBeExecuted(game, card, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		effect.setup(game, thisCard, a);
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		// On refait le test (il est possible qu'apres que l'event soit mis sur la
		// pile, l'adversaire fasse une action en reponse qui emepeche la condition de
		// rester valide
		if (condition != null) {
			if (condition.test(game, thisCard, saved)) {
				// On donne les cibles les plus pertinentes à l'effet
				final Targetable[] targets = condition.getResultedTargets() == null ? saved.getTargets()
						: condition.getResultedTargets();

				// On donne le plus d'element possible à l'effet avec les données de l'event
				// enregistré et les cibles pertinentes.
				return effect.applyEffect(game, thisCard, new EffectData(saved.getData(), targets));
			} else
				return null;
		}

		return effect.applyEffect(game, thisCard, new EffectData(null, thisCard));
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		saved = null;
		effect.cleanup(game, thisCard);
	}

	public Event getEvent() {
		return event;
	}

	public Condition getCondition() {
		return condition;
	}

	public Effect getEffect() {
		return effect;
	}

	public Place getActivationZone() {
		return activationZone;
	}

	public void setActivationZone(final Place activationZone) {
		this.activationZone = activationZone;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(condition, effect, event, activationZone);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof EventTrigger other)
			return Objects.equals(condition, other.condition) && Objects.equals(effect, other.effect)
					&& event == other.event && activationZone == other.activationZone;

		return false;
	}

}
