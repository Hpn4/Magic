package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Counter;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;

public class LoyaltyCostEffect implements CostableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7081545945052989649L;

	private final Integer count;

	private int cost;

	public LoyaltyCostEffect(final Integer count) {
		this.count = count;
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		if (count == null)
			cost = game.chooseX(a);

		cost = count;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		// On vérifie que le planeswalker a suffisement de marqueur loyauté sur lui
		if (object instanceof String str && str.equals("cost") && thisCard.getCounter(CounterType.LOYALTY) < cost)
			return false;

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Counter counter = new Counter(CounterType.LOYALTY, cost);

		game.pushPile(thisCard, cost < 0 ? Event.REMOVED_COUNTER : Event.ADDED_COUNTER, counter, thisCard);

		return new EffectData(counter, thisCard);
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		cost = 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cost, count);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof LoyaltyCostEffect other)
			return cost == other.cost && Objects.equals(count, other.count);

		return false;
	}

}
