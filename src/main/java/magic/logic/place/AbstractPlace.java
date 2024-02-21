package magic.logic.place;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.game.Game;
import magic.logic.utils.selector.Selectors;

public abstract class AbstractPlace implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -344038557548960949L;

	protected ArrayList<Card> cards;

	private final transient ArrayList<Card> cardsCopy;

	public AbstractPlace(final ArrayList<Card> place) {
		cards = new ArrayList<Card>();
		cardsCopy = place;
	}

	public void addCard(final Card card) {
		cardsCopy.add(card);
		cards.add(card);
	}

	public void addCards(final Card... abstractCards) {
		final List<Card> c = Arrays.asList(abstractCards);
		cardsCopy.addAll(c);
		cards.addAll(c);
	}

	public boolean remove(final Card card) {
		final int id = card.getGameID();
		cardsCopy.remove(card);
		for (int i = 0; i < cards.size(); i++)
			if (cards.get(i).getGameID() == id) {
				cards.remove(i);
				return true;
			}

		return false;
	}

	public Card get(final int index) {
		return cards.get(index);
	}

	public Card remove(final int index) {
		final Card tmp = cards.remove(index);
		cardsCopy.remove(tmp);

		return tmp;
	}

	public Card[] remove(final Game game, final Selectors selector) {
		final ArrayList<? extends Targetable> matchesCard = selector.matchCards(game, null, cards);

		for (int j = 0; j < matchesCard.size(); j++) {
			final int id = matchesCard.get(j).getGameID();
			cardsCopy.remove(matchesCard.get(j));

			for (int i = 0; i < cards.size(); i++) {
				if (id == cards.get(i).getGameID()) {
					matchesCard.remove(j);
					cards.remove(i);
				}
			}
		}

		return matchesCard.toArray(Card[]::new);
	}

	public Card[] getCards(final Game game, final Selectors selector) {
		final ArrayList<? extends Targetable> matchesCard = selector.matchCards(game, null, cards);
		return matchesCard.toArray(Card[]::new);
	}

	public ArrayList<Card> getAllCards() {
		return cards;
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public void clear() {
		cards.clear();
	}

	/**
	 * Réorganise les cartes suivants les methodes redéfinies {@code CompareTo}. Les
	 * cartes seront triées par leurs coût de Mana, suivant l'ordre des couleurs de
	 * l'Enum {@code MCType}, en fonction de leur nom (ordre alphabetique) et de
	 * leur rareté suivant l'ordre de définition de l'Enum {@code Rarity}
	 */
	public void sort() {
		cards.sort(null);
	}

	public int size() {
		return cards.size();
	}
}
