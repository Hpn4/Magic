package magic.logic.game;

import java.util.HashMap;

import magic.logic.card.Targetable;
import magic.logic.utils.Paint;

public class TargetableBDD {

	private HashMap<Integer, Targetable> bdd;

	private int index;

	public TargetableBDD() {
		bdd = new HashMap<>();
		index = 0;
	}

	public Targetable get(final int gameID) {
		return bdd.get(gameID);
	}

	public Targetable put(final int gameID, final Targetable target) {
		target.setGameID(gameID);
		return bdd.put(gameID, target);
	}

	public void add(final Targetable target) {
		System.out.println(Paint.B_CYAN + "[TARGETABLE BDD] : ajout index : " + index + ", " + target + Paint.RESET);
		target.setGameID(index);
		bdd.put(index, target);

		index++;
	}

	public void addAll(final Targetable... targetables) {
		for (final Targetable target : targetables)
			add(target);
	}

	public void clear() {
		bdd.clear();
	}
}
