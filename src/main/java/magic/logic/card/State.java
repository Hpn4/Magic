package magic.logic.card;

/**
 * This enum stores all the different possible states a card can have.
 * Each state have is opposite state (tapped != untapped)
 */
public enum State {
	TAPPED(0), UNTAPPED(0), FLIPPED(1), UNFLIPPED(1), FACE_UP(2), FACE_DOWN(2), PHASED_IN(3), PHASED_OUT(3);
	
	private final byte index;
	
	State(final int index) {
		this.index = (byte) index;
	}
	
	public int getIndex() {
		return index;
	}
}
