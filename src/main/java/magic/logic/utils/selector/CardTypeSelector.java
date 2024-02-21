package magic.logic.utils.selector;

import java.util.Arrays;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.EnchantT;
import magic.logic.game.Game;

public class CardTypeSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7157319785140230103L;

	private final CardT[] types;

	private final LogicalOp op;

	protected CardTypeSelector(final LogicalOp op, final CardT... cardTypes) {
		types = cardTypes;
		this.op = op;
	}

	protected CardTypeSelector(final CardT... cardTypes) {
		types = cardTypes;
		op = LogicalOp.OR;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		for (final CardT type : card.getType()) {
			for (int i = 0; i < types.length; i++) {
				final boolean test = switch (types[i]) {
				// Pre built test
				case NON_CREATURE -> type != CardT.CREATURE;
				case SORCERY_INSTANT -> type == CardT.SORCERY || type == CardT.INSTANT;
				case PERMANENT -> !(type == CardT.SORCERY || type == CardT.INSTANT);
				case HISTORIC -> card.getEnchantmentType() == EnchantT.SAGA || type == CardT.LEGENDARY
						|| type == CardT.ARTIFACT;
				default -> types[i] == type;
				};

				// Si un des types ne correspond pas, return false
				if (op == LogicalOp.AND) {
					if (!test)
						return false;
				} else if (test)
					return true;
			}
		}

		return op == LogicalOp.AND;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		final int result = prime + Arrays.hashCode(types);
		return prime * result + Objects.hash(op);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CardTypeSelector other)
			return op == other.op && Arrays.equals(types, other.types);

		return false;
	}
}
