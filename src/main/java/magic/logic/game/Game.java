package magic.logic.game;

import java.util.ArrayList;
import java.util.List;

import magic.graphics.GameView;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.EnchantT;
import magic.logic.card.State;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.Cant;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.SpellAbilities;
import magic.logic.card.abilities.TriggeredAbilities;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.TapEffect;
import magic.logic.card.abilities.effect.linker.E;
import magic.logic.card.abilities.statics.AlternativeCostAbilities;
import magic.logic.card.abilities.statics.AsLongAsAbilities;
import magic.logic.card.abilities.statics.StaticAbilities;
import magic.logic.card.abilities.statics.WouldAbilities;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.place.AbstractPlace;
import magic.logic.place.Battlefield;
import magic.logic.place.Deck;
import magic.logic.place.Exile;
import magic.logic.place.Graveyard;
import magic.logic.place.Hand;
import magic.logic.place.OptiPlace;
import magic.logic.place.Place;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.Counterable;
import magic.logic.utils.GameEvent;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;
import magic.reseau.AbstractGameMaster;
import magic.reseau.server.ServerSide;

public class Game {

	/**
	 * JOUEUR 1
	 */
	private Player player1;

	private Battlefield battlefield1;

	private Graveyard graveyard1;

	private Exile exile1;

	/**
	 * JOUEUR 2
	 */
	private Player player2;

	private Battlefield battlefield2;

	private Graveyard graveyard2;

	private Exile exile2;

	private GameView gameView;

	/**
	 * Save the last action
	 */
	private Pile pile;

	private Step step;

	private Owner playingPlayer;

	private Owner beginnerPlayer;

	private AbstractGameMaster agm;

	private TargetableBDD bdd;

	private final ArrayList<GlobalEffectData> globalEffects;

	private final ArrayList<Cant> interdiction;

	private final ArrayList<GamePileData> gamePile;

	private int turn;

	private final OptiPlace place;

	public Game(final int life, final AbstractGameMaster agm, final Owner player) {
		place = new OptiPlace();

		player1 = new Player(life, Owner.PLAYER1, place);
		player2 = new Player(life, Owner.PLAYER2, place);

		battlefield1 = new Battlefield(place);
		battlefield2 = new Battlefield(place);

		graveyard1 = new Graveyard(place);
		graveyard2 = new Graveyard(place);

		exile1 = new Exile(place);
		exile2 = new Exile(place);

		pile = new Pile();

		bdd = new TargetableBDD();
		bdd.add(player1);
		bdd.add(player2);

		globalEffects = new ArrayList<>();
		gamePile = new ArrayList<>();

		this.agm = agm;

		interdiction = new ArrayList<>();
		place.setPlayer(player1, player2);

		gameView = new GameView(this, player);
	}

	public void start() {
		agm.setGame(this);

		new Thread(agm).start();
	}

	public void close() {
		if (agm instanceof ServerSide server)
			server.close();
	}

	public enum Flags {
		ONLY_TRIGGER, WOULD, NONE;
	}

	public void pushEvent(final GameEvent event, final Card thrower, final Object obj, final Targetable... targs) {
		pushEvent(event, thrower, obj, Flags.NONE, targs);
	}

	private boolean jump;

	public void pushEvent(final GameEvent event, final Card thrower, final Object obj, final Flags flag,
			final Targetable... target) {
		final Targetable[] targs = new Targetable[target.length];
		for (int i = 0; i < target.length; i++)
			targs[i] = getBDD().get(target[i].getGameID());

		final PilePart part = new PilePart(thrower, event, (Object) obj, targs);

		List<Card> cards = getAllCardsInGame();

		// On parcout une première fois à la recherche des capacité qui modifie.
		// C'est à dire les capacités qui vont annuler l'execution de l'event
		jump = false;
		cards.forEach(card -> {
			final ArrayList<StaticAbilities> statics = card.getCardAbilities().getStatics();
			if (statics != null)
				for (final StaticAbilities s : statics)
					// Si la capacité est du type would, on regarde si elle peut s'executer. Si
					// c'est le cas, on la met sur la pile et arrête l'execution de l'event actuel
					if (s instanceof WouldAbilities would && would.canBeExecuted(this, card, part)) {
						putOnStack(would, card, card.getController());
						jump = true;
						return;
					}
		});
		if (jump)
			return;

		// On active l'effet
		agm.push(part);

		// On recharge car il a pu y avoir des changement entre temps
		cards = getAllCardsInGame();

		// On parcourt une autre fois les cartes à la recherche des trigger et des
		// autres capacités statiques
		cards.forEach(card -> {

			final ArrayList<TriggeredAbilities> triggers = card.getCardAbilities().getTriggered();

			// On test toute les cartes pour savoir si l'event peut être executé
			if (triggers != null) {
				for (final TriggeredAbilities trigger : triggers) {

					// Si c'est le cas, on l'envoi sur le stack
					if (trigger.canEventExecuted(this, card, part))
						putOnStack(trigger, card, card.getController());
				}
			}

			if (flag != Flags.ONLY_TRIGGER) {
				final ArrayList<StaticAbilities> statics = card.getCardAbilities().getStatics();
				if (statics != null) {
					for (final StaticAbilities s : statics)
						if (s instanceof AsLongAsAbilities asLong) {
							// Si capacité du type asLong :
							// On check si elle peut être execute, si c'est le cas -> pile
							// Sinon on réinitialise
							if (asLong.canBeExecuted(this, card, obj)) {
								if (!asLong.isEffectApplyed())
									putOnStack(asLong, card, card.getController());
							} else if (asLong.isEffectApplyed())
								asLong.invert(this, card);
						} else if (s instanceof WouldAbilities would)
							would.reset();
				}
			}
		});
	}

	/**
	 * Met sur la pile un sort ou une capacité
	 * 
	 * @param object   Le sort ou la capacité a placer sur la pile
	 * @param thisCard
	 * 
	 * @return Si a prob
	 */
	public boolean putOnStack(final Counterable object, final Card thisCard, final Owner owner) {
		thisCard.setController(owner);

		gamePile.add(new GamePileData(object, thisCard));

		if (object instanceof Abilities a) {
			// On initialise (choix des cibles, choix modales...)
			a.setup(this, thisCard, a);

			if (a.canBeExecuted(this, thisCard, null)) {
				// Si la capacité est une capacité à activer on paie les couts de la carte.
				if (a instanceof ActivatedAbilities activated)
					activated.paidCost(this, thisCard);

				gameView.getStack().addOnStack(object);

				// On indique qu'une capacité se déclanche mais on ne veut déclancher que les
				// capacités déclanchées (pour eviter boucle infinie)
				pushEvent(Event.ABILITIES, thisCard, object, Flags.ONLY_TRIGGER);

				gameView.getStack().pauseStack();

				// On attend les reponses de la part du joueur
				// On execute toute les réponses du joueur

				// Puis on execute la capacité
				a.applyAbiities(this, thisCard);
			} else {
				System.out.println("Peut pas lancer");
				thisCard.setController(null);
				return true;
			}

			a.cleanup(this, thisCard);
		}

		else if (object instanceof Effect e) {
			// On initialise (choix des cibles, choix modales...)
			e.setup(this, thisCard, null);

			if (e.canBeExecuted(this, thisCard, null)) {
				// On attend les reponses de la part du joueur
				// On execute toute les réponses du joueur

				// Puis on execute la capacité
				e.applyEffect(this, thisCard, new EffectData());

			} else
				thisCard.setController(null);

			e.cleanup(this, thisCard);
		}

		else if (object instanceof Card c) {
			// On extrait tout les divers couts aternatif
			final ArrayList<Abilities> alternativeCost = new ArrayList<>();
			for (final StaticAbilities sa : c.getCardAbilities().getStatics())
				if (sa instanceof AlternativeCostAbilities aca) {
					aca.setup(this, thisCard, aca);

					if (aca.canBeExecuted(this, thisCard, "cost"))
						alternativeCost.add(aca);
					else
						aca.cleanup(this, thisCard);
				}

			if (alternativeCost.size() > 0) {
				// On vérifie que le sort peut bien être joué et on fait les setup (cible,
				// modal...)
				final ArrayList<SpellAbilities> spells = new ArrayList<>();
				if (c.getCardAbilities().getSpells() != null)
					for (final SpellAbilities spell : c.getCardAbilities().getSpells()) {
						spell.setup(this, thisCard, spell);

						if (spell.canBeExecuted(this, thisCard, object))
							spells.add(spell);
						else {
							spell.cleanup(this, thisCard);
							return true;
						}
					}

				final AlternativeCostAbilities cost = (AlternativeCostAbilities) chooseFrom(alternativeCost);

				cost.applyAbilities(this, thisCard, "card");
				// On applique toute les différentes réduction ou augemntation du cout
				// On paie le mana de la carte
				// On paie tout les couts supplémentaire

				gameView.getStack().addOnStack(thisCard);

				// On lance un event comme quoi on lance une carte
				pushEvent(Event.CAST, thisCard, object);

				gameView.getStack().pauseStack();

				for (final SpellAbilities spell : spells) {
					spell.applyAbiities(this, thisCard);
					spell.cleanup(this, thisCard);
				}

				// Si c'est un permanent, autrement dit, si ce n'est ni une ephemere et ni un
				// rituel. On indique que le permanent arrive sur le champ de bataille
				if (!c.hasType(CardT.SORCERY) && !c.hasType(CardT.INSTANT))
					pushEvent(Event.ENTER_BATTLEFIELD, thisCard, null, thisCard);
			} else {
				System.err.println("On ne peut pas lancer la carte, les couts ne sont pas bon");
				return true;
			}
		}

		// On supprime de la pile
		gamePile.remove(gamePile.size() - 1);

		return false;
	}

	/**
	 * Test si le proprietaire de la carte peut lancé un rituel
	 * 
	 * @param card La card avec le proprietaire à extraire
	 * 
	 * @return Si l'on peut jouer un rituel
	 */
	public boolean canPlaySorcery(final Card card) {
		// On ne peut lancer une carte comme un rituel que si c'est le bon tour, si la
		// pile est vide et si c'est durant la premier phase principale ou durant la
		// phase secondaire
		return card.getOwner() == playingPlayer && (step == Step.MAIN_PHASE || step == Step.MAIN_PHASE_2)
				&& gamePile.size() == 0;
	}

	public void turn(final Owner owner) {
		Step step;
		final Player p = getPlayer(owner, null)[0];

		step = Step.UNTAP;
		Effect untap = new TapEffect(new Selectors().owner(owner).state(State.TAPPED).place(Place.BATTLEFIELD),
				TargetType.ALL_CARD, false);

		// On detap mais aucune capacité ne doit trigger
		untap.applyEffect(this, null, new EffectData());

		// Le stack doit être traité après, pendant l'upkeep

		/**********/
		step = Step.UPKEEP;

		// owner recoit la priorité
		// Les effets possiblement mis dans la pile lors de l'untap reste dans la pile
		// et les effets "at the beginning of your upkeep" sont ajoutés à la pile
		// L'ordre dans lequel les effets sont executés n'as pas d'importance.
		/**********/

		step = Step.DRAW;

		// On pioche une carte
		Effect draw = E.draw(1);
		draw.applyEffect(this, null, new EffectData());

		// owner recoit la priorité

		/**********/
		step = Step.MAIN_PHASE;

		// On ajoute un lore counter sur toute les SAGA
		Effect lore = E.addCounter(new Selectors(CardT.ENCHANTMENTS).owner(owner).enchantType(EnchantT.SAGA),
				TargetType.ALL_CARD, CounterType.LORE, 1);
		lore.applyEffect(this, null, new EffectData());

		// owner recoit la priorité
		// On joue un terrain (pas contrecarrable et d'action)
		// Le joueur peut joueur ce qu'il veut

		/**********/
		step = Step.COMBAT_PHASE;

		System.out.println(step + "" + p);
	}

	/**
	 * 
	 * @return All the cards in Graveyard, Exile and Battlefield of all players
	 */
	public List<Card> getAllCardsInGame() {
		final ArrayList<Card> cards = new ArrayList<>();

		// Champs de bataille
		cards.addAll(place.getAllBattlefield());

		// Cimetierre
		cards.addAll(place.getAllGraveyard());

		// Exile
		cards.addAll(place.getAllExile());

		return cards;
	}

	public void putCardInBDD(final Card... cards) {
		for (final Card card : cards)
			pushEvent(AGMEvent.SEND_CARD, null, card);
	}

	public Card getThrower(final int id) {
		return (Card) getBDD().get(id);
	}

	public void pushPile(final int gameIDThrower, final GameEvent event, final Object obj, final int... gameIDs) {
		Card thrower = null;
		if (gameIDThrower != -1)
			thrower = getThrower(gameIDThrower);

		final Targetable[] targets = new Targetable[gameIDs.length];
		for (int i = 0; i < gameIDs.length; i++)
			targets[i] = getBDD().get(gameIDs[i]);

		pushPile(thrower, event, obj, targets);
	}

	public void pushPile(Card thrower, final GameEvent gameEvent, final Object obj, final Targetable... target) {
		if (gameEvent != null)
			return;

		System.err.println("Faut penser a mettre a jour !!! Event : " + gameEvent + ", object:" + obj);

	}

	public GamePileData[] playerResponse() {
		return null;
	}

	public boolean can(final Interdiction inter, final Card thisCard, final Targetable... targets) {
		for (final Cant c : interdiction)
			if (c.getInterdiction() == inter) {
				if (c.getSelect() != null && c.getSelect().match(this, thisCard, targets).size() > 0)
					return false;
				if (c.getCondition() != null && c.getCondition().test(this, thisCard, targets))
					return false;
				if (c.getSelect() == null && c.getCondition() == null)
					return false;
			}

		return thisCard.getCardAbilities().can(this, thisCard, inter, targets);
	}

	/**
	 * 
	 * @param owner The target
	 * @param card  The card with this target
	 * @return the player targeted by {@code owner}
	 */
	public Player[] getPlayer(final Owner owner, final Card card) {
		return switch (owner) {
		case EACH -> new Player[] { player1, player2 };
		case YOU -> card.getOwner() == Owner.PLAYER1 ? new Player[] { player1 } : new Player[] { player2 };
		case OPPONENT -> card.getOwner() == Owner.PLAYER2 ? new Player[] { player1 } : new Player[] { player2 };
		case PLAYER1 -> new Player[] { player1 };
		default -> new Player[] { player2 };
		};
	}

	public Battlefield[] getBattlefield(final Owner owner, final Card card) {
		return getGenericPlace(Battlefield.class, owner, card, battlefield1, battlefield2);
	}

	public Graveyard[] getGraveyard(final Owner owner, final Card card) {
		return getGenericPlace(Graveyard.class, owner, card, graveyard1, graveyard2);
	}

	public Exile[] getExile(final Owner owner, final Card card) {
		return getGenericPlace(Exile.class, owner, card, exile1, exile2);
	}

	public Hand[] getHand(final Owner owner, final Card card) {
		return getGenericPlace(Hand.class, owner, card, player1.getHand(), player2.getHand());
	}

	public Deck[] getDeck(final Owner owner, final Card card) {
		return getGenericPlace(Deck.class, owner, card, player1.getDeck(), player2.getDeck());
	}

	public AbstractPlace[] getPlace(final Owner owner, final Card card, final AbstractPlace place1,
			final AbstractPlace place2) {
		if (owner == null)
			return new AbstractPlace[] { place1, place2 };
		return switch (owner) {
		case EACH -> new AbstractPlace[] { place1, place2 };
		case YOU -> card.getOwner() == Owner.PLAYER1 ? new AbstractPlace[] { place1 } : new AbstractPlace[] { place2 };
		case OPPONENT -> card.getOwner() == Owner.PLAYER2 ? new AbstractPlace[] { place1 }
				: new AbstractPlace[] { place2 };
		case PLAYER1 -> new AbstractPlace[] { place1 };
		default -> new AbstractPlace[] { place2 };
		};
	}

	public <A extends AbstractPlace> A[] getGenericPlace(final Class<A> classes, final Owner owner, final Card card,
			final AbstractPlace place1, final AbstractPlace place2) {
		final AbstractPlace[] places = getPlace(owner, card, place1, place2);
		final A[] meshes = (A[]) java.lang.reflect.Array.newInstance(classes, places.length);

		for (int i = 0; i < places.length; i++)
			meshes[i] = classes.cast(places[i]);

		return meshes;
	}

	public void reveal(final Owner owner, final Targetable... targetables) {

	}

	public boolean playerWantToDo(final Effect effect, final Abilities a) {
		return gameView.getStack().may(effect, a);
	}

	public Effect[] chooseModal(final int min, final int max, final Abilities ab, final Effect... effects) {
		return gameView.getChoosePane().chooseModal(min, max, ab, effects);
	}

	public <A extends Targetable> List<Targetable> chooseTarg(final int min, final int max, final List<A> a) {
		return gameView.getChoosePane().chooseTargets(min, max, a);
	}

	public <A extends Targetable> List<Targetable> chooseTarg(final int min, final int max, final A[] a) {
		return chooseTarg(min, max, List.of(a));
	}

	// Return null si canceled
	public Abilities chooseFrom(final ArrayList<? extends Abilities> abilities) {
		if (abilities.size() == 1)
			return abilities.get(0);

		return gameView.getChoosePane().chooseAb(abilities.toArray(Abilities[]::new));
	}

	public int chooseX(final Counterable target) {
		return gameView.getStack().chooseX(target);
	}

	public Targetable[] chooseOrder(final Owner who, final String gauche, final String droit,
			final Targetable... cards) {
		return cards;
	}

	/**
	 * 
	 * @param firstOption
	 * @param secondOp
	 * @param target
	 * @return True si premiere option choisie
	 */
	public boolean chooseOpt(final String firstOption, final String secondOp, final Counterable target) {
		return gameView.getStack().chooseOpt(firstOption, secondOp, target);
	}

	public Owner choosePlayer(final Card card) {
		return gameView.getChoosePane().choosePlayer(this, card);
	}

	/**
	 * Permet d'afficher une info à l'autre joueur et à sois même. Ex : Résultat
	 * d'un dé, d'un lancer pile ou face, d'un choix quelconque (type de créature,
	 * pair ou impair...). Ex2 : Résultat d'un effet, regard...
	 * 
	 * @param info L'info à afficher
	 */
	public void throwInfo(final String info) {

	}

	public <T extends Enum<T>> T chooseEnumFrom(final Owner who, final Enum<T>... enums) {
		return gameView.getChoosePane().chooseEnumFrom(enums);
	}

	/**
	 * 
	 * @param owner    one of Owner.YOU, Owner.OPPONENT
	 * @param thisCard the card who has the effect
	 * @return Owner.PLAYER1 or Owner.PLAYER2
	 */
	public Owner ownerToPlayer(final Owner owner, final Card thisCard) {
		Owner card = thisCard.getController();
		if (card == null)
			card = thisCard.getOwner();

		if (owner == Owner.YOU)
			return card;
		else if (owner == Owner.OPPONENT)
			return card == Owner.PLAYER1 ? Owner.PLAYER2 : Owner.PLAYER1;

		return owner;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Graveyard getGraveyard1() {
		return graveyard1;
	}

	public Graveyard getGraveyard2() {
		return graveyard2;
	}

	public Exile getExile1() {
		return exile1;
	}

	public Exile getExile2() {
		return exile2;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(final Step step) {
		this.step = step;
	}

	public Owner getPlayingPlayer() {
		return playingPlayer;
	}

	public void setPlayingPlayer(final Owner player) {
		playingPlayer = player;
	}

	public Pile getPile() {
		return pile;
	}

	public TargetableBDD getBDD() {
		return bdd;
	}

	public ArrayList<GlobalEffectData> getGlobalEffects() {
		return globalEffects;
	}

	public ArrayList<GamePileData> getGamePile() {
		return gamePile;
	}

	public ArrayList<Cant> getInterdiction() {
		return interdiction;
	}

	public OptiPlace getOptiPlace() {
		return place;
	}

	public GameView getGameView() {
		return gameView;
	}

	public int getTurn() {
		return turn;
	}

	public Turn getTurn(final int nmb) {
		Owner player;
		if (nmb % 2 == 0)
			player = beginnerPlayer;
		else
			player = beginnerPlayer == Owner.PLAYER1 ? Owner.PLAYER2 : Owner.PLAYER1;

		if (turn % 2 == 0) {
			// On est au tour 4 et on test le tour 3, c'etait le tour d'avant
			if (turn - nmb > 2)
				return Turn.ALL;
			else if (turn - nmb > 0)
				return player == Owner.PLAYER1 ? Turn.LAST_TURN_J1 : Turn.LAST_TURN_J2;

			return player == Owner.PLAYER1 ? Turn.THIS_TURN_J1 : Turn.THIS_TURN_J2;
		}

		else {
			// On est au tour 3 et on test le tour 2, c'etait le meme tour
			if (nmb == turn - 1)
				return player == Owner.PLAYER1 ? Turn.THIS_TURN_J1 : Turn.THIS_TURN_J2;
			if (nmb - turn > 3)
				return Turn.ALL;

			return player == Owner.PLAYER1 ? Turn.LAST_TURN_J1 : Turn.LAST_TURN_J2;
		}
	}
}
