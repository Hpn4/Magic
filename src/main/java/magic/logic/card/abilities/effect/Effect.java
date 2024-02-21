package magic.logic.card.abilities.effect;

import java.io.Serializable;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.Counterable;

public interface Effect extends Serializable, Counterable {

	/**
	 * Cette méthode est appellé pour vérifier que l'effet peut-être exécuté. (Cible
	 * valide, condition valide)
	 * 
	 * @param game   Le jeu actuel
	 * @param card   La carte possedant cet effet
	 * @param object Une valeur utilitaire passé par la méthode mere
	 * 
	 * @return Si l'effet peut être execute ou non
	 */
	public default boolean canBeExecuted(final Game game, final Card card, final Object object) {
		return true;
	}
	
	/**
	 * 
	 * @param game       The game, who contains all the information of this game
	 * @param thisCard   The card who have this effect
	 * @param effectData The possible data send by a precedent effect or by a
	 *                   possible trigger
	 * @return
	 */
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData);

	/**
	 * Cette méthode est appelée au moment ou la capacité est mise sur la pile. Elle
	 * sert à charger toutes les actions avant que l'adversaire aie une réponse.
	 * </br>
	 * Par exemple pour le choix des cibles, le choix des effets (pour une carte à
	 * effet modale), ...
	 * 
	 * @param game     Le jeu actuel
	 * @param thisCard La carte qui possède cet effet
	 */
	public default void setup(final Game game, final Card thisCard, final Abilities thisAbilities) {
	}

	/**
	 * Cette méthode est appellé une fois que le sort à été lancé. Elle s'éxecute
	 * même si le sort à été contrecarre ou retirer de la pile par un quelconque
	 * effet. Cette méthode permet de liberer ou de restorer toute les données
	 * utilisé par l'effet. </br>
	 * Par exemple les différents tag placés sur une carte, les cibles retenues en
	 * mémoire, ...
	 * 
	 * @param game     Le jeu actuel
	 * @param thisCard La carte qui possède cette effeyt
	 */
	public default void cleanup(final Game game, final Card thisCard) {
	}

	public default boolean isManaEffect() {
		return false;
	}

}
