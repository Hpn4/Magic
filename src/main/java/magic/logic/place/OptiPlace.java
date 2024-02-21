package magic.logic.place;

import java.util.ArrayList;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.game.Player;

public class OptiPlace {

	private ArrayList<Card> battlefield;

	private ArrayList<Card> graveyard;

	private ArrayList<Card> library;

	private ArrayList<Card> exile;

	private ArrayList<Card> hand;

	private ArrayList<Targetable> stack;

	private ArrayList<Player> players;

	public OptiPlace() {
		battlefield = new ArrayList<>();
		graveyard = new ArrayList<>();
		library = new ArrayList<>();
		exile = new ArrayList<>();
		hand = new ArrayList<>();
		stack = new ArrayList<>();
		players = new ArrayList<>();
	}

	public void setPlayer(final Player player1, final Player player2) {
		players.add(player1);
		players.add(player2);
	}

	public ArrayList<Card> getAllBattlefield() {
		return battlefield;
	}

	public ArrayList<Card> getAllGraveyard() {
		return graveyard;
	}

	public ArrayList<Card> getAllLibrary() {
		return library;
	}

	public ArrayList<Card> getAllExile() {
		return exile;
	}

	public ArrayList<Card> getAllHand() {
		return hand;
	}

	public ArrayList<Targetable> getStack() {
		return stack;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}
}
