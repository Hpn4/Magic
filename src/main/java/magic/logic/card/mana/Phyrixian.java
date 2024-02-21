package magic.logic.card.mana;

import java.util.Comparator;

public class Phyrixian extends AbstractMana {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1223193295365416071L;

	public Phyrixian(final MCType type) {
		super(type);
	}

	public Phyrixian(final Phyrixian phy) {
		super(phy.getMana());
		setData(phy.getData());
	}

	@Override
	public int compareTo(final Object o) {
		return Comparator.comparing(Phyrixian::getMana).compare(this, (Phyrixian) o);
	}

}
