package magic.logic.card.abilities.effect.linker;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.TargetType;

public class UntilEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4844246967958232747L;

	private final InversableEffect effect;

	private final AGMEvent event;

	public UntilEffect(final InversableEffect effect, final UntilMode mode) {
		this.effect = effect;

		event = switch (mode) {
		case END_OF_TURN -> AGMEvent.UNTIL_END_OF_TURN;
		case LEAVE_BATTLEFIELD -> AGMEvent.UNTIL_CARD_LEAVE_BATTLEFIELD;
		case LEAVE_BATTLEFIELD_GLOBAL -> AGMEvent.GLOBAL_UNTIL_CARD_LEAVE_BATTLEFIELD;
		};
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		effect.setup(game, thisCard, a);
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		return effect.canBeExecuted(game, thisCard, object);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final EffectData data = effect.applyEffect(game, thisCard, effectData);

		if (effect instanceof AbstractTargetEffect ate && ate.getTargetType() == TargetType.ALL_CARD)
			ate.setTargetType(TargetType.RETURNED_TARGET);

		game.pushPile(thisCard, event, effect, data.getTargets());

		return data;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
	}

	@Override
	public int hashCode() {
		return Objects.hash(effect, event);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof UntilEffect other)
			return Objects.equals(effect, other.effect) && event == other.event;

		return false;
	}

	public enum UntilMode {
		END_OF_TURN, LEAVE_BATTLEFIELD, LEAVE_BATTLEFIELD_GLOBAL;
	}

}
