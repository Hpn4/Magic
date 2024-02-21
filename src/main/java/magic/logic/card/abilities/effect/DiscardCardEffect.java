package magic.logic.card.abilities.effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Owners;

public class DiscardCardEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9129155975075016443L;

	private final Selectors select;

	private final boolean random;

	private final Owners target;

	public DiscardCardEffect(final Owners target, final Selectors select, final boolean random) {
		this.target = target;
		this.select = select;
		this.random = random;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		// On recup les joueurs affectés
		final Player[] players = game.getPlayer(target.get(game, thisCard, effectData), thisCard);
		
		// Le nombre max de carte à défausser
		final int target = select.getMaxTarget().get(game, thisCard, effectData);
		Card[] cards = null;

		for (final Player player : players) {
			final ArrayList<Card> matchedCards = select.matchCards(game, thisCard, player.getHand().getAllCards());

			// Si on doit se défausser aléatoirement des cartes
			if (random) {
				cards = new Card[target];
				Collections.shuffle(matchedCards);
				for (int i = 0; i < target; i++)
					cards[i] = matchedCards.get(i);
			} else // Sinon le joueur choisit lequel il veut défausser
				cards = game.chooseTarg(target, target, matchedCards).toArray(Card[]::new);

			game.pushEvent(Event.DISCARD_CARD, thisCard, player.getPlayer(), cards);
		}

		return new EffectData(cards, players);
	}

	@Override
	public int hashCode() {
		return Objects.hash(random, select, target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof DiscardCardEffect other)
			return random == other.random && Objects.equals(select, other.select)
					&& Objects.equals(target, other.target);

		return false;
	}

	@Override
	public String toString() {
		return "Discard " + select.getMaxTarget().toString() + " cards";
	}

}
