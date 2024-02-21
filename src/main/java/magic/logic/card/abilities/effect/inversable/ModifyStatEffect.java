package magic.logic.card.abilities.effect.inversable;

import magic.logic.card.Card;
import magic.logic.card.Stat;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Nmbs;

public class ModifyStatEffect extends AbstractTargetEffect implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8564022555639348706L;

	private Nmbs power;

	private Nmbs toughness;

	private Stat memory;

	public ModifyStatEffect(final Selectors select, final TargetType targetType, final Stat stat) {
		super(select, targetType);
		power = new Nmbs(stat.getPower());
		toughness = new Nmbs(stat.getToughness());
	}

	public ModifyStatEffect(final Selectors select, final TargetType targetType, final Nmbs power,
			final Nmbs toughness) {
		super(select, targetType);
		this.power = power;
		this.toughness = toughness;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		final int pow = power.get(game, thisCard, effectData);
		final int toug = toughness.get(game, thisCard, effectData);

		memory = new Stat(pow, toug);

		game.pushPile(thisCard, AGMEvent.ADD_STAT, memory, targets);

		return new EffectData(memory, targets);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		game.pushPile(thisCard, AGMEvent.REMOVE_STAT, getValue(data), data.getTargets());
	}

	/**
	 * On renvoie une copie de l'objet
	 */
	@Override
	public Object getData() {
		return new Stat(memory);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + power.hashCode();
		result = prime * result + toughness.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		final ModifyStatEffect other = (ModifyStatEffect) obj;
		return power.equals(other.power) && toughness.equals(other.toughness);
	}

}
