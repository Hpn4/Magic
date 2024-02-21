package magic.logic.utils.extractor.number.operation;

import java.io.Serializable;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public interface Operations extends Serializable {

	float apply(final float value, final Game game, final Card thisCard, final EffectData effectData);
}
