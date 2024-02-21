package magic.logic.card.abilities.effect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.place.Deck;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class LookAtLibraryEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8642320691327117946L;

	private final Owners owner;

	private final Nmbs nmbCard;

	private final Effect first;

	private final Effect restCard; // est appele avec le reste des cartes qui n'ont pas été traité par le premier
									// effet

	private final boolean reveal;

	public LookAtLibraryEffect(final Owners owner, final Nmbs nmbCard, final Effect first, final Effect restCard,
			final boolean reveal) {
		this.owner = owner;
		this.nmbCard = nmbCard;
		this.first = first;
		this.restCard = restCard;
		this.reveal = reveal;
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		first.setup(game, thisCard, a);

		if (restCard != null)
			restCard.setup(game, thisCard, a);
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object obj) {
		boolean test = first.canBeExecuted(game, thisCard, obj);

		if (restCard != null)
			test &= restCard.canBeExecuted(game, thisCard, obj);

		return test;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData data) {
		final int nmb = nmbCard.get(game, thisCard, data);
		final Owner o = owner.get(game, thisCard, data);

		final Targetable[] cards = new Targetable[nmb];
		final Deck deck = game.getDeck(o, thisCard)[0];

		// On récupère les cartes du dessus de la bibliothèque
		for (int i = 0; i < nmb; i++)
			cards[i] = deck.get(deck.size() - i);

		// Si on doit reveler les cartes, on le fait
		if (reveal)
			game.reveal(o, cards);

		// On recup les données du premier effet et donc les cibles traités
		final EffectData result = first.applyEffect(game, thisCard, new EffectData(nmb, cards));

		if (restCard != null) {
			int size = result.getTargets().length;
			final List<Targetable> resultedTarg = Arrays.asList(cards);

			// On supprime les cartes déjà traité par le premier effet
			for (int i = 0; i < size; i++)
				resultedTarg.remove(result.getTargets()[i]);

			// On applique le deuxième effet en lui envoyant les cartes qui n'ont pas encore
			// été traité
			return restCard.applyEffect(game, thisCard, new EffectData(nmb, resultedTarg.toArray(Targetable[]::new)));
		}

		return result;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		first.cleanup(game, thisCard);

		if (restCard != null)
			restCard.cleanup(game, thisCard);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, nmbCard, owner, restCard, reveal);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof LookAtLibraryEffect other)
			return Objects.equals(first, other.first) && Objects.equals(nmbCard, other.nmbCard)
					&& Objects.equals(owner, other.owner) && Objects.equals(restCard, other.restCard)
					&& reveal == other.reveal;

		return false;
	}

}