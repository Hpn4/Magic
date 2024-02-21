package magic.logic.card.mana;

import java.util.Comparator;

public class Mana extends AbstractMana {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2271830643485337764L;

	private int number;

	public Mana(final MCType mana) {
		super(mana);
		number = 1;
	}

	public Mana(final MCType mana, final int number) {
		super(mana);
		this.number = number;
	}

	public Mana(final MCType mana, final int number, final Object data) {
		super(mana);
		setData(data);
		this.number = number;
	}

	public Mana(final Mana mana) {
		this(mana.getMana(), mana.getNumber(), mana.getData());
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Mana:[" + getMana() + "*" + getNumber() + "]";
	}

	@Override
	public int compareTo(final Object o) {
		return Comparator.comparing(Mana::getMana).thenComparingInt(Mana::getNumber).compare(this, (Mana) o);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + number;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof Mana other)
			return number == other.number;

		return false;
	}

}
