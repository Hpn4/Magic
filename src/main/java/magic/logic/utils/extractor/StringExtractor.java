package magic.logic.utils.extractor;

import java.io.Serializable;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;

/**
 * Extrait une chaine de caractere en fonction du parametre passé. Si le type de
 * cible est {@code THIS_CARD}, c'est le nom de la carte ayant cette effet qui
 * sera renvoyés. Si le code est {@code RETURNNED_TARGET}, ce sera le nom de la
 * premiere cible contenue par l'effet parent. Sinon ce sera une chaine de
 * caractere extraite depuis la valeur de {@code data} passé par l'objet parent.
 * 
 * @author Hpn4
 *
 */
public class StringExtractor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6268167527974801298L;

	private final TargetType target;

	public StringExtractor(final TargetType target) {
		this.target = target;
	}

	public StringExtractor() {
		target = TargetType.ALL_CARD;
	}

	public String get(final Game game, final Card thisCard, final EffectData data) {
		return switch (target) {
		case THIS_CARD -> thisCard.getName();
		case RETURNED_TARGET -> {
			if (data.getTargets()[0] instanceof Card c)
				yield c.getName();
			yield "";
		}
		default -> data.getString();
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		return obj instanceof StringExtractor other && target == other.target;
	}

}
