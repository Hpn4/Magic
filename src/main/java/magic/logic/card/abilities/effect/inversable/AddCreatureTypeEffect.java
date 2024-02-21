package magic.logic.card.abilities.effect.inversable;

import java.util.ArrayList;

import magic.logic.card.Card;
import magic.logic.card.CreatureT;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.CreatureTypes;

public class AddCreatureTypeEffect extends AbstractTargetEffect implements InversableEffect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 807916976857278114L;

	private final CreatureTypes creatureTypes;

	public AddCreatureTypeEffect(final Selectors select, final TargetType targetType, final CreatureT... types) {
		super(select, targetType);
		creatureTypes = new CreatureTypes(types);
	}

	public AddCreatureTypeEffect(final Selectors select, final TargetType targetType,
			final CreatureTypes creatureTypes) {
		super(select, targetType);
		this.creatureTypes = creatureTypes;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Targetable[] targets = getTargets(game, effectData, thisCard);

		final ArrayList<CreatureT> types = creatureTypes.get(game, thisCard, effectData);
		// On modify la valeur, faire attention
		creatureTypes.set(types);

		game.pushPile(thisCard, AGMEvent.ADD_CREATURE_TYPE, types, targets);

		return new EffectData(types, targets);
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		game.pushPile(thisCard, AGMEvent.REMOVE_CREATURE_TYPE, getValue(data), data.getTargets());
	}

	@Override
	public Object getData() {
		return creatureTypes.get(null, null, null);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + creatureTypes.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		final AddCreatureTypeEffect other = (AddCreatureTypeEffect) obj;
		return creatureTypes.equals(other.creatureTypes);
	}

}
