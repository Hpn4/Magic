package magic.graphics.utils;

import java.io.Serializable;
import java.util.ArrayList;

import magic.logic.card.CardT;
import magic.logic.card.mana.ManaCost;

public record SetData(String cardName, ManaCost cost, String path, ArrayList<CardT> types) implements Serializable {

}
