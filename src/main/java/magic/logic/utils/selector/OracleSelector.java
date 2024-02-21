package magic.logic.utils.selector;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.game.Game;

public class OracleSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6626208718707072093L;

	private final String oracle;

	private final boolean contains;

	public OracleSelector(final String oracle, final boolean contains) {
		this.oracle = oracle;
		this.contains = contains;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		return contains ? card.getOracle().contains(oracle) : card.getOracle().equals(oracle);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contains, oracle);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof OracleSelector other)
			return contains == other.contains && Objects.equals(oracle, other.oracle);

		return false;
	}

}
