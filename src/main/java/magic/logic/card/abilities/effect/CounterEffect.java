package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Counter;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Nmbs;

public class CounterEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3994692142641359777L;

	private final CounterType type;

	private final Nmbs counterCount;

	private final boolean addCount;

	public CounterEffect(final CounterType type, final int count) {
		this(null, TargetType.ALL_CARD, type, count, true);
	}

	public CounterEffect(final Selectors selector, final TargetType target, final CounterType type, final int number,
			final boolean addCounter) {
		this(selector, target, type, new Nmbs(number), addCounter);
	}

	public CounterEffect(final Selectors selector, final TargetType target, final CounterType type, final Nmb nmb,
			final boolean addCounter) {
		this(selector, target, type, new Nmbs(nmb), addCounter);
	}

	public CounterEffect(final Selectors selector, final TargetType target, final CounterType type, final Nmbs nmb,
			final boolean addCounter) {
		super(selector, target);
		this.type = type;
		this.addCount = addCounter;
		counterCount = nmb;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		final int counter = counterCount.get(game, thisCard, effectData);

		Counter count = new Counter(type, counter);
		for (final Targetable target : targets) {
			if (!addCount && !game.can(Interdiction.PUT_COUNTERS, thisCard, target))
				continue;

			game.pushEvent(addCount ? Event.ADDED_COUNTER : Event.REMOVED_COUNTER, thisCard, count, target);
		}

		return new EffectData(count, targets);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(addCount, counterCount, type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof CounterEffect other)
			return addCount == other.addCount && Objects.equals(counterCount, other.counterCount) && type == other.type;

		return false;
	}

	@Override
	public String toString() {
		return (addCount ? "Add" : "Remove") + " " + counterCount.toString() + " " + type.toString().toLowerCase() + " counter";
	}

}
