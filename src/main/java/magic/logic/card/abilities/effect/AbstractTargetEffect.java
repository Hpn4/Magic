package magic.logic.card.abilities.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public abstract class AbstractTargetEffect implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7620292405790311202L;

	private final Selectors select;

	private TargetType targetType;

	private transient ArrayList<Targetable> savedTargets;

	public AbstractTargetEffect(final Selectors select, final TargetType targetType) {
		this.select = select;
		this.targetType = targetType;
		savedTargets = new ArrayList<>();
	}

	public Selectors getSelector() {
		return select;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(final TargetType targetType) {
		this.targetType = targetType;
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities abilities) {
		// On cible
		if (select != null && select.canTarget()) {
			if (savedTargets == null)
				savedTargets = new ArrayList<>();
			switch (targetType) {
			case THIS_CARD -> {
				final boolean canBeTargeted = game.can(Interdiction.BE_THE_TARGET, thisCard, thisCard, abilities);
				if (select == null || select.match(game, thisCard, thisCard) && canBeTargeted)
					savedTargets.add(thisCard);
			}
			case ALL_CARD -> {
				// On extrait toute les cibles qui sont valides
				final ArrayList<? extends Targetable> validTargs = select.matchAny(game, thisCard, get(game, thisCard));

				// On vérifie que les cibles peuvent être ciblés. Si c'est le cas on les
				// prestock
				for (final Targetable targ : validTargs)
					if (targ instanceof Card c && game.can(Interdiction.BE_THE_TARGET, c, abilities, targ))
						savedTargets.add(targ);

				final int max = select.getMaxTarget().get(game, thisCard, new EffectData(null, thisCard));

				if (max > 0) {
					final List<? extends Targetable> t = game.chooseTarg(select.isUpTo() ? 0 : max, max, savedTargets);
					savedTargets.clear();
					savedTargets.addAll(t);
				}
			}
			default -> {

			}
			}

			// On note les cibles comme étant ciblés
			final int id = thisCard.getGameID();
			for (final Targetable target : savedTargets)
				target.getTargetsID().add(id);

			// On envoie un event comme quoi on cible
			if (savedTargets.size() > 0)
				game.pushEvent(Event.TARGET, thisCard, abilities, savedTargets.toArray(Targetable[]::new));
		}
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object obj) {
		// Si il n'y a pas le bon nombre de cible, pas bon
		if (select != null && !select.isUpTo() && select.canTarget())
			if (savedTargets.size() != select.getMaxTarget().get(game, thisCard, new EffectData(obj, thisCard)))
				return false;

		return true;
	}

	public Targetable[] getTargets(final Game game, final EffectData data, final Card thisCard) {
		// On renvoi les cibles qui ont été ciblés et on vérifie qu'elles sont toujours
		// valide
		if (savedTargets == null)
			savedTargets = new ArrayList<>();
		
		if (select != null && select.canTarget())
			return savedTargets.stream().filter(e -> select.match(game, thisCard, e)).collect(Collectors.toList())
					.toArray(Targetable[]::new);

		switch (targetType) {
		case THIS_CARD -> {
			if (select == null || select.match(game, thisCard, thisCard))
				savedTargets.add(thisCard);
		}
		// On réunie les deux ensembles car c'est les même opérations
		case ALL_CARD, ATTACHED_CARD -> {
			// On recup les cartes
			final ArrayList<? extends Targetable> all = targetType == TargetType.ALL_CARD ? get(game, thisCard)
					: thisCard.getAttachedCards();

			// Si il y a une limite au nombre de cible
			if (select.getMaxTarget() != null) {
				// On extrait la limite
				final Targetable[] alls = all.toArray(Targetable[]::new);
				final int size = select.getMaxTarget().get(game, thisCard, new EffectData(null, alls));

				// Et on ajoute ce que le joueur a choisie
				if (size > 0)
					savedTargets.addAll(game.chooseTarg(select.isUpTo() ? 0 : size, size, all));
				else
					savedTargets.addAll(all);
			} else
				savedTargets.addAll(select.matchAny(game, thisCard, all));
		}
		case RETURNED_TARGET -> {
			// Si il n'y a pas de selecteur, on ajoute tout
			if (select == null)
				savedTargets.addAll(List.of(data.getTargets()));
			else {
				// Si il y a une limite au nombre de cible
				if (select.getMaxTarget() != null) {
					// On extrait la limite
					final int size = select.getMaxTarget().get(game, thisCard, data);

					// Et on ajoute ce que le joueur a choisie
					savedTargets.addAll(game.chooseTarg(select.isUpTo() ? 0 : size, size, data.getTargets()));
				} else
					savedTargets.addAll(select.match(game, thisCard, data.getTargets()));
			}
		}
		}

		// On marque les cibles enregistré comme étant "choisie"
		final int id = thisCard.getGameID();
		for (final Targetable target : savedTargets)
			if (target instanceof Card c)
				c.addSelectedByID(id);

		return savedTargets.toArray(Targetable[]::new);
	}

	private ArrayList<? extends Targetable> get(final Game game, final Card thisCard) {
		final Owner o = select.getOptiOwner();
		final Place[] places = select.getOptiPlace();

		if (places == null)
			return game.getOptiPlace().getPlayers();
		else if (places.length > 0) {
			ArrayList<Targetable> targ = null;
			for (int i = 0; i < places.length; i++) {
				final Place place = places[i];
				if (i == 0)
					targ = new ArrayList<>(get(game, thisCard, o, place));
				else
					targ.addAll(get(game, thisCard, o, place));
			}

			return targ;
		} else
			return get(game, thisCard, o, places[0]);
	}

	private ArrayList<? extends Targetable> get(final Game game, final Card thisCard, final Owner o,
			final Place place) {
		switch (place) {
		case BATTLEFIELD -> {
			if (o == Owner.EACH)
				return game.getOptiPlace().getAllBattlefield();
			else
				return game.getBattlefield(select.getOptiOwner(), thisCard)[0].getAllCards();
		}
		case GRAVEYARD -> {
			if (o == Owner.EACH)
				return game.getOptiPlace().getAllGraveyard();
			else
				return game.getGraveyard(select.getOptiOwner(), thisCard)[0].getAllCards();
		}
		case HAND -> {
			if (o == Owner.EACH)
				return game.getOptiPlace().getAllHand();
			else
				return game.getHand(o, thisCard)[0].getAllCards();
		}
		case STACK -> {
			return game.getOptiPlace().getStack();
		}
		case EXILE -> {
			if (o == Owner.EACH)
				return game.getOptiPlace().getAllExile();
			else
				return game.getExile(o, thisCard)[0].getAllCards();
		}
		case DECK -> {
			if (o == Owner.EACH)
				return game.getOptiPlace().getAllLibrary();
			else
				return game.getDeck(o, thisCard)[0].getAllCards();
		}
		}

		return game.getOptiPlace().getPlayers();
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		for (final Targetable target : savedTargets) {
			if (select != null && select.canTarget())
				target.getTargetsID().remove((Integer) thisCard.getGameID());
			else if (target instanceof Card card)
				card.getSelectedBy().remove((Integer) thisCard.getGameID());
		}

		savedTargets.clear();
	}

	@Override
	public int hashCode() {
		return Objects.hash(select, targetType);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AbstractTargetEffect other)
			return Objects.equals(savedTargets, other.savedTargets) && Objects.equals(select, other.select)
					&& targetType == other.targetType;

		return false;
	}

}
