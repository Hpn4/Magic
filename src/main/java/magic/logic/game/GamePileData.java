package magic.logic.game;

import magic.logic.card.Card;
import magic.logic.utils.Counterable;

public record GamePileData(Counterable counterable, Card thisCard) {

}
