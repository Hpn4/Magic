package magic.logic.game;

import java.util.ArrayList;
import java.util.HashMap;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;

public class Pile {

	private final ArrayList<PilePart> pile;

	private final HashMap<Owner, ArrayList<PilePart>> thisTurn;

	private final HashMap<Owner, ArrayList<PilePart>> lastTurn;

	public Pile() {
		pile = new ArrayList<>();

		thisTurn = new HashMap<>();
		thisTurn.put(Owner.PLAYER1, new ArrayList<>());
		thisTurn.put(Owner.PLAYER2, new ArrayList<>());

		lastTurn = new HashMap<>();
		lastTurn.put(Owner.PLAYER1, new ArrayList<>());
		lastTurn.put(Owner.PLAYER2, new ArrayList<>());
	}

	public ArrayList<PilePart> getThisTurn(final Owner owner) {
		if (owner == Owner.EACH) {
			final ArrayList<PilePart> all = new ArrayList<>(thisTurn.get(Owner.PLAYER1));
			all.addAll(thisTurn.get(Owner.PLAYER2));
			return all;
		}
		return thisTurn.get(owner);
	}

	public ArrayList<PilePart> getLastTurn(final Owner owner) {
		if (owner == Owner.EACH) {
			final ArrayList<PilePart> all = new ArrayList<>(lastTurn.get(Owner.PLAYER1));
			all.addAll(lastTurn.get(Owner.PLAYER2));
			return all;
		}

		return lastTurn.get(owner);
	}

	public void push(final PilePart part) {
		thisTurn.get(part.getThrower().getOwner()).add(part);
		pile.add(part);
	}

	public void push(final Card thrower, final Event event, final Object obj, final Targetable... targets) {
		push(new PilePart(thrower, event, obj, targets));
	}

	public PilePart getTop() {
		return pile.get(pile.size() - 1);
	}

	public PilePart pop() {
		return pile.remove(pile.size() - 1);
	}

	public void endTurn(final Owner owner) {
		lastTurn.get(owner).clear();
		lastTurn.get(owner).addAll(thisTurn.get(owner));
		thisTurn.get(owner).clear();
	}

	public String toString() {
		String out = "";
		for (final PilePart part : pile)
			out += part + ", ";

		return out;
	}
}
