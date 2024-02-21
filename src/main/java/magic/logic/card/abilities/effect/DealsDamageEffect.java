package magic.logic.card.abilities.effect;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Nmbs;

public class DealsDamageEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5130545494882605065L;

	private final Nmbs damage;

	public DealsDamageEffect(final Selectors select, final TargetType targetType, final int damage) {
		this(select, targetType, new Nmbs(damage));
	}

	public DealsDamageEffect(final Selectors select, final TargetType targetType, final Nmb extractor) {
		this(select, targetType, new Nmbs(extractor));
	}

	public DealsDamageEffect(final Selectors select, final TargetType targetType, final Nmbs nmbs) {
		super(select, targetType);
		damage = nmbs;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		final int count = damage.get(game, thisCard, effectData);
		game.pushPile(thisCard, Event.DEALT_DAMAGE, count, targets);

		return new EffectData(count, targets);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + damage.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		final DealsDamageEffect other = (DealsDamageEffect) obj;
		return damage.equals(other.damage);
	}

}
