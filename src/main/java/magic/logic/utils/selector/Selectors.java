package magic.logic.utils.selector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.CreatureT;
import magic.logic.card.EnchantT;
import magic.logic.card.LandT;
import magic.logic.card.PT;
import magic.logic.card.Rarity;
import magic.logic.card.State;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.selector.AbilitiesSelector.AbilitiesT;
import magic.logic.utils.value.Colors;
import magic.logic.utils.value.CreatureTypes;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;
import magic.logic.utils.value.Strings;

public class Selectors implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6621919885694385820L;

	private static final byte UPTO = 1;

	private static final byte EXCEPT = 2;

	/** La liste de tout les selecteurs a tester ou des opérateurs */
	private final ArrayList<ArrayList<Object>> selectors;

	private int index;

	private byte flag;

	/**
	 * Si la selection implique un ciblage. (Ex:la creature ciblé gagne +1/+1).
	 * Sinon le choix n'est pas forcement un ciblage (Ex:choissisez une creature
	 * verte).
	 * 
	 * "Choissir" est diffrent de "cibler" puisque "cibler" peut etre un trigger
	 * (Ex:si "name" est la cible d'un sort...)
	 */
	private final SelectData data;

	/** Le nombre maximum de cible a choisir */
	private Nmbs maxTarget;

	/**
	 * Ces deux variables permettent de préciser ou aller chercher les cibles (si on
	 * selectionne les cartes dans un cimetiere, inutile de tester toute les cartes
	 * sur le champs de bataille)
	 */
	private Place[] optiPlace = { Place.BATTLEFIELD };

	private Owner optiOwner = Owner.EACH;

	public Selectors() {
		this(false, 0);
	}

	public Selectors(final boolean target, final int maxTarget) {
		data = new SelectData(target);
		this.maxTarget = new Nmbs(maxTarget);
		selectors = new ArrayList<>();
		selectors.add(new ArrayList<>());
		index = 0;
	}

	public Selectors(final LogicalOp op, final CardT... types) {
		this(false, 0);
		cardType(op, types);
	}

	public Selectors(final CardT... types) {
		this(LogicalOp.OR, types);
	}

	/**
	 * Constructeur de copie
	 * 
	 * @param select Le selecteur à copier
	 */
	public Selectors(final Selectors select) {
		selectors = new ArrayList<>(select.selectors);
		maxTarget = select.maxTarget;
		flag = select.flag;
		index = select.index;
		data = new SelectData(select.data);
		optiPlace = select.optiPlace;
		optiOwner = select.optiOwner;
	}

	/**
	 * *************** *** ABILITIES *** ****************
	 */
	/**
	 * Test le type de la capacité, si c'est une capacité de mana, activé,
	 * déclanche, statique...</br>
	 * Il y a possibilité de fournir une liste de type, le selecteur effectuera un
	 * OU logique entre ces types. Dès que l'un des types correspond il renvoie
	 * vrai. </br>
	 * Ex : "Contrecarrer la capacité activé ou déclanché ciblée"
	 * 
	 * @param abilitiesType Les différents types à tester
	 * 
	 * @return Ce {@code Selectors}
	 */
	public Selectors abilities(final AbilitiesT... abilitiesType) {
		return generic(new AbilitiesSelector(abilitiesType));
	}

	/**
	 * **************** *** PLAYER *** *****************
	 */
	/**
	 * Test si le joueur possède un marqueur de type {@code counter}. Si c'est le
	 * cas, il est possible de tester le nombre de marqueur en fonction d'un
	 * opérateur de comparaison. (Ex:Si un joueur possede moins de 2 marqueurs
	 * "curse"...)
	 * 
	 * @param counter Le type du marqueur
	 * @param number  Le nombre de marqueur
	 * @param op      L'opérateur de comparaison
	 * 
	 * @return {@code this}
	 */
	public Selectors playerCounter(final CounterType counter, final int number, final Operator op) {
		return playerCounter(counter, new Nmbs(number), op);
	}

	public Selectors playerCounter(final CounterType counter, final Nmbs number, final Operator op) {
		return generic(new PlayerCounterSelector(counter, number, op));
	}

	/**
	 * ************* *** USER SELECT *** **************
	 */
	/**
	 * Test si la carte possède la rareté {@code rarity}
	 * 
	 * @param rarity La rarete a tester
	 * 
	 * @return {@code this}
	 */
	public Selectors rarity(final Rarity rarity) {
		return generic(new RaritySelector(rarity));
	}

	/**
	 * Si {@code contains}, test si le texte d'effet de la carte contient
	 * {@code oracle}. Sinon test si il est exactement le même que {@code oracle}
	 * 
	 * @param oracle   Le text à tester
	 * @param contains Si la carte doit contenir le text ou être parfaitement égale
	 * 
	 * @return {@code this}
	 */
	public Selectors oracle(final String oracle, final boolean contains) {
		return generic(new OracleSelector(oracle, contains));
	}

	/**
	 * ************* *** PLANESWLAKER *** **************
	 */
	/**
	 * Test le type de planeswalker. </br>
	 * (Ex:si vous controlez un planeswalker Garruk...)
	 * 
	 * @param type Le type de planeswalker à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors planeswalkerType(final PT type) {
		return generic(new PlaneswalkerTypeSelector(type));
	}

	/**
	 * ************* *** ENCHANTMENT *** **************
	 */
	/**
	 * Test le type de l'enchantement. </br>
	 * (Ex:si vous controlez une saga...)
	 * 
	 * @param type Le type d'enchantement à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors enchantType(final EnchantT type) {
		return generic(new EnchantTypeSelector(type));
	}

	/**
	 * ****************** *** ANY *** ******************
	 */
	/**
	 * Test si la carte est de l'un des types de {@code cardTypes}. </br>
	 * (Ex:Détruissez une creatures ou un artefact ciblée...)
	 * 
	 * @param cardTypes Le(s) type(s) de carte à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors cardType(final CardT... cardTypes) {
		return generic(new CardTypeSelector(cardTypes));
	}

	/**
	 * SI {@code op == LogicalOp.OR}, Test si la carte est de l'un des types de
	 * {@code cardTypes}. </br>
	 * (Ex:Détruissez une creatures ou un artefact ciblée...) </br>
	 * </br>
	 * SI {@code op == Logical.AND}, Test si la carte à tous les types de
	 * {@code cardTypes} </br>
	 * (Ex:Détruissez la créture artefact ciblée)
	 * 
	 * @param op        L'opérateur de comparaison : OR ou AND
	 * @param cardTypes Le(s) type(s) de carte à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors cardType(final LogicalOp op, final CardT... cardTypes) {
		return generic(new CardTypeSelector(op, cardTypes));
	}

	/**
	 * Test le possesseur de la carte. (Ex:les creature que vous possedez).</br>
	 * </br>
	 * Il y a une différence entre posséder et controler. Posséder est l'action
	 * d'appartenance de la carte. Au début de la partie, chaque joueur possède ses
	 * cartes et ce jusqu'a la fin de la partie. On dit que vous êtes le
	 * propriétaire des cartes.</br>
	 * Controler refaire a qui a le controle sur la carte, qui l'a sur son champ de
	 * bataille. Quand vous jouez un sort, vous avez après qu'il soit mis sur la
	 * pile, le controle dessus. Mais il est possible qu'un adversaire prenne le
	 * controle de votre carte. Ainsi votre carte est sur son champ de bataille, il
	 * la controle mais vous en êtes le propriétaire.
	 * 
	 * @param owner Le possesseur à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors owner(final Owner owner) {
		return owner(new Owners(owner));
	}

	public Selectors owner(final Owners owner) {
		return generic(new OwnerSelector(owner));
	}

	/**
	 * Test le controleur de la carte. (Ex:les creature que vous contrôlez).</br>
	 * </br>
	 * Il y a une différence entre posséder et controler. Posséder est l'action
	 * d'appartenance de la carte. Au début de la partie, chaque joueur possède ses
	 * cartes et ce jusqu'a la fin de la partie. On dit que vous êtes le
	 * propriétaire des cartes.</br>
	 * Controler refaire a qui a le controle sur la carte, qui l'a sur son champ de
	 * bataille. Quand vous jouez un sort, vous avez après qu'il soit mis sur la
	 * pile, le controle dessus. Mais il est possible qu'un adversaire prenne le
	 * controle de votre carte. Ainsi votre carte est sur son champ de bataille, il
	 * la controle mais vous en êtes le propriétaire.
	 * 
	 * @param owner Le controlleur à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors controller(final Owner owner) {
		optiOwner = owner;
		return controller(new Owners(owner));
	}

	public Selectors controller() {
		return controller(Owner.YOU);
	}

	public Selectors controller(final Owners owner) {
		return generic(new ControllerSelector(owner));
	}

	/**
	 * Test l'emplacement de la carte. </br>
	 * (Ex:champs de bataille, cimetierre)
	 * 
	 * @param place Le(s) lieu(x) à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors place(final Place... places) {
		optiPlace = places;
		return generic(new PlaceSelector(places));
	}

	/**
	 * Test si la carte possède un marqueur de type {@code counter}. Si c'est le
	 * cas, il est possible de tester le nombre de marqueur en fonction d'un
	 * opérateur de comparaison. (Ex:Si il y a 4 marqueurs "temps" ou plus...)
	 * 
	 * @param counter Le type du marqueur
	 * @param number  Le nombre de marqueur
	 * @param op      L'opérateur de comparaison
	 * 
	 * @return {@code this}
	 */
	public Selectors counter(final CounterType counter, final int number, final Operator op) {
		return counter(counter, new Nmbs(number), op);
	}

	public Selectors counter(final CounterType counter, final Nmbs number, final Operator op) {
		return generic(new CounterSelector(counter, number, op));
	}

	/**
	 * Test si la carte a un des états de {@code state}. </br>
	 * (Ex:Créature engagée et face caché)
	 * 
	 * @param states Les états à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors state(final State... states) {
		return state(LogicalOp.OR, states);
	}

	/**
	 * Test si la carte a un des etats ou tout les etats de {@code states}.</br>
	 * (Ex:creature engagé ou flipped)
	 * 
	 * @param op     Si la carte doit posseder l'un des etats ou tout les états.
	 *               Soit OR ou AND
	 * @param states Les etats à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors state(final LogicalOp op, final State... states) {
		return generic(new CardStateSelector(op, states));
	}

	public Selectors attack() {
		return generic(new TestValueSelector("isAttacking", true));
	}

	public Selectors block() {
		return generic(new TestValueSelector("isBLocking", true));
	}

	/**
	 * Test si le nom de la carte est très exactement {@code name}
	 * 
	 * @param name Le nom à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors name(final String name) {
		return name(name, true);
	}

	/**
	 * Si {@code contains}, test si le nom de la carte contient {@code name}. Sinon
	 * test si le nom de la carte est tres exactement egale a {@code name}.
	 * 
	 * @param name     Le nom à tester
	 * @param contains Si le nom de la carte doit contenir {@code name} ou bien si
	 *                 il est exactement egale
	 * 
	 * @return {@code this}
	 */
	public Selectors name(final String name, final boolean contains) {
		return name(new Strings(name), contains);
	}

	public Selectors name(final Strings name, final boolean contains) {
		return generic(new NameSelector(name, contains));
	}

	/**
	 * Exclut la carte possedant ce selecteur du ciblage
	 * 
	 * @return {@code this}
	 */
	public Selectors excludeThis() {
		return generic(new ExcludeThisCardSelector());
	}

	/**
	 * Test si la carte possede l'une des capacitées {@code capacities}. (Ex:les
	 * creaturees avec le vol...)
	 * 
	 * @param capacities La/les capacitée(s) à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors capacities(final Capacities... capacities) {
		return generic(new CapacitiesSelector(capacities));
	}

	/**
	 * ************* *** CASTABLE SPELL *** *************
	 */
	/**
	 * Test le coût convertie de mana de la carte.
	 * 
	 * @param ccm Le coût convertit de mana de comparaison
	 * @param op  L'opérateur de comparaison
	 * 
	 * @return {@code this}
	 */
	public Selectors CCM(final int ccm, final Operator op) {
		return CCM(new Nmbs(ccm), op);
	}

	public Selectors CCM(final Nmbs ccm, final Operator op) {
		return generic(new CCMSelector(ccm, op));
	}

	/**
	 * Test si la carte a un coût de mana d'exactement {@code mc}
	 * 
	 * @param mc Le coût de mana de comparaison
	 * 
	 * @return {@code this}
	 */
	public Selectors manaCost(final ManaCost mc) {
		return generic(new ManaCostSelector(mc));
	}

	/**
	 * Test si la carte est de la/l'une des couleur(s) {@code colors}. </br>
	 * (Ex:creature verte ou bleue)
	 * 
	 * @param colors La/les couleur(s) à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors color(final CColor... colors) {
		return color(LogicalOp.OR, colors);
	}

	/**
	 * Test si la carte doit être soit de l'une des couleurs {@code colors}. Soit de
	 * toute les couleurs {@code colors}
	 * 
	 * @param op     Si la carte doit être de l'une ou de toute les couleurs
	 * @param colors La/les couleur(s) à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors color(final LogicalOp op, final CColor... colors) {
		return color(op, new Colors(colors));
	}

	public Selectors color(final LogicalOp op, final Colors colors) {
		return generic(new CardColorSelector(op, colors));
	}

	/**
	 * **************** *** CREATURE *** ****************
	 */
	/**
	 * Test si la carte de creature possede l'un des types de creature
	 * {@code creatureTypes}. (Ex:pirate ou humain)
	 * 
	 * @param creatureTypes Le(s) type(s) de creature à tester
	 * 
	 * @return {@code this}
	 */
	public Selectors creatureType(final CreatureT... creatureTypes) {
		return creatureType(new CreatureTypes(creatureTypes));
	}

	public Selectors creatureType(final LogicalOp op, final CreatureT... creatureTypes) {
		return creatureType(op, new CreatureTypes(creatureTypes));
	}

	public Selectors creatureType(final CreatureTypes creatureTypes) {
		return generic(new CreatureTypeSelector(creatureTypes));
	}

	public Selectors creatureType(final LogicalOp op, final CreatureTypes creatureTypes) {
		return generic(new CreatureTypeSelector(op, creatureTypes));
	}

	/**
	 * Test la force de la creature. (Ex:creature avec froce superieur ou egale a 3)
	 * 
	 * @param pwer La valeur de force de comparaison
	 * @param op   L'opérateur de comparaison
	 * 
	 * @return {@code this}
	 */
	public Selectors power(final int power, final Operator op) {
		return power(new Nmbs(power), op);
	}

	public Selectors power(final Nmbs power, final Operator op) {
		return generic(new PowerSelector(power, op));
	}

	/**
	 * Test l'endurance de la creature. (Ex:creature avec une endurance inferieur a
	 * 4)
	 * 
	 * @param toughness L'endurance de comparaison
	 * @param op        L'opérateur de comparaison
	 * 
	 * @return {@code this}
	 */
	public Selectors toughness(final int toughness, final Operator op) {
		return toughness(new Nmbs(toughness), op);
	}

	public Selectors toughness(final Nmbs toughness, final Operator op) {
		return generic(new ToughnessSelector(toughness, op));
	}

	/**
	 * ****************** *** LAND *** ******************
	 */
	/**
	 * Test si le terrain possède l'un des types de {@code types}. </br>
	 * (Ex:foret ou plaine)
	 * 
	 * @param types Le(s) type(s) de terrain a tester
	 * 
	 * @return {@code this}
	 */
	public Selectors landType(final LandT... types) {
		return generic(new LandTypeSelector(LogicalOp.OR, types));
	}

	/**
	 * Test le(s) type(s) du terrain. Le terrain peut soit posséder l'un des types
	 * passer en parametre (ex:foret ou plaine) </br>
	 * Si {@code op == AND} Le terrain doit posséder tout les types. </br>
	 * (Ex:foret et plaine)
	 * 
	 * @param op    Si le terrain doit posseder l'un des types ou tous
	 * @param types Le(s) type(s) de terrain a tester
	 * 
	 * @return {@code this}
	 */
	public Selectors landType(final LogicalOp op, final LandT... types) {
		return generic(new LandTypeSelector(op, types));
	}

	/**
	 * ****************** *** UTILS *** ******************
	 */
	/**
	 * Methode utilitaire ajoutant un selecteur a la liste des selecteurs
	 * 
	 * @param selector Le selecteur a ajouter
	 * 
	 * @return {@code this}
	 */
	private Selectors generic(final Selector selector) {
		selectors.get(index).add(selector);
		return this;
	}

	/**
	 * Ou logic. Rajoute une nouvelle branche de selection. Tout les appelles de
	 * fonction après l'utilisation de ce OU ferront partit d'une nouvelle branche.
	 * Il peut y avoir plusieurs selecteur au seins de cette nouvelle branche</br>
	 * (Ex:détruissez l'artefact ciblée ou la créature engagée ciblée)</br>
	 * {@code new Selectors(CardT.ARTIFACT).or().cardType(CardT.CREATURE).state(State.TAPPED);}
	 * </br>
	 * </br>
	 * Il est possible d'inclure plusieurs ou. </br>
	 * (Ex:A chaque fois que vous lancé un sort de créature de force 4, 5, ou 6...)
	 * 
	 * @return {@code this}
	 */
	public Selectors or() {
		index++;
		selectors.add(new ArrayList<Object>());
		return this;
	}

	/**
	 * Non logic. Permet d'inverse le resultat du prochain selecteur. </br>
	 * (Ex:détruissez la créature non rouge ciblée)</br>
	 * </br>
	 * Si cette fonction est appellée à la fin d'une branche d'un selecteur, elle
	 * inverse le résultat de toute la branche. </br>
	 * 
	 * @return {@code this}
	 */
	public Selectors not() {
		selectors.get(index).add(LogicalOp.NOT);
		return this;
	}

	/**
	 * Pour chaque cible du tableau {@code targets}, on test si elle repond a tout
	 * les criteres de ce selecteur. Si c'est la cas, elle est ajoute a une liste
	 * renvoyé par la fonction.
	 * 
	 * @param thisCard La carte possedant ce selecteur
	 * @param targets  Le tableau de cible a tester
	 * 
	 * @return Une liste de toute les cibles repondant a tout les criteres de ce
	 *         selecteur
	 */
	public ArrayList<Targetable> match(final Game game, final Card thisCard, final Targetable... targets) {
		final ArrayList<Targetable> matchesTarget = new ArrayList<>();

		for (final Targetable target : targets)
			if (match(game, thisCard, target))
				matchesTarget.add(target);

		return matchesTarget;
	}

	/**
	 * Pour chaque carte de la liste {@code cards}, on test si elle repond a tout
	 * les criteres de ce selecteur. Si c'est la cas, elle est ajoute a une liste
	 * renvoyé par la fonction.
	 * 
	 * @param thisCard La carte possedant ce selecteur
	 * @param cards    La liste de carte à tester
	 * 
	 * @return Une liste de toute les cartes repondant a tout les criteres de ce
	 *         selecteur
	 */
	public ArrayList<? extends Targetable> matchAny(final Game game, final Card thisCard,
			final List<? extends Targetable> cards) {
		return cards.stream().filter(e -> match(game, thisCard, e)).collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<Card> matchCards(final Game game, final Card thisCard, final List<Card> cards) {
		return cards.stream().filter(e -> match(game, thisCard, e)).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Test si {@code target} correspond a tout les criteres de ce selecteur.
	 * {@code thisCard} est la carte possedant ce Selecteur. Soit dans un effet,
	 * dans un déclancheur...
	 * 
	 * @param thisCard La carte possedant ce selecteur
	 * @param target   La cible a tester
	 * @return {@code true} si {@code target} repond a tout les criteres de ce
	 *         selecteur. Sinon {@code false}
	 */
	public boolean match(final Game game, final Card thisCard, final Targetable target) {
		boolean first = true, not = false;
		for (final Object o : selectors.get(0))
			if (o instanceof Selector select) {
				boolean result = select.match(game, thisCard, target, data);

				// Si on doit inverser le resultat on l'inverse
				if (not) {
					not = false;
					result = !result;
				}

				first &= result;
			} else if (o instanceof LogicalOp op && op == LogicalOp.NOT)
				not = true; // Specifie d'inverser le prochain selecteur

		if (not)
			first = !first;

		for (int i = 1; i < selectors.size(); i++) {
			boolean second = true;
			not = false;
			for (final Object o : selectors.get(i))
				if (o instanceof Selector select) {
					boolean result = select.match(game, thisCard, target, data);

					// Si on doit inverser le resultat on l'inverse
					if (not) {
						not = false;
						result = !result;
					}

					second &= result;
				} else if (o instanceof LogicalOp op && op == LogicalOp.NOT)
					not = true; // Specifie d'inverser le prochain selecteur

			first |= not ? !second : second;
		}

		return isExcept() ? !first : first;
	}

	/**
	 * 
	 * @return Si les cibles sont "ciblées"
	 */
	public boolean canTarget() {
		return data.canTarget();
	}

	/**
	 * Définit si l'action est de choisir est "cibler". Car choisir n'est pas cibler
	 * 
	 * @param target Si les cibles sont "ciblées"
	 * 
	 * @return {@code this}
	 */
	public Selectors setTarget(final boolean target) {
		data.setTarget(target);
		return this;
	}

	/**
	 * Définit le nombre maximum de cible que le joueur doit choisir
	 * 
	 * @param maxTarget Le nombre maximum de cible à choisir
	 * 
	 * @return {@code this}
	 * 
	 * @see #maxTarget
	 * @see #target
	 */
	public Selectors target(final int maxTarget) {
		data.setTarget(true);
		this.maxTarget = new Nmbs(maxTarget);
		return this;
	}

	/**
	 * 
	 * @return Le nombre maximum de cible a choisir
	 */
	public Nmbs getMaxTarget() {
		return maxTarget;
	}

	/**
	 * Définit le nombre maximum de cible que le joueur doit choisir
	 * 
	 * @param maxTarget Le nombre maximum de cible a choisir
	 * 
	 * @return {@code this}
	 */
	public Selectors maxTarget(final int maxTarget) {
		this.maxTarget = new Nmbs(maxTarget);
		return this;
	}

	/**
	 * Définit le nombre maximum de cible que le joueur doit choisir
	 * 
	 * @param maxTarget Le nombre maximum de cible a choisir
	 * 
	 * @return {@code this}
	 */
	public Selectors maxTarget(final Nmbs maxTarget) {
		this.maxTarget = maxTarget;
		return this;
	}

	/**
	 * 
	 * @return Si le nombre de cible est "jusqu'a". (Ex:ciblé jusqu'a trois
	 *         creatures) peut etre donc 0, 1, 2 ou 3 cibles.
	 */
	public boolean isUpTo() {
		return (flag & UPTO) == UPTO;
	}

	/**
	 * Le nombre de cible est parfois pas définie mais sur un intervalle, comme
	 * "jusqu'a". (Ex:ciblé jusqu'a trois creatures). Ici 0, 1, 2 ou 3 cibles
	 * peivent etre selectionné.
	 * 
	 * @param upTo Si le nombre de cible est "jusqu'a"
	 * 
	 * @return {@code this}
	 */
	public Selectors setUpTo(final boolean upTo) {
		flag = (byte) (upTo ? flag | UPTO : flag & ~UPTO);
		return this;
	}

	/**
	 * Le nombre de cible est parfois pas définie mais sur un intervalle, comme
	 * "jusqu'a". (Ex:ciblé jusqu'a trois creatures). Ici 0, 1, 2 ou 3 cibles
	 * peuvent etre selectionné.
	 * 
	 * @return {@code this}
	 */
	public Selectors upTo() {
		return setUpTo(true);
	}

	/**
	 * 
	 * @return Si le résultat du selecteur doit être inversé
	 */
	public boolean isExcept() {
		return (flag & EXCEPT) == EXCEPT;
	}

	/**
	 * Inverse le résultat du selecteur
	 * 
	 * @return Ce {@code Selectors}
	 */
	public Selectors except() {
		flag = (byte) (flag | EXCEPT);
		return this;
	}

	public Place[] getOptiPlace() {
		return optiPlace;
	}

	public Owner getOptiOwner() {
		return optiOwner;
	}

	@Override
	public int hashCode() {
		return Objects.hash(maxTarget, selectors, data, flag, index, optiPlace, optiOwner);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Selectors other)
			return Objects.equals(maxTarget, other.maxTarget) && Objects.equals(selectors, other.selectors)
					&& Objects.equals(data, other.data) && index == other.index && flag == other.flag
					&& optiPlace == other.optiPlace && optiOwner == other.optiOwner;

		return false;
	}

}
