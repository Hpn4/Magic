package magic.logic.place;

import java.util.Collections;
import java.util.List;

import magic.logic.card.Card;

public class Deck extends AbstractPlace {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DeckType type;

	private String name;

	public Deck(final String name, final DeckType type, final OptiPlace place) {
		super(place.getAllLibrary());
		this.name = name;
		this.type = type;
	}

	public Card draw() {
		if (cards.size() > 0)
			return cards.remove(cards.size() - 1);
		return null;
	}

	public Card[] draw(final int count) {
		if (cards.size() >= count) {
			final Card[] drawedCards = new Card[count];
			for (int i = 0; i < count; i++)
				drawedCards[i] = cards.remove(cards.size() - (i + 1));

			return drawedCards;
		} else
			return null;
	}

	public void putUnder(final Card card) {
		cards.add(0, card);
	}

	public void putUnder(final boolean randomOrder, final Card... abstractCards) {
		final List<Card> underCards = List.of(abstractCards);
		if (randomOrder)
			Collections.shuffle(underCards);
		cards.addAll(0, underCards);
	}

	public DeckType getDeckType() {
		return type;
	}

	public void setDeckType(final DeckType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
}
