package magic.main.set;

import magic.logic.card.abilities.condition.NumberCondition;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.SacrificeEffect;
import magic.logic.card.abilities.effect.linker.ConditionEffect;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.selector.Operator;
import magic.logic.utils.value.Nmbs;

public class Sagas {

	private final int max;

	private ConditionEffect condition;

	private final Nmbs nmb;

	public Sagas(final CounterType type, final int max) {
		this.max = max;

		nmb = new Nmb().getCounter(TargetType.THIS_CARD, type).s();
	}

	public void first() {

	}

	public void effect(final int number, final Operator op, final Effect effect) {
		final NumberCondition nmbCondition = new NumberCondition(nmb, op, number);

		condition = new ConditionEffect(nmbCondition, effect, condition);
	}

	public void end() {
		final NumberCondition nmbCondition = new NumberCondition(nmb, Operator.EQUAL, max);
		condition = new ConditionEffect(nmbCondition, new SacrificeEffect(null, TargetType.THIS_CARD), condition);
	}
}
