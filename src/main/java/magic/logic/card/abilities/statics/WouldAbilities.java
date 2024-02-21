package magic.logic.card.abilities.statics;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.game.PilePart;
import magic.logic.place.Place;

public class WouldAbilities extends StaticAbilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 347043508323071688L;

	private final Event event;

	private final Condition condition;

	private final Effect effect;

	private Place activationZone;

	private transient EffectData saved;

	private boolean exec;

	public WouldAbilities(final Event event, final Condition condition, final Effect effect) {
		this.event = event;
		this.condition = condition;
		this.effect = effect;
		exec = false;
		activationZone = Place.BATTLEFIELD;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		final PilePart part = (PilePart) object;
		if (part != null) {

			if (part.getEvent() != event)
				return false;

			saved = new EffectData(part.getData(), part.getTargets());
			if (condition != null && condition.test(game, card, saved))
				return false;
		}

		if (activationZone != card.getPlace() || exec)
			return false;

		return effect.canBeExecuted(game, card, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities ab) {
		effect.setup(game, thisCard, ab);
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		exec = true;
		if (condition != null) {
			final EffectData data = new EffectData(saved.getData(), condition.getResultedTargets());
			return effect.applyEffect(game, thisCard, data);
		} else
			return effect.applyEffect(game, thisCard, saved);
	}

	public void reset() {
		exec = false;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
		saved = null;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(activationZone, condition, effect, event, exec);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof WouldAbilities other) {
			return activationZone == other.activationZone && Objects.equals(condition, other.condition)
					&& Objects.equals(effect, other.effect) && event == other.event && exec == other.exec
					&& Objects.equals(saved, other.saved);
		}

		return false;
	}

}
