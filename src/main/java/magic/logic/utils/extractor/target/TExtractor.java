package magic.logic.utils.extractor.target;

import java.io.Serializable;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public interface TExtractor extends Serializable {

	/**
	 * 
	 * @param game     This game
	 * @param thisCard Is the card who have this effect
	 * @param targets  All the targetable returns by the trigger, the effects or any
	 * @return
	 */
	Targetable[] get(final Game game, final Card thisCard, final EffectData data);

}
