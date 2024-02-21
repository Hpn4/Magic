package magic.logic.card.mana;

import java.util.Comparator;
import java.util.Objects;

public class DoubleMana extends AbstractMana {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1566658154561834985L;

	private MCType secondMana;

	public DoubleMana(final MCType type, final MCType secondMana) {
		super(type);
		this.secondMana = secondMana;
	}

	public DoubleMana(final MCType type, final MCType secondMana, final Object data) {
		super(type);
		setData(data);
		this.secondMana = secondMana;
	}

	public DoubleMana(final DoubleMana dm) {
		this(dm.getMana(), dm.getSecondMana(), dm.getData());
	}

	public MCType getSecondMana() {
		return secondMana;
	}

	public boolean match(final MCType mana) {
		return mana == getMana() || mana == getSecondMana();
	}

	public boolean match(final DoubleMana doubleMana) {
		return match(doubleMana.getMana()) || match(doubleMana.getSecondMana());
	}

	public String toString() {
		return "DoubleMana:[" + getMana() + "/" + getSecondMana() + "]";
	}

	public int compareTo(final Object o) {
		final DoubleMana mana = (DoubleMana) o;
		return Comparator.comparing(DoubleMana::getMana).thenComparing(DoubleMana::getSecondMana).compare(this, mana);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(secondMana);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof DoubleMana other)
			return secondMana == other.secondMana;

		return false;
	}

}
