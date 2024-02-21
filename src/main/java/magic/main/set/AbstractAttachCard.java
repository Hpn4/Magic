package magic.main.set;

import magic.logic.card.Stat;
import magic.logic.card.abilities.Cant;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.EventTrigger;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.effect.inversable.AddCapacitiesEffect;
import magic.logic.card.abilities.effect.inversable.AddInterdictionEffect;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.effect.inversable.ModifyStatEffect;
import magic.logic.card.abilities.effect.linker.InversableEffects;
import magic.logic.card.abilities.utils.Event;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public abstract class AbstractAttachCard extends CardM {

	protected final InversableEffects inversableEffects;

	public AbstractAttachCard(final AbstractSet set, final String name) {
		super(set, name);
		inversableEffects = new InversableEffects();
	}

	/**
	 * Rajoute un effet a la carte attaché
	 * 
	 * @param effect
	 */
	public void addEffect(final InversableEffect effect) {
		inversableEffects.add(effect);
	}

	public void addCapacities(final Capacities... capacities) {
		addEffect(new AddCapacitiesEffect(null, TargetType.ATTACHED_CARD, capacities));
	}

	public void addStat(final int stat) {
		addStat(new Stat(stat, stat));
	}

	public void addStat(final Stat stat) {
		addEffect(new ModifyStatEffect(null, TargetType.ATTACHED_CARD, stat));
	}

	public void interdiction(final Interdiction interdiction) {
		addEffect(new AddInterdictionEffect(null, TargetType.ATTACHED_CARD, new Cant(interdiction)));
	}

	public void interdiction(final Interdiction interdiction, final Selectors select) {
		addEffect(new AddInterdictionEffect(null, TargetType.ATTACHED_CARD, new Cant(interdiction, select)));
	}

	public void interdiction(final Interdiction interdiction, final Condition cond) {
		addEffect(new AddInterdictionEffect(null, TargetType.ATTACHED_CARD, new Cant(interdiction, cond)));
	}

	protected void end(final Event event) {
		// Si il n'y a qu'un seul effet, on transforme la listee d'effet en un seul par
		// souci de place et d'optimisation
		final InversableEffect effect = inversableEffects.getSize() == 1 ? inversableEffects.get(0) : inversableEffects;

		// Applique les effets lorsque la carte sera attaché
		final EventTrigger attached = new EventTrigger(event, condition, inversableEffects);

		// Les effets sont ensuite retiré lorsque la carte est detaché
		final EventTrigger unattached = new EventTrigger(Event.UNATTACHED, condition, (game, thisCard, effectData) -> {
			effect.invertEffect(game, thisCard, effectData);
			return null;
		});
		
		card.addAbility(unattached);

		set.writeCard(attached);
	}
}
