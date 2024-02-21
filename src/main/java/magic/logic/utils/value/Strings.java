package magic.logic.utils.value;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.extractor.StringExtractor;
import magic.logic.utils.property.StringProperty;

public class Strings extends Value<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 614912555312248413L;

	public Strings(final String string) {
		super(string);
	}

	public Strings(final StringExtractor extractor) {
		super(extractor);
	}

	public Strings(final StringProperty property) {
		super(property);
	}

	@Override
	public String get(final Game game, final Card thisCard, final EffectData data) {
		if (object instanceof String str)
			return str;

		if (object instanceof StringExtractor extractor)
			return extractor.get(game, thisCard, data);

		return ((StringProperty) object).get();
	}

}
