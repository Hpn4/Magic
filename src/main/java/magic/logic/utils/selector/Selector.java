package magic.logic.utils.selector;

import java.io.Serializable;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.game.Game;

@FunctionalInterface
public interface Selector extends Serializable {

	boolean match(final Game game, final Card thisCard, final Targetable target, final SelectData data);
}
