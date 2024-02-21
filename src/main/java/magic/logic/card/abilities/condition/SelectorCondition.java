package magic.logic.card.abilities.condition;

import java.util.ArrayList;
import java.util.List;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public class SelectorCondition extends Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1667025035455349838L;

	private final Selectors select;

	private final TargetType target;

	public SelectorCondition(final Selectors select, final TargetType target) {
		this.select = select;
		this.target = target;
	}

	public boolean test(final Game game, final Card thisCard, final EffectData data) {
		switch (target) {
		case ALL_CARD -> {
			final List<Card> cards = game.getAllCardsInGame();

			final List<Card> matchesTargets = select.matchCards(game, thisCard, cards);
			if (matchesTargets.size() == 0)
				return false;

			final Player[] players = game.getPlayer(Owner.EACH, thisCard);
			final ArrayList<Targetable> matchesPlayer = select.match(game, thisCard, players);
			if (matchesPlayer.size() == 0)
				return false;

			matchesPlayer.addAll(matchesTargets);

			targets = matchesPlayer.toArray(Targetable[]::new);
			return true;
		}
		case RETURNED_TARGET -> {
			final ArrayList<Targetable> matchesTargets = select.match(game, thisCard, data.getTargets());
			if (matchesTargets.size() == 0)
				return false;

			targets = matchesTargets.toArray(Targetable[]::new);
			return true;
		}
		case THIS_CARD -> {
			if (select.match(game, thisCard, thisCard)) {
				targets = new Targetable[] { thisCard };
				return true;
			}

			return false;
		}
		default -> {
			return false;
		}

		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + select.hashCode();
		result = prime * result + target.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;

		final SelectorCondition other = (SelectorCondition) obj;
		return select.equals(other.select) && target == other.target;
	}

}
