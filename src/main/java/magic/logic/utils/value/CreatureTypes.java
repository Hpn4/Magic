package magic.logic.utils.value;

import java.util.ArrayList;
import java.util.List;

import magic.logic.card.Card;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.extractor.CreatureTypeExtractor;
import magic.logic.utils.property.CreatureTypeProperty;

public class CreatureTypes extends Value<ArrayList<CreatureT>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1279848406440073388L;

	public CreatureTypes(final CreatureT... types) {
		super(List.of(types));
	}

	public CreatureTypes(final CreatureTypeExtractor extractor) {
		super(extractor);
	}

	public CreatureTypes(final CreatureTypeProperty property) {
		super(property);
	}

	@Override
	public ArrayList<CreatureT> get(final Game game, final Card thisCard, final EffectData data) {
		if (object instanceof ArrayList<?>)
			return (ArrayList<CreatureT>) object;

		if (object instanceof CreatureTypeExtractor extractor)
			return extractor.get(game, thisCard, data);
		
		return (ArrayList<CreatureT>) List.of(((CreatureTypeProperty) object).get());
	}

}
