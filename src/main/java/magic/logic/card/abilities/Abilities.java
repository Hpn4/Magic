package magic.logic.card.abilities;

import java.io.Serializable;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.Counterable;

public abstract class Abilities extends Targetable implements Serializable, Counterable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5023460978792558986L;

	private transient Card parent;

	private String oracle;

	public Card getParent() {
		return parent;
	}

	public void setParent(final Card card) {
		parent = card;
	}

	public String getOracle() {
		return oracle;
	}

	public void setOracle(final String str) {
		oracle = str;
	}

	/**
	 * Cette méthode est appellé pour vérifier que la capacité peut-être exécuté.
	 * (Le bon trigger, la bonne phase...)
	 * 
	 * @param game   Le jeu actuel
	 * @param card   La carte possedant cette capacité
	 * @param object Une valeur utilitaire passé par la méthode mère
	 * 
	 * @return Si la capacité peut être execute ou non
	 */
	public abstract boolean canBeExecuted(final Game game, final Card card, final Object object);

	/**
	 * Cette méthode est appellé au moment ou la capacité est mise sur la pile. Elle
	 * sert a charger toute les actions avant que l'adversaire aie une réponse.
	 * </br>
	 * Par exemple pour le choix des cibles, le choix des effets (pour une carte à
	 * effet modale), ...
	 * 
	 * @param game     Le jeu actuel
	 * @param thisCard La carte qui possède cette capacité
	 */
	public abstract void setup(final Game game, final Card thisCard, final Abilities ab);

	public abstract EffectData applyAbiities(final Game game, final Card thisCard);

	/**
	 * Cette méthode est appellé une fois que le sort à été lancé. Elle s'éxecute
	 * même si le sort à été contrecarre ou retirer de la pile par un quelconque
	 * effet. Cette méthode permet de liberer ou de restorer toute les données
	 * utilisé par l'effet. </br>
	 * Par exemple les différents tag placés sur une carte, les cibles retenues en
	 * mémoire, ...
	 * 
	 * @param game     Le jeu actuel
	 * @param thisCard La carte qui possède cette capacité
	 */
	public abstract void cleanup(final Game game, final Card thisCard);

	public boolean manaAbilities() {
		return false;
	}

	public boolean attachCard(final Card card) {
		return false;
	}
}
