package magic.logic.place;

public class Hand extends AbstractPlace {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5536822473259710703L;

	/**
	 * Defaut : 7 -1 : Infinie
	 */
	private int maxHandSize;

	public Hand(final OptiPlace place) {
		super(place.getAllHand());
		maxHandSize = 7;
	}

	public int getMaximumHandSize() {
		return maxHandSize;
	}

	public void setMaximumHandSize(final int maxHandSize) {
		this.maxHandSize = maxHandSize;
	}

	public boolean handToBig() {
		if (maxHandSize == -1)
			return false;
		return size() > maxHandSize;
	}
}
