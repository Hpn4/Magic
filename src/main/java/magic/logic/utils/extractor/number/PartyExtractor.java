package magic.logic.utils.extractor.number;

import java.util.ArrayList;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.selector.Selectors;

public class PartyExtractor implements Extractor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8958466264355211097L;

	@Override
	public float get(final Game game, final Card card, final EffectData data) {
		final ArrayList<Card> crea = new Selectors(CardT.CREATURE).controller(Owner.YOU).matchCards(game, card,
				game.getAllCardsInGame());

		final ArrayList<Card> sorcier = new Selectors().creatureType(CreatureT.WIZARD).matchCards(game, card, crea),
				clerc = new Selectors().creatureType(CreatureT.CLERIC).matchCards(game, card, crea),
				rogue = new Selectors().creatureType(CreatureT.ROGUE).matchCards(game, card, crea),
				warrior = new Selectors().creatureType(CreatureT.WARRIOR).matchCards(game, card, crea);

		final ArrayList<CreatureT> party = new ArrayList<>();
		ArrayList<Card> min = clerc;
		CreatureT type = CreatureT.CLERIC;

		int changeling = 0;
		for (int i = 0; i < 4; i++) {
			if (sorcier.size() < min.size() && !party.contains(CreatureT.WIZARD)) {
				min = sorcier;
				type = CreatureT.WIZARD;
			}

			if (rogue.size() < min.size() && !party.contains(CreatureT.ROGUE)) {
				min = rogue;
				type = CreatureT.ROGUE;
			}

			if (warrior.size() < min.size() && !party.contains(CreatureT.WARRIOR)) {
				min = warrior;
				type = CreatureT.WARRIOR;
			}

			Card car = null;
			for (final Card c : min) {
				if (c.getCreatureType().contains(CreatureT.CHANGELING))
					changeling++;
				else {
					car = c;
					party.add(type);
				}
			}

			sorcier.remove(car);
			clerc.remove(car);
			rogue.remove(car);
			warrior.remove(car);
		}

		// Vue qu'un est compté 4 fois
		changeling /= 4;

		// On calcule le max
		int max = party.size() > changeling ? party.size() : changeling;

		// On vérifie que ça dépasse pas 4
		return max > 4 ? 4 : max;
	}
}