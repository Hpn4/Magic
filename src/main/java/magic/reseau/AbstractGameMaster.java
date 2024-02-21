package magic.reseau;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javafx.application.Platform;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Counter;
import magic.logic.card.CreatureT;
import magic.logic.card.EnchantT;
import magic.logic.card.Stat;
import magic.logic.card.State;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.CardAbilities;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.AbstractMana;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.game.GamePileData;
import magic.logic.game.GlobalEffectData;
import magic.logic.game.PilePart;
import magic.logic.game.Player;
import magic.logic.place.Place;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.Counterable;
import magic.logic.utils.GameEvent;
import magic.logic.utils.Paint;
import magic.reseau.server.ServerSide;

public abstract class AbstractGameMaster implements Runnable {

	protected ObjectOutputStream oos;

	/**
	 * 0 : Connexion 1 : Fin de connexion
	 */
	protected IntConsumer consumer;

	protected Game game;

	private Card vide;

	public void setGame(final Game game) {
		this.game = game;

		vide = new Card("vide");
		vide.setOwner(Owner.EACH);
	}

	public void doPart(final Packet packet, final boolean updateCardEffect, final Targetable[] allTargets) {
		Targetable[] targets;
		if (allTargets != null)
			targets = allTargets;
		else
			targets = getTargets(packet.getGameIDs());

		final GameEvent gameEvent = packet.getEvent();
		final int idThrower = packet.getThrower();
		Card thrower = vide;
		if (idThrower != -1)
			thrower = getThrower(packet.getThrower());

		final Owner owner = thrower.getOwner();

		if (gameEvent instanceof Event event) {
			switch (event) {
			case ADD_MANA -> {
				final Player player = (Player) targets[0];
				player.getReserve().addCosts(packet.getManaCost());

				// On met a jour la partie graphique
				Platform.runLater(() -> game.getGameView().updateManePool(owner));
			}
			case SPEND_MANA -> {
				final Player player = (Player) targets[0];
				final ManaCost cost = packet.getManaCost();
				final ManaCost payed = player.getReserve().payBasic(cost, thrower, targets[1], game);

				if (targets[1] instanceof Card c) {
					// On extrait le cout de X si il y en a un et on le stock dans la carte
					for (final AbstractMana am : cost.getListOfCosts())
						if (am instanceof Mana m && m.getMana() == MCType.X)
							payed.addCosts(new Mana(m));

					c.set(Card.PAYED_MANA_COST, payed);
				}

				// On met a jour la partie graphique
				Platform.runLater(() -> game.getGameView().updateManePool(owner));
			}

			case ATTACK -> {
			}
			case BECOME_BLOCKED -> {
			}
			case BLOCKS -> {
			}

			case CAST, TARGET -> {
			}

			case DEALS_COMBAT_DAMAGE -> {
			}
			case IS_DEALT_DAMAGE -> {
			}
			case DEALT_DAMAGE -> {
				// Rajouter le choix pour planeswalker
				final int damage = packet.getInt();
				for (final Targetable target : targets) {
					if (target instanceof Card card) {
						card.getCurrentStat().addToughness(-damage);
						// On ne lance d'event dans la pile que lorsqu'on peut modifier. Sinon les
						// events sont mancé en double
						if (card.getCurrentStat().getToughness() <= 0 && updateCardEffect)
							game.pushPile(card, Event.DIES, null, card);
					} else if (target instanceof Player player && updateCardEffect)
						game.pushPile(thrower, Event.LOSE_LIFE, damage, target);
				}
			}

			case DESTROY, SACRIFICE -> {
				if (updateCardEffect)
					game.pushEvent(Event.DIES, thrower, Place.BATTLEFIELD, targets);
			}
			case DIES -> {
				if (updateCardEffect)
					game.pushEvent(Event.PUT_INTO_GRAVEYARD, thrower, packet.getPlace(), targets);
			}

			case DISCARD_CARD -> {
				if (updateCardEffect)
					game.pushEvent(Event.PUT_INTO_GRAVEYARD, thrower, Place.HAND, targets);
			}
			case DRAW_CARD -> {
				final Owner player = ((Player) targets[0]).getPlayer();
				final Card[] drawedCards = game.getDeck(player, null)[0].draw(packet.getInt());
				if (drawedCards == null)
					System.err.println(player + " a perdu, pas assez de carte");
				else
					game.getHand(player, null)[0].addCards(drawedCards);

				// La partie graphique
				for (final Card draw : drawedCards)
					Platform.runLater(() -> game.getGameView().putInHand(player, draw, Place.DECK));
			}
			case SCRY -> {

			}

			case GAIN_LIFE -> {
				for (final Targetable target : targets) {
					final Player p = (Player) target;
					p.addLife(packet.getInt());

					// Met à jour le graphique
					Platform.runLater(() -> game.getGameView().setLife(p.getPlayer(), p.getLife()));
				}

			}
			case LOSE_LIFE -> {
				for (final Targetable target : targets) {
					final Player player = (Player) target;
					player.addLife(-packet.getInt());
					if (player.isDead())
						System.err.println(
								"Le joueur : " + player.getPlayer() + " est mort, sa vie : " + player.getLife());

					// Met à jour le graphique
					Platform.runLater(() -> game.getGameView().setLife(player.getPlayer(), player.getLife()));
				}
			}

			case ENTER_BATTLEFIELD -> {
				forEach((card) -> {
					removeCardFrom(owner, packet.getPlace(), card);
					final Place prev = card.getPlace();
					card.setPlace(Place.BATTLEFIELD);
					game.getBattlefield(owner, null)[0].addCard(card);

					if (updateCardEffect)
						for (final GlobalEffectData ged : game.getGlobalEffects())
							ged.enterBattlefield(game, targets);

					Platform.runLater(() -> game.getGameView().putInBattlefield(card, prev));
				}, targets);
			}
			case PUT_INTO_EXILE -> {
				forEach((card) -> {
					removeCardFrom(owner, packet.getData(), card);
					card.setPlace(Place.EXILE);
					game.getExile(owner, null)[0].addCard(card);

					unattach(card, updateCardEffect);
				}, targets);
			}
			case PUT_INTO_GRAVEYARD -> {
				forEach((card) -> {
					final Place prev = card.getPlace();
					removeCardFrom(owner, packet.getData(), card);
					card.setPlace(Place.GRAVEYARD);
					game.getGraveyard(owner, null)[0].addCard(card);

					unattach(card, updateCardEffect);
					
					Platform.runLater(() -> game.getGameView().putInGraveyard(card.getOwner(), card, prev));
				}, targets);
			}
			case PUT_INTO_LIBRARY -> {
				forEach((card) -> {
					removeCardFrom(owner, packet.getData(), card);
					card.setPlace(Place.DECK);
					game.getDeck(owner, null)[0].addCard(card);

					unattach(card, updateCardEffect);
				}, targets);
			}
			case PUT_INTO_HAND, RETURNED_INTO_HAND -> {
				forEach((card) -> {
					removeCardFrom(owner, packet.getData(), card);
					card.setPlace(Place.HAND);
					game.getHand(owner, null)[0].addCard(card);

					unattach(card, updateCardEffect);

					// Parti graphique
					Platform.runLater(() -> game.getGameView().putInHand(owner, card, Place.DECK));
				}, targets);
			}

			case ADDED_COUNTER -> forEach((card) -> card.addCounter(packet.getCounter()), targets);
			case REMOVED_COUNTER -> {
				final Counter count = packet.getCounter();
				final CounterType type = count.getType();
				forEach((card) -> {
					if (type == CounterType.ALL_COUNTER)
						card.removeAllCounters();
					else if (count.getCount() == -1)
						card.removeCounter(type);
					else
						card.removeCounter(type, count.getCount());
				}, targets);
			}

			case TAPPED -> forEach((card) -> {
				final State[] state = card.getState();
				card.set("previousState", state);
				card.changeState(State.TAPPED);
			}, targets);
			case UNTAPPED -> forEach((card) -> {
				final State[] state = card.getState();
				card.set("previousState", state);
				card.changeState(State.UNTAPPED);
			}, targets);

			case CREATE_TOKEN -> {

			}

			// Aussi bien lorsque l'on enchante que l'on equipe
			// thrower est la carte a attache (enchantement, artefact equipement)
			// targets[0] est la cible de la carte. Au deux on attache l'autre
			case ENCHANT, EQUIP -> {
				targets[0].attachCard(thrower);
				thrower.attachCard((Card) targets[0]);
			}
			case UNATTACHED -> {
				// Les cibles ne sont forcement que des enchatement aura ou des artefact
				// equipement
				forEach((card) -> {
					final ArrayList<Card> attachedCards = card.getAttachedCards();
					if (attachedCards != null && attachedCards.size() > 0) {
						// On recuperer la carte attaché et on la supprime
						final Card toRemove = attachedCards.get(0);
						attachedCards.clear();

						// On supprime l'equipement du coté de la carte equipé
						final ArrayList<Card> attachedCards1 = toRemove.getAttachedCards();
						attachedCards1.remove(card);

						// On met a jour les cartes
						card.set(Card.ATTACHED_CARDS, attachedCards);
						toRemove.set(Card.ATTACHED_CARDS, attachedCards1);
					}
				}, targets);
			}
			default -> {
			}
			}
		} else if (gameEvent instanceof AGMEvent event) {
			switch (event) {
			case SEND_CARD -> {
				final Card card = (Card) packet.getData();
				card.getCardAbilities().setup(card);
				game.getBDD().add(card);
			}
			case SEND_DECK -> {
			}

			case ADD_STAT -> {
				final Stat stat = packet.getStat();
				forEach((card) -> {
					final Stat newStat = new Stat(card.getCurrentStat());
					newStat.add(stat);
					card.set(Card.CURRENT_STAT, newStat);
				}, targets);
			}
			case REMOVE_STAT -> {
				final Stat stat = packet.getStat();
				forEach((card) -> {
					final Stat newStat = new Stat(card.getCurrentStat());
					newStat.remove(stat);
					card.set(Card.CURRENT_STAT, newStat);

					if (card.getToughness() <= 0 && updateCardEffect)
						game.pushPile(card, Event.DIES, null, card);
				}, targets);
			}

			case ADD_CAPACITIES -> {
				final Capacities[] capacities = (Capacities[]) packet.getData();

				forEach((card) -> card.addCapacities(capacities), targets);
			}
			case REMOVE_CAPACITIES -> {
				final List<Capacities> capacities = List.of((Capacities[]) packet.getData());
				forEach((card) -> {
					final CardAbilities ab = card.getCardAbilities();
					ab.getCapacities().removeAll(capacities);
					card.set(Card.ABILITIES, ab);
				}, targets);
			}

			case ADD_ABILITIES -> {
				final Abilities ability = (Abilities) packet.getData();
				forEach((card) -> card.addAbility(ability), targets);
			}
			case REMOVE_ABILITIES -> {
				final Abilities ability = (Abilities) packet.getData();
				forEach((card) -> {
					final CardAbilities ab = card.getCardAbilities();
					ab.removeAbility(ability);
					card.set(Card.ABILITIES, ab);
				}, targets);
			}

			case ADD_CARD_TYPE -> {
				// Les types à ajouter
				final CardT[] types = (CardT[]) packet.getData();
				forEach((card) -> card.getType().addAll(List.of(types)), targets);
			}
			case REMOVE_CARD_TYPE -> {

			}

			case ADD_CREATURE_TYPE -> {
				// Les types à ajouter
				final CreatureT[] types = (CreatureT[]) packet.getData();
				forEach((card) -> card.getCreatureType().addAll(List.of(types)), targets);
			}
			case REMOVE_CREATURE_TYPE -> {

			}

			case REMOVE_FROM_PILE -> game.getGamePile()
					.remove(new GamePileData((Counterable) packet.getData(), thrower));
			case UNTIL_CARD_LEAVE_BATTLEFIELD, GLOBAL_UNTIL_CARD_LEAVE_BATTLEFIELD -> {
				if (updateCardEffect) {
					final GlobalEffectData ged = new GlobalEffectData(packet.getInvEffect(), thrower, true, targets,
							System.currentTimeMillis());
					game.pushPile(null, AGMEvent.ADD_GLOBAL_EFFECT, ged);
				}
			}
			case UNTIL_END_OF_TURN -> {
				final InversableEffect effect = packet.getInvEffect();
				forEach((card) -> {
					final CardAbilities ab = card.getCardAbilities();
					ab.addEndTurn(effect);
					card.set(Card.ABILITIES, ab);
				}, targets);
			}

			case ADD_GLOBAL_EFFECT -> {
				game.getGlobalEffects().add((GlobalEffectData) packet.getData());
			}
			case ADD_TO_GLOBAL -> {
				final long id = (long) packet.getData();
				for (final GlobalEffectData ged : game.getGlobalEffects()) {
					if (ged.getId() == id)
						ged.getTargets().addAll(List.of(targets));
				}
			}
			case REMOVE_GLOBAL_EFFECT -> {
				if (updateCardEffect) {
					final int i = packet.getInt();
					game.getGlobalEffects().remove(i).invert(game);
					System.out.println("[" + getName() + "] DEL global effect : " + i);
				}
			}

			case SET_LIFE -> {
				for (final Targetable target : targets) {
					final Player p = (Player) target;
					p.setLife(packet.getInt());
					Platform.runLater(() -> game.getGameView().setLife(p.getPlayer(), p.getLife()));
				}
			}
			case SHUFFLE_LIBRARY -> {
				if (targets != null)
					game.getDeck(owner, thrower)[0].addCard((Card) targets[0]);
				game.getDeck(owner, thrower)[0].shuffle();
			}
			case TRANSFORM -> {

			}
			}
		}

		if (getClass() == ServerSide.class)
			System.out.println(Paint.B_GREEN + "[SERVER] executé : " + packet + " " + updateCardEffect + Paint.RESET);
		else
			System.out.println(Paint.B_MAGENTA + "[CLIENT] executé : " + packet + " " + updateCardEffect + Paint.RESET);
		if (!updateCardEffect)
			System.out.println("\n");
	}

	public String getName() {
		return this.getClass().getSimpleName().toUpperCase().substring(0, 6);
	}

	public Card getThrower(final int id) {
		return (Card) game.getBDD().get(id);
	}

	public void setEvent(final IntConsumer consumer) {
		this.consumer = consumer;
	}

	public void doEvent(final int step) {
		if (consumer != null)
			consumer.accept(step);
	}

	public void push(final PilePart part) {
		// On envoie les donnés que si le transfert n'a pas ete annulé
		final Targetable[] targets = part.getTargets();
		final int size = targets.length;
		final int[] ids = new int[size];

		for (int i = 0; i < size; i++)
			ids[i] = targets[i].getGameID();

		final int idThrower = part.getThrower() == null ? -1 : part.getThrower().getGameID();
		final Packet packet = new Packet(idThrower, part.getEvent(), part.getData(), ids);

		doPart(packet, true, targets);

		try {
			oos.writeObject(packet);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			oos.write("exit".getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void unattach(final Card card, final boolean updateCardEffect) {
		if (updateCardEffect) {
			if (card.getEnchantmentType() == EnchantT.AURA || card.hasType(CardT.ARTIFACT))
				game.pushPile(card, Event.UNATTACHED, null, card);
			else
				game.pushPile(card, Event.UNATTACHED, null, card.getAttachedCards().toArray(Targetable[]::new));

		}
	}

	protected void forEach(Consumer<? super Card> action, final Targetable[] targets) {
		for (final Targetable target : targets)
			if (target instanceof Card card)
				action.accept(card);
	}

	protected boolean removeCardFrom(final Owner owner, final Object data, final Card card) {
		if (data != null && data instanceof Place place) {
			card.set("previousPlace", card.getPlace());
			return switch (place) {
			case BATTLEFIELD -> game.getBattlefield(owner, null)[0].remove(card);
			case DECK -> game.getDeck(owner, null)[0].remove(card);
			case EXILE -> game.getExile(owner, null)[0].remove(card);
			case GRAVEYARD -> game.getGraveyard(owner, null)[0].remove(card);
			case HAND -> game.getHand(owner, null)[0].remove(card);
			case STACK -> false;
			};
		}

		return false;
	}

	public Targetable[] getTargets(final int[] ids) {
		final int size = ids.length;
		final Targetable[] targets = new Targetable[size];

		for (int i = 0; i < size; i++)
			targets[i] = game.getBDD().get(ids[i]);

		return targets;
	}
}
