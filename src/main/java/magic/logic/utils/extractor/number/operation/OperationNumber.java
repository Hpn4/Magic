package magic.logic.utils.extractor.number.operation;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Nmbs;

public class OperationNumber implements Operations {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2137701802663127772L;

	public enum Operation {
		ADD, SUB, MUL, DIV;
	}

	private final Operation op;

	private final Nmbs nmb;

	public OperationNumber(final Operation op, final int nmb) {
		this.op = op;
		this.nmb = new Nmbs(nmb);
	}

	public OperationNumber(final Operation op, final Nmbs nmb) {
		this.op = op;
		this.nmb = nmb;
	}

	@Override
	public float apply(final float value, final Game game, final Card thisCard, final EffectData effectData) {
		final int nmb = this.nmb.get(game, thisCard, effectData);
		return switch (op) {
		case ADD -> value + nmb;
		case DIV -> (float) value / nmb;
		case MUL -> (float) value * nmb;
		case SUB -> value - nmb;
		default -> 0;
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(nmb, op);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof OperationNumber other)
			return Objects.equals(nmb, other.nmb) && op == other.op;

		return false;
	}
}
