package magic.logic.utils.value;

import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.property.ColorProperty;

public class Colors extends Value<CColor[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7294849578956793020L;

	public Colors(final CColor... colors) {
		super(colors);
	}

	public Colors(final ColorProperty property) {
		super(property);
	}

	@Override
	public CColor[] get(final Game game, final Card thisCard, final EffectData data) {
		if (object instanceof CColor[] col)
			return col;

		return new CColor[] { ((ColorProperty) object).get() };
	}

}
