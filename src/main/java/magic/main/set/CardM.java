package magic.main.set;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.Cant;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.EventTrigger;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.SpellAbilities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.condition.NumberCondition;
import magic.logic.card.abilities.condition.ThisCardCondition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.linker.E;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.selector.Operator;
import magic.logic.utils.selector.Selectors;

public class CardM {

	protected static final ThisCardCondition condition = new ThisCardCondition();

	protected final AbstractSet set;

	protected final Card card;

	public CardM(final AbstractSet set, final String name) {
		this.set = set;
		card = set.get(name);
	}

	public void ETB(final Effect effect) {
		whenThis(Event.ENTER_BATTLEFIELD, effect);
	}

	public void die(final Effect effect) {
		whenThis(Event.DIES, effect);
	}

	public void whenThis(final Event event, final Effect effect) {
		set.addAbility(new EventTrigger(event, condition, effect));
	}
	
	public void add(final Abilities...abilities) {
		set.addAbility(abilities);
	}

	public void capacities(final Capacities... capacities) {
		card.addCapacities(capacities);
	}

	public void interdiction(final Interdiction... interdiction) {
		for (final Interdiction inter : interdiction)
			card.getCardAbilities().addInterdiction(new Cant(inter));
	}

	public void protectionFrom(final Selectors select) {
		interdiction(Interdiction.BE_BLOCKED, select);
		interdiction(Interdiction.BE_THE_TARGET, select);
		interdiction(Interdiction.DEALT_DAMAGE, select);
		interdiction(Interdiction.BE_EQUIPPED, select);
		interdiction(Interdiction.BE_ENCHANTED, select);
	}

	public void interdiction(final Interdiction interdiction, final Selectors select) {
		card.getCardAbilities().addInterdiction(new Cant(interdiction, select));
	}

	public void interdiction(final Interdiction interdiction, final Condition cond) {
		card.getCardAbilities().addInterdiction(new Cant(interdiction, cond));
	}

	public void end(final Abilities... abilities) {
		set.writeCard(abilities);
	}

	public void spellAbilities(final Effect effect) {
		set.addAbility(new SpellAbilities(effect));
	}

	public void activatedAbilities(final Effect effect) {
		set.addAbility(ActivatedAbilities.cost(null, true, effect));
	}

	public void activatedAbilities(final Mana mana, final Effect effect) {
		set.addAbility(ActivatedAbilities.cost(new ManaCost(mana), effect));
	}

	public void activatedAbilities(final ManaCost cost, final Effect effect) {
		set.addAbility(ActivatedAbilities.cost(cost, effect));
	}

	public void activatedAbilities(final ManaCost cost, final Effect additionalCost, final Effect effect) {
		set.addAbility(ActivatedAbilities.cost(cost, false, additionalCost, effect));
	}

	public void adapt(final ManaCost cost, final int counter) {
		// On ajoute les maarqueurs
		final Effect e = E.addCounter(CounterType.PLUS_ONE, counter);

		// Si la créature n'a pas de marqueurs sur elle
		final Condition cond = new NumberCondition(new Nmb().getCounter(TargetType.THIS_CARD, CounterType.PLUS_ONE).s(),
				Operator.EQUAL, 0);

		ActivatedAbilities.cost(cost, E.condition(cond, e));
	}

	// RAVNICA
	public void riot() {
		final Effect e = E.may(E.addCounter(CounterType.PLUS_ONE, 1), null, E.gains(Capacities.HASTE));

		ETB(e);
	}
}
