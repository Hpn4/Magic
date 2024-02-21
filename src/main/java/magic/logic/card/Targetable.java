package magic.logic.card;

import java.util.ArrayList;

/**
 * Les class implementant cette interface pourront être des cibles des Selecteur
 * 
 * @author Hpn4
 *
 */
public abstract class Targetable {

	private int gameID;

	// Liste contenant tout les ID des cartes dont cet objet est ciblée
	private final ArrayList<Integer> targets;

	public Targetable() {
		targets = new ArrayList<>();
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(final int gameID) {
		this.gameID = gameID;
	}
	
	public ArrayList<Integer> getTargetsID() {
		return targets;
	}

	public abstract boolean attachCard(final Card card);

	@Override
	public int hashCode() {
		return 31 + gameID;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return gameID == ((Targetable) obj).gameID;
	}

}
