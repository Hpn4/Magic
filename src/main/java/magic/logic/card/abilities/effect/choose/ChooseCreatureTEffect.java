package magic.logic.card.abilities.effect.choose;

import magic.logic.card.Card;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.property.CreatureTypeProperty;
import magic.logic.utils.value.Owners;

public class ChooseCreatureTEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 737514066476906040L;

	private final CreatureTypeProperty property;

	private final Owners who;

	private final CreatureT[] among;

	public ChooseCreatureTEffect(final CreatureTypeProperty property, final Owners who, final CreatureT... among) {
		this.property = property;
		this.who = who;
		this.among = among;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Owner owner = who.get(game, thisCard, effectData);

		final CreatureT choosenType = game.chooseEnumFrom(owner, among.length == 0 ? CreatureT.values() : among);
		property.set(choosenType); // On enregistre

		return new EffectData(choosenType);
	}

}
