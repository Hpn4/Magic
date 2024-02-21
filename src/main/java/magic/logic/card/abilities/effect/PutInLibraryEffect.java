package magic.logic.card.abilities.effect;

import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.TargetType;
import magic.logic.utils.Utils;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class PutInLibraryEffect extends AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5033711289229928576L;

	private final Owners owner;

	private final boolean onTop;

	private final boolean random;

	private final Nmbs index;

	public PutInLibraryEffect(final Selectors select, final TargetType target, final Owners owner, final boolean onTop,
			final boolean random, final Nmbs index) {
		super(select, target);
		this.owner = owner;
		this.onTop = onTop;
		this.index = index;
		this.random = random;
	}

	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData data) {
		final Owner o = owner.get(game, thisCard, data);
		final int size = game.getDeck(o, thisCard)[0].size();

		int i = size;
		if (index != null)
			i = index.get(game, thisCard, data);

		// On button
		if (!onTop)
			i = size - i;

		// On recup les cartes
		Targetable[] cards = getTargets(game, data, thisCard);

		if (random)
			Utils.shuffle(cards);
		else
			cards = game.chooseOrder(o, "bottom", "top", cards);

		return new EffectData(i, cards);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(index, onTop, owner, random);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof PutInLibraryEffect))
			return false;
		PutInLibraryEffect other = (PutInLibraryEffect) obj;
		return Objects.equals(index, other.index) && onTop == other.onTop && Objects.equals(owner, other.owner)
				&& random == other.random;
	}

	@Override
	public String toString() {
		return (onTop ? "Au dessus" : "Au dessous") + " de la bibliothèque dans "
				+ (random ? " un ordre aléatoire" : " l'ordre de votre choix");
	}

}
