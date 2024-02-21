package magic.logic.utils.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.LandT;
import magic.logic.game.Game;

public class LandTypeSelector implements CardSelector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6283902199219231632L;

	private final LandT[] types;

	/**
	 * Définie si une ou toute les couleurs doivent correspondre Ex : (lorsque.. si
	 * vous controlé une foret ou une plaine)
	 */
	private final LogicalOp op;

	protected LandTypeSelector(final LogicalOp op, final LandT... types) {
		this.op = op;
		this.types = types;
	}

	public boolean match(final Game game, final Card thisCard, final Card card) {
		final ArrayList<LandT> landTypes = card.getLandTypes();

		// On verifie que la carte possede un type de terrain
		if (landTypes != null) {

			final boolean and = op == LogicalOp.AND;

			// On parcourt parmis tout les types de terrain à tester
			for (final LandT landT : types) {
				final boolean match = landTypes.contains(landT);
				if (and) {
					// En configuration "ET", si l'un des types n'est pas présent on renvoie faux
					if (!match)
						return false;
				} else if (match)
					return true; // En configuration "OU", si un des types est présent on renvoie vrai
			}

			return and;
		}

		return false;
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
		if (obj instanceof LandTypeSelector other)
			return op == other.op && Arrays.equals(types, other.types);

		return false;
	}

}
