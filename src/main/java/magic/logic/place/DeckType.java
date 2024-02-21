package magic.logic.place;

public enum DeckType {
	NORMAL(60), COMMANDER(100);

	private int cardLimit;

	DeckType(final int limit) {
		cardLimit = limit;
	}

	public int getCardLimit() {
		return cardLimit;
	}
}
