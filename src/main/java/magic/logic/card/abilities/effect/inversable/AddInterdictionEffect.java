package magic.logic.card.abilities.effect.inversable;

import magic.logic.card.Card;
import magic.logic.card.abilities.Cant;
import magic.logic.card.abilities.effect.AbstractTargetEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class AddInterdictionEffect extends AbstractTargetEffect implements InversableEffect {

	private final Cant cant;

	public AddInterdictionEffect(final Selectors select, final TargetType target, final Cant cant) {
		super(select, target);
		this.cant = cant;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invertEffect(final Game game, final Card thisCard, final EffectData data) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
