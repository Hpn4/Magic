package magic.logic.card.abilities.statics;

import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public class OpeningHand extends StaticAbilities {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7503483657066653615L;

	private final Effect effect;

	public OpeningHand(final Effect effect) {
		this.effect = effect;
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card card, final Object object) {
		return effect.canBeExecuted(game, card, object);
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities ab) {
		effect.setup(game, thisCard, ab);
	}

	@Override
	public EffectData applyAbiities(final Game game, final Card thisCard) {
		return effect.applyEffect(game, thisCard, new EffectData(null, thisCard));
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		effect.cleanup(game, thisCard);
	}

}
