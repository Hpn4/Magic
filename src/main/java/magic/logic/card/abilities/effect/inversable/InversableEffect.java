package magic.logic.card.abilities.effect.inversable;

import magic.logic.card.Card;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;

public interface InversableEffect extends Effect {

	public abstract void invertEffect(final Game game, final Card thisCard, final EffectData data);

	public abstract Object getData();

	default Object getValue(final EffectData data) {
		Object obj = data.getData();
		// Si les donnés sont un tableau de donné, on recupere la vrai donne donné au
		// rang i. Le rang est precisé au premier index du tableau de donné
		if (obj instanceof Object[] datas)
			obj = datas[(int) datas[0]];

		return obj;
	}

}
