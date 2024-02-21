package magic.logic.game;

import java.util.ArrayList;
import java.util.EnumMap;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.place.Deck;
import magic.logic.place.DeckType;
import magic.logic.place.Hand;
import magic.logic.place.OptiPlace;

public class Player extends Targetable {

	private int life;

	private final EnumMap<CounterType, Integer> counters;

	private final ArrayList<Card> attachedCards;

	private final ManaCost reserve;

	private Owner player;

	private Hand hand;

	private Deck deck;

	public Player(final int life, Owner who, final OptiPlace place) {
		this.life = life;
		counters = new EnumMap<>(CounterType.class);
		attachedCards = new ArrayList<>();
		hand = new Hand(place);
		deck = new Deck("Zeub", DeckType.NORMAL, place);
		player = who;
		reserve = new ManaCost();
	}

	/**
	 ******************
	 **** COUNTERS ****
	 ******************
	 */
	public boolean haveCounter(final CounterType counter) {
		return counters.containsKey(counter);
	}

	public Integer getCounter(final CounterType counter) {
		return counters.get(counter);
	}

	public int addCounter(final CounterType counter, final int number) {
		return counters.merge(counter, number, (i, j) -> i + j);
	}

	public int removeCounter(final CounterType counter, final int number) {
		return counters.merge(counter, 0, (i, j) -> i - number);
	}

	public int removeCounter(final CounterType counter) {
		return counters.remove(counter);
	}

	public void removeAllCounters() {
		counters.clear();
	}

	/**
	 ******************
	 ****** LIFE ******
	 ******************
	 */
	public int getLife() {
		return life;
	}

	public void setLife(final int life) {
		this.life = life;
	}

	public void addLife(final int add) {
		life += add;
	}

	public boolean isDead() {
		return life < 0;
	}

	public boolean attachCard(final Card card) {
		return attachedCards.add(card);
	}

	public Owner getPlayer() {
		return player;
	}

	public void setPlayer(final Owner player) {
		this.player = player;
	}

	public ManaCost getReserve() {
		return reserve;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(final Hand hand) {
		this.hand = hand;
	}

	public Deck getDeck() {
		return deck;
	}

	public void setDeck(final Deck deck) {
		this.deck = deck;
	}
}