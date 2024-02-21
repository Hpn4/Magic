package magic.logic.card.abilities;

import magic.logic.card.abilities.effect.Effect;

public abstract class A {

	public static SpellAbilities spell(final Effect effect) {
		return new SpellAbilities(effect);
	}
}
