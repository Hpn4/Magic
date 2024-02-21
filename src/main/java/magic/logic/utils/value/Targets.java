package magic.logic.utils.value;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.extractor.target.TExtractor;
import magic.logic.utils.value.Value;

public class Targets extends Value<Targetable[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6913854630368346932L;

	public Targets(final Targetable... targetables) {
		super(targetables);
	}

	public Targets(final TExtractor extractor) {
		super(extractor);
	}

	/**
	 * 
	 * @param game     This game
	 * @param thisCard Is the card who have this effect
	 * @param targets  All the targetable returns by the trigger, the effects or any
	 * @return
	 */
	public Targetable[] get(final Game game, final Card card, final EffectData data) {
		if (object instanceof TExtractor extractor)
			return extractor.get(game, card, data);

		if (object instanceof Targetable[] targets)
			return targets;

		return null;
	}
}
