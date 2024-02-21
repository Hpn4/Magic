package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.value.ManaCosts;
import magic.logic.utils.value.Owners;

public class AddManaEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3019753290453335962L;

	private final ManaCosts mana;

	private final Owners player;

	public AddManaEffect(final ManaCost mana) {
		this(mana, Owner.YOU);
	}

	public AddManaEffect(final ManaCost mana, final Owner player) {
		this(new ManaCosts(mana), new Owners(player));
	}

	public AddManaEffect(final ManaCosts cost, final Owners targets) {
		mana = cost;
		player = targets;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Player[] players = game.getPlayer(player.get(game, thisCard, effectData), thisCard);
		final ManaCost mc = mana.get(game, thisCard, effectData);

		for (final Player p : players)
			game.pushEvent(Event.ADD_MANA, thisCard, mc, p);

		return new EffectData(mc, players);
	}

	public boolean isManaEffect() {
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mana, player);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AddManaEffect other)
			return Objects.equals(mana, other.mana) && Objects.equals(player, other.player);

		return false;
	}

	@Override
	public String toString() {
		return "Add mana";
	}
}
