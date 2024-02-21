package magic.logic.utils.selector;

import java.util.Arrays;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.TriggeredAbilities;
import magic.logic.card.abilities.statics.StaticAbilities;
import magic.logic.game.Game;

public class AbilitiesSelector implements Selector {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8880395814266863541L;

	private final AbilitiesT[] types;

	public AbilitiesSelector(final AbilitiesT... types) {
		this.types = types;
	}

	@Override
	public boolean match(final Game game, final Card thisCard, final Targetable target, final SelectData data) {
		if (target instanceof Abilities ab) {
			for (final AbilitiesT type : types) {
				final boolean match = switch (type) {
				case STATIC -> ab instanceof StaticAbilities;
				case MANA -> ab.manaAbilities();
				case ACTIVATED -> ab instanceof ActivatedAbilities;
				case TRIGGERED -> ab instanceof TriggeredAbilities;
				};

				if (match)
					return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return 31 + Arrays.hashCode(types);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		return obj instanceof AbilitiesSelector other && Arrays.equals(types, other.types);
	}

	public enum AbilitiesT {
		STATIC, MANA, ACTIVATED, TRIGGERED;
	}

}
