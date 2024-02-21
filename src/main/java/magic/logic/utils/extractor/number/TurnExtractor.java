package magic.logic.utils.extractor.number;

import java.util.ArrayList;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.condition.Cumul;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.game.PilePart;
import magic.logic.utils.Utils;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Owners;

public class TurnExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3242328206293084172L;

	private final Event event;

	private final Selectors select;

	private final Owners who;

	/**
	 * Précise la donnée à recupérer. </br>
	 * Si mode == Cumul.TARGET : Ce sera le nombre de cible trouvé par le selecteur
	 * et ayant effectué l'action qui seront compté. </br>
	 * Par exemple : "Gagner X point de vie, X étant le nombre de créature morte ce
	 * tour-ci".</br>
	 * </br>
	 * Si mode == Cumul.DATA : Ce sera la somme des données de l'event de la pile
	 * qui sera récupéré. </br>
	 * Ex : "piochez X cartes, X étant le nombres de point de vie que vous avez
	 * gagné ce tour-ci".</br>
	 * Si mode == Cumul.COUNT : compte le nombre de fois que l'event s'est éxecuté.
	 * </br>
	 * Ex : "Piochez X cartes, X étant le nombres de fois que vous avez gagné des
	 * point de vie ce tour-ci".
	 */
	private final Cumul mode;

	public TurnExtractor(final Event event, final Selectors select, final Owners owner, final Cumul mode) {
		this.event = event;
		this.select = select;
		who = owner;
		this.mode = mode;
	}

	@Override
	public float get(final Game game, final Card thisCard, final EffectData data) {
		final ArrayList<PilePart> pile = game.getPile().getThisTurn(who.get(game, thisCard, data));

		int nmb = 0;
		for (final PilePart part : pile) {
			if (part.getEvent() == event) {

				switch (mode) {
				case TARGET -> {
					final Targetable[] targets = part.getTargets();
					// On compte le nombre de cible
					if (select != null)
						nmb += select.match(game, thisCard, targets).size();
					else
						nmb += targets.length;
				}
				case DATA -> {
					final Targetable[] targets = part.getTargets();
					boolean bon;
					// C'est bon seulement si le selectuer passe. "Ex; gagner un point de vie égale
					// au nombre de blessure que des créatures vertes que vous controlez ont
					// infligé"
					if (targets != null && select != null)
						bon = select.match(game, thisCard, targets).size() > 0;
					else
						bon = true;

					// On additionne les données de la pile
					if (bon && Utils.getData(part.getData()) instanceof Integer i)
						nmb += i;
				}
				case COUNT -> nmb++;
				default -> {
				}
				}
			}
		}

		return nmb;
	}

	@Override
	public int hashCode() {
		return Objects.hash(event, mode, select, who);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof TurnExtractor other)
			return event == other.event && mode == other.mode && Objects.equals(select, other.select)
					&& Objects.equals(who, other.who);

		return false;
	}

}
