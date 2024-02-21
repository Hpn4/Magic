package magic.logic.card.abilities.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.game.PilePart;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

/**
 * This condition class is used for condition of this kind of type :
 *
 *  If [someone] [event] [this turn/last turn]
 */
public class TurnCondition extends Condition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5684216450898880148L;

	private final Event event;

	private final Owner player;

	private final Cumul cumulMode;

	private final boolean thisTurn;

	private final Condition condition;

	public TurnCondition(final Event event, final Owner player, final boolean thisTurn, final Condition condition,
			final Cumul cumulMode) {
		this.event = event;
		this.player = player;
		this.thisTurn = thisTurn;
		this.condition = condition;
		this.cumulMode = cumulMode;
	}

	public TurnCondition(final Event event, final Selectors selector) {
		this.event = event;
		player = Owner.EACH;
		thisTurn = true;
		condition = new SelectorCondition(selector, TargetType.ALL_CARD);
		cumulMode = Cumul.NONE;
	}

	public boolean test(final Game game, final Card thisCard, final EffectData dataEffect) {
		// On recupere le joueur
		final Owner who = game.ownerToPlayer(player, thisCard);

		// On recupere tous les events lancé par le joueur
		final ArrayList<PilePart> turn = thisTurn ? game.getPile().getThisTurn(who) : game.getPile().getLastTurn(who);

		// On les parcours et on test si l'event lance correspond a celui desiré
		Object memory = null;
		final ArrayList<Targetable> targets = new ArrayList<>();
		for (final PilePart part : turn)
			if (part.getEvent() == event) {

				// on recupere la donne de la pile
				Object obj = part.getData();

				// Si la condition necessite de cumuler les donnés, on les cumul entre elles.
				// Addition de nombre, addition de mana...
				if (cumulMode == Cumul.BOTH || cumulMode == Cumul.DATA) {
					if (obj instanceof Integer i) {
						if (memory == null)
							memory = i;
						else
							memory = (int) memory + i;
						obj = memory;
					} else if (obj instanceof ManaCost mc) {
						if (memory == null)
							memory = new ManaCost(mc);
						else
							((ManaCost) memory).addCosts(mc);
						obj = memory;
					}
				} else if (cumulMode == Cumul.COUNT) // On ajoute un à chaque occurence de l'evenement
					if (memory == null)
						memory = 1;
					else
						memory = (int) memory + 1;

				// On recupere toute les cibles de la pile touché par l'evenement
				Targetable[] sendTargets = part.getTargets();

				// Si la condition necessite de cumuler les cibles, on les ajoute dans une liste
				if (cumulMode == Cumul.BOTH || cumulMode == Cumul.TARGET) {
					targets.addAll(List.of(sendTargets));
					sendTargets = targets.toArray(Targetable[]::new);
				}

				// On construit notre EffectData avec les donnés extraite et on trasnfert a la
				// condition
				final EffectData data = new EffectData(obj, sendTargets);
				if (condition.test(game, thisCard, data)) {
					this.targets = condition.getResultedTargets();
					return true;
				}
			}

		return false;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(condition, cumulMode, event, player, thisTurn);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TurnCondition))
			return false;

		final TurnCondition other = (TurnCondition) obj;
		return Objects.equals(condition, other.condition) && cumulMode == other.cumulMode && event == other.event
				&& player == other.player && thisTurn == other.thisTurn;
	}

}
