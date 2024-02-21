package magic.logic.card.abilities.utils;

import java.io.Serializable;

import magic.logic.card.abilities.effect.inversable.InversableEffect;

public record InversableEffectData(InversableEffect effect, Object data) implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7234105481019648283L;
}
