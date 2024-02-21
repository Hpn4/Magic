package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.game.Player;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.GameEvent;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class LifeEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9051597757508658994L;

	private final Owners target;

	private Nmbs life;

	private final GameEvent event;

	public LifeEffect(final Owner owner, final int life, final LifeMode mode) {
		this(new Owners(owner), new Nmbs(life), mode);
	}

	public LifeEffect(final Owner owner, final Nmb nmbExtractor, final LifeMode mode) {
		this(new Owners(owner), new Nmbs(nmbExtractor), mode);
	}

	public LifeEffect(final Owners owners, final Nmbs nmbs, final LifeMode mode) {
		target = owners;
		life = nmbs;
		event = switch (mode) {
		case GAIN -> Event.GAIN_LIFE;
		case LOSE -> Event.LOSE_LIFE;
		case SET -> AGMEvent.SET_LIFE;
		};
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		// Si l'effet est inclus dans un cout additionel
		if (object instanceof String str && str.equals("cost") && event == Event.LOSE_LIFE) {
			// On extrait la vie et on regarde si le joueur a plus de vie
			final int l = life.get(game, thisCard, new EffectData());
			if (game.getPlayer(Owner.YOU, thisCard)[0].getLife() <= l)
				return false;

		}

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final Player[] players = game.getPlayer(target.get(game, thisCard, effectData), thisCard);

		int vie = life.get(game, thisCard, effectData);

		if (event == Event.GAIN_LIFE && !game.can(Interdiction.GAIN_LIFE, thisCard, players))
			vie = 0;

		// game.pushPile(thisCard, event, vie, players);
		game.pushEvent(event, thisCard, vie, players);

		return new EffectData(vie, players);
	}

	@Override
	public int hashCode() {
		return Objects.hash(event, life, target);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof LifeEffect other)
			return Objects.equals(event, other.event) && Objects.equals(life, other.life)
					&& Objects.equals(target, other.target);

		return false;
	}

	@Override
	public String toString() {
		return (event == Event.LOSE_LIFE ? "Lose" : event == Event.GAIN_LIFE ? "Gain" : "Set") + " " + life.toString() + " life";
	}

	public enum LifeMode {
		GAIN, LOSE, SET;
	}
}
