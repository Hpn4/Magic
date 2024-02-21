package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Stat;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.abilities.utils.TokenData;
import magic.logic.game.Game;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class CreateTokenEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6742520322529089657L;

	private final Owners owner;

	private final Nmbs count;

	private final Card token;

	private Stat stat;

	private Nmb statExtractor;

	public CreateTokenEffect(final Card token) {
		this(1, token);
	}

	public CreateTokenEffect(final int count, final Card token) {
		this.count = new Nmbs(count);
		this.token = token;
		this.stat = new Stat(token.getDefaultStat());
		owner = new Owners(Owner.YOU);
	}

	public CreateTokenEffect(final int count, final Card token, final Nmb stat) {
		this.count = new Nmbs(count);
		this.token = token;
		statExtractor = stat;
		owner = new Owners(Owner.YOU);
	}

	public CreateTokenEffect(final Nmbs count, final Card token, final Owners owner, final Stat stat,
			final Nmb statExtractor) {
		this.count = count;
		this.token = token;
		this.owner = owner;
		this.stat = stat;
		this.statExtractor = statExtractor;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		final int nmb = count.get(game, thisCard, effectData);

		Stat tokenStat = stat;
		if (statExtractor != null) {
			final int value = statExtractor.get(game, thisCard, effectData);
			tokenStat.add(value, value);
		}

		final Owner o = owner.get(game, thisCard, effectData);
		final TokenData data = new TokenData(tokenStat, nmb, o);

		// Si il n'y a pas de controlleur ou pas de jeton a créer on stop l'effet
		if (o == null || nmb == 0)
			return null;

		game.pushPile(thisCard, Event.CREATE_TOKEN, data, token);

		return new EffectData(data, token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, owner, stat, statExtractor, token);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CreateTokenEffect other)
			return Objects.equals(count, other.count) && Objects.equals(owner, other.owner)
					&& Objects.equals(stat, other.stat) && Objects.equals(statExtractor, other.statExtractor)
					&& Objects.equals(token, other.token);

		return false;
	}

	@Override
	public String toString() {
		return "Create token";
	}
}
