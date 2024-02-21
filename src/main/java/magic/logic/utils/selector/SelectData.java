package magic.logic.utils.selector;

import java.io.Serializable;
import java.util.Objects;

public class SelectData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8484877726019923444L;

	private boolean target;

	public SelectData(final SelectData other) {
		target = other.target;
	}

	public SelectData(final boolean target) {
		this.target = target;
	}

	public boolean canTarget() {
		return target;
	}

	public void setTarget(final boolean canTarget) {
		target = canTarget;
	}

	@Override
	public int hashCode() {
		return Objects.hash(target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SelectData other)
			return target == other.target;

		return false;
	}

}
