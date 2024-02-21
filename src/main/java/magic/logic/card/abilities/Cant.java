package magic.logic.card.abilities;

import java.io.Serial;
import java.io.Serializable;

import magic.logic.card.abilities.condition.Condition;
import magic.logic.utils.selector.Selectors;

public class Cant implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 5390341896680394467L;

	private final Interdiction interdiction;

	private final Selectors selector;

	// En testant, not cond.test
	private final Condition condition;

	public Cant(final Interdiction interdiction) {
		this.interdiction = interdiction;
		selector = null;
		condition = null;
	}

	public Cant(final Interdiction interdiction, final Selectors select) {
		this.interdiction = interdiction;
		selector = select;
		condition = null;
	}

	public Cant(final Interdiction interdiction, final Condition condition) {
		this.interdiction = interdiction;
		this.condition = condition;
		selector = null;
	}

	public Interdiction getInterdiction() {
		return interdiction;
	}

	public Selectors getSelect() {
		return selector;
	}

	public Condition getCondition() {
		return condition;
	}
}
