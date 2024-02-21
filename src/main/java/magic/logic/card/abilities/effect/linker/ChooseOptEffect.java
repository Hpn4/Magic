package magic.logic.card.abilities.effect.linker;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class ChooseOptEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6687194211896453553L;

	private final String oracleFirstOption;

	private final Effect firstOption;

	private final String oracleSecondOption;

	private final Effect secondOption;

	private transient boolean isFirstOptChoosed;

	public ChooseOptEffect(final String oracleFirst, final Effect firstOpt, final String oracleSecond,
			final Effect secondOpt) {
		oracleFirstOption = oracleFirst;
		firstOption = firstOpt;
		oracleSecondOption = oracleSecond;
		secondOption = secondOpt;
	}

	public void setup(final Game game, final Card thisCard, final Abilities thisAbilities) {
		isFirstOptChoosed = game.chooseOpt(oracleFirstOption, oracleSecondOption, thisAbilities);

		if (isFirstOptChoosed)
			firstOption.setup(game, thisCard, thisAbilities);
		else
			secondOption.setup(game, thisCard, thisAbilities);
	}

	public boolean canBeExecuted(final Game game, final Card thisCard, final Object data) {
		if (isFirstOptChoosed)
			return firstOption.canBeExecuted(game, thisCard, data);

		return secondOption.canBeExecuted(game, thisCard, data);
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		if (isFirstOptChoosed)
			return firstOption.applyEffect(game, thisCard, effectData);

		return secondOption.applyEffect(game, thisCard, effectData);
	}

	public void cleanup(final Game game, final Card thisCard) {
		if (isFirstOptChoosed)
			firstOption.cleanup(game, thisCard);
		else
			secondOption.cleanup(game, thisCard);

		isFirstOptChoosed = false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstOption, oracleFirstOption, oracleSecondOption, secondOption);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ChooseOptEffect other)
			return Objects.equals(firstOption, other.firstOption)
					&& Objects.equals(oracleFirstOption, other.oracleFirstOption)
					&& Objects.equals(oracleSecondOption, other.oracleSecondOption)
					&& Objects.equals(secondOption, other.secondOption);

		return false;
	}

}
