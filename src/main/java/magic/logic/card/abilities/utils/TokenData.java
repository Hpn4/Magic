package magic.logic.card.abilities.utils;

import java.io.Serializable;

import magic.logic.card.Stat;

public record TokenData(Stat stat, int count, Owner creator) implements Serializable {

}
