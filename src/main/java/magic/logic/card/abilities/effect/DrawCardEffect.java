package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class DrawCardEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2212981016865655001L;

	private final Nmbs cardCount;

	private final Owners target;

	public DrawCardEffect() {
		this(Owner.YOU, 1);
	}

	public DrawCardEffect(final Owner owner, final int count) {
		this(new Owners(owner), new Nmbs(count));
	}

	public DrawCardEffect(final Owners target, final Nmbs cardCount) {
		this.target = target;
		this.cardCount = cardCount;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Player[] players = game.getPlayer(target.get(game, thisCard, effectData), thisCard);
		final int card = cardCount.get(game, thisCard, effectData);

		// On vérifie pour chaque joueur que le joueur à bien le droit de piocher
		for (final Player player : players)
			if (game.can(Interdiction.DRAW_CARDS, thisCard, players))
				game.pushPile(thisCard, Event.DRAW_CARD, card, player);

		return new EffectData(card, players);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cardCount, target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof DrawCardEffect other)
			return Objects.equals(cardCount, other.cardCount) && Objects.equals(target, other.target);

		return false;
	}

	@Override
	public String toString() {
		return "Draw cards";
	}

}
