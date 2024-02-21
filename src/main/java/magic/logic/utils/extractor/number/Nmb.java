package magic.logic.utils.extractor.number;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.abilities.condition.Cumul;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.number.operation.NegateNumber;
import magic.logic.utils.extractor.number.operation.OperationNumber;
import magic.logic.utils.extractor.number.operation.OperationNumber.Operation;
import magic.logic.utils.extractor.number.operation.Operations;
import magic.logic.utils.extractor.number.operation.RoundNumber;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class Nmb implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4419168742217138453L;

	/**
	 * L'extracteur, c'est lui qui se charge de recuperer un nombre en fonction de
	 * certain critere
	 */
	private Extractor extractor;

	/**
	 * La liste des operation a effectuer sur le nombre extrait
	 */
	private final List<Operations> operations;

	/**
	 * Constructeur par defaut initialisant une liste vide d'operation
	 */
	public Nmb() {
		operations = new ArrayList<>();
	}

	/**
	 * Recupere le nombre de marqueur de type {@code counterType} place, soit sur
	 * cette carte, soit sur la premiere transmise par l'appeleur de ce {@code Nmb}
	 * 
	 * @param targetType  Sur quelle cible recupere les marqieurs. Soit
	 *                    TargetType.THIS_CARD ou TargetType.RETURNED_TARGET
	 * @param counterType Le type du marqueur a recupere
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getCounter(final TargetType targetType, final CounterType counterType) {
		extractor = new CounterExtractor(targetType, counterType);
		return this;
	}

	/**
	 * Recupere la devotion au mana {@code mana} du joueur posseseur de la carte.
	 * 
	 * @param mana La couleur de la devotion a recuperer
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getDevotion(final CColor mana) {
		extractor = new DevotionExtractor(mana);
		return this;
	}

	/**
	 * Recupere le nombre de couleurs de mana differentes pour lance cette carte
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getConverge() {
		extractor = new ConvergeExtractor();
		return this;
	}

	/**
	 * Compte le nombre de mana neigeux utiliszé pour lancer la carte possedant ce
	 * Nmb.
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getSnowMana() {
		extractor = new SnowManaExtractor();
		return this;
	}

	/**
	 * Recupere le mana {@code MCType.X} possiblement contenue dans le donnés de
	 * l'effet appelant ce {@code Nmb}
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getXFromEffect() {
		extractor = new XExtractor(true);
		return this;
	}

	/**
	 * Recupere le mana {@code MCType.X} depensé pour lancé ce sort
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getX() {
		extractor = new XExtractor();
		return this;
	}

	/**
	 * Recupere les donnés possiblement transmis par l'effet appelant ce
	 * {@code Nmb}. Les donnés peuvent etre des nombre (ex:vie gagné, blessure
	 * infligé). Ou peuvent etre un cout de mana, dans ce cas le cout convertie de
	 * mana est extrait. Ou encore des marqueur, dans ca cas le nombre de marqueur
	 * est extrait
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getFromEffect() {
		extractor = new EffectExtractor();
		return this;
	}

	/**
	 * Recupere une donné de cette carte. Pouvant etre le cout convertie de mana,
	 * son attaque ou encore son endurance.
	 * 
	 * @param numberType Le type de valuer a récuperer
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getThis(final NumberType numberType) {
		return getThis(numberType, TargetType.THIS_CARD);
	}

	/**
	 * Recupere une donne de la cible. Pouvant etre cette carte ou bien la premiere
	 * possiblement renvoiyé par un effet. La donnée peut le cout convertie de mana
	 * de la carte, sa force ou encore son endurance.
	 * 
	 * @param numberType Le type de valeur a récuperer
	 * @param targetType La cible, soit TargetType.THIS_CARD ou bien
	 *                   TargetType.RETURNED_TARGET
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getThis(final NumberType numberType, final TargetType targetType) {
		extractor = new ThisExtractor(numberType, targetType);
		return this;
	}

	/**
	 * Recupere la donnée {@code NumberType} la plus eleve parmis les cible
	 * {@code TargetType} du selecteur {@code select}.
	 * 
	 * @param select     Le selecteur
	 * @param targetType Sur quelle cible appliquer le selecteur
	 * @param type       Le type de donné à extraire
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getHighest(final Selectors select, final TargetType targetType, final NumberType type) {
		extractor = new ExtremumExtractor(select, type, targetType, true);
		return this;
	}

	/**
	 * Recupere la donnée {@code NumberType} la plus base parmis les cible
	 * {@code TargetType} du selecteur {@code select}.
	 * 
	 * @param select     Le selecteur
	 * @param targetType Sur quelle cible appliquer le selecteur
	 * @param type       Le type de donné à extraire
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getLowest(final Selectors select, final TargetType targetType, final NumberType type) {
		extractor = new ExtremumExtractor(select, type, targetType, false);
		return this;
	}

	/**
	 * Recupere la donnée {@code NumberType} pour chaque cible extraite du
	 * selecteur. Extrait ensuite la somme de toute ces données
	 * 
	 * @param select     Le selecteur
	 * @param targetType Sur quelle cible appliquer le selecteur
	 * @param type       Le type de donné à extraire
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getSomme(final Selectors select, final TargetType targetType, final NumberType type) {
		extractor = new SommeExtractor(select, targetType, type);
		return this;
	}

	/**
	 * Recupere la vie du joueur possesseur de cette carte
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getLife() {
		return getLife(Owner.YOU);
	}

	/**
	 * Recupere la vie du joueur {@code owner}
	 * 
	 * @param owner Le joueur sur lequel applique l'extraction
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getLife(final Owner owner) {
		extractor = new LifeExtractor(owner);
		return this;
	}

	/**
	 * Compte le nombre de carte répondant a toute les conditions du selecteur
	 * {@code select}. Par defaut toute les cartes du jeu sont prises en compte
	 * (exile, cimetierre, champ de bataille)
	 * 
	 * @param select      Le selecteur
	 * @param includeThis Si cette carte doit etre pris en compte ou non
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getCardCount(final Selectors select, final boolean includeThis) {
		return getCardCount(select, TargetType.ALL_CARD, includeThis);
	}

	/**
	 * Compte le nombre de carte répondant a toute les conditions du selecteur
	 * {@code select}. Le selecteur s'applique sur {@code type}
	 * 
	 * @param select      Le selecteur
	 * @param type        Le type de cible
	 * @param includeThis Si cette carte doit etre pris en compte ou non
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getCardCount(final Selectors select, final TargetType type, final boolean includeThis) {
		extractor = new CardCountExtractor(select, type, includeThis);
		return this;
	}

	/**
	 * Récupère le nombre maximum de carte qui partagent un type de creature en
	 * commun. L'algorithme est conçu pour trouver le type le plus partagé. Par
	 * défaut seules les cartes sur le champs de bataille et sous le controle du
	 * propriétaire de la carte possédant ce Nmb sont prises en compte. Pour
	 * utiliser un autre selecteur voire l'autre fonction.
	 * 
	 * @return Ce {@code Nmb}
	 * @see #getSharesCreatureType(Selectors)
	 */
	public Nmb getSharesCreatureType() {
		extractor = new SharesCreatureTExtractor(
				new Selectors(CardT.CREATURE).place(Place.BATTLEFIELD).owner(Owner.YOU));
		return this;
	}

	/**
	 * Récupère le nombre maximum de carte qui partagent un type de creature en
	 * commun. L'algorithme est conçu pour trouver le type le plus partagé.
	 * 
	 * @param select Précise les cartes à tester
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getSharesCreatureType(final Selectors select) {
		extractor = new SharesCreatureTExtractor(select);
		return this;
	}

	/**
	 * Récupère le nombre de carte dans votre party. (Up to one rogue, one warrior,
	 * one wizard and one warrior)
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getParty() {
		extractor = new PartyExtractor();
		return this;
	}

	/**
	 * Extrait un nombre des différentes actions {@code event} qui ont pu se passer
	 * ce tour-ci par le joueur {@code owner}. </br>
	 * {@code mode} permet de parametrer l'extraction :
	 * <ul>
	 * <li>Si mode == Cumul.TARGET : Ce sera le nombre de cible trouvé par le
	 * selecteur et ayant effectué l'action qui seront compté. Un selecteur
	 * peut-être utilisé pour affiner le comptage. Si aucun selecteur n'est
	 * spécifié, toute les cibles seront comptés. Sinon on compte juste le nombre de
	 * cibles qui correspondent au selecteur.</br>
	 * Par exemple : "Gagner X point de vie, X étant le nombre de créature morte ce
	 * tour-ci".</li>
	 * <li>Si mode == Cumul.DATA : Ce sera la somme des données de l'event de la
	 * pile qui sera récupéré. </br>
	 * Ex : "piochez X cartes, X étant le nombres de point de vie que vous avez
	 * gagné ce tour-ci".</li>
	 * <li>Si mode == Cumul.COUNT : compte le nombre de fois que l'event s'est
	 * éxecuté. </br>
	 * Ex : "Piochez X cartes, X étant le nombres de fois que vous avez gagné des
	 * point de vie ce tour-ci".</li> </lu>
	 *
	 * @param event  L'action à tester
	 * @param select Le selecteur potentiel
	 * @param owner  Pendant le tour de quel joueur
	 * @param mode   Permet de parametrer l'extraction, un de :
	 *               {@code TARGET, DATA, COUNT}
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getTurn(final Event event, final Selectors select, final Owners owner, final Cumul mode) {
		extractor = new TurnExtractor(event, select, owner, mode);
		return this;
	}

	/**
	 * Génère un nombre aléatoire entre l'intervalle [min; max] renseigné. (min et
	 * max sont compris). {@code Min, max} peuvent être également des extracteur
	 * 
	 * @param min Le nombre minimale de l'intervalle
	 * @param max Le nombre maximale de l'intervalle
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getRandom(final Nmbs min, final Nmbs max) {
		extractor = new RandomExtractor(min, max);
		return this;
	}

	/**
	 * Génère un nombre aléatoire entre l'intervalle [min; max] renseigné. (min et
	 * max sont compris).
	 * 
	 * @param min Le nombre minimale de l'intervalle
	 * @param max Le nombre maximale de l'intervalle
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getRandom(final int min, final int max) {
		extractor = new RandomExtractor(new Nmbs(min), new Nmbs(max));
		return this;
	}

	/**
	 * Génère un nombre aléatoire entre l'intervalle [0; max] renseigné. (0 et max
	 * sont compris).
	 * 
	 * @param max Le nombre maximale de l'intervalle
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb getRandom(final int max) {
		extractor = new RandomExtractor(new Nmbs(0), new Nmbs(max));
		return this;
	}

	/**
	 ********************
	 **** OPERATIONS ****
	 ********************
	 */

	/**
	 * Ajoute {@code nmb} au resultat obtenue de l'extraction
	 * 
	 * @param nmb Le nombre a ajouter
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb add(final int nmb) {
		operations.add(new OperationNumber(Operation.ADD, nmb));
		return this;
	}

	/**
	 * Ajoute {@code nmb} au resultat obtenue de l'extraction
	 * 
	 * @param nmb Le nombre a ajouter
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb add(final Nmbs nmb) {
		operations.add(new OperationNumber(Operation.ADD, nmb));
		return this;
	}

	/**
	 * Soustrait {@code nmb} au resultat obtenue de l'extraction
	 * 
	 * @param nmb Le nombre a soustraire
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb sub(final int nmb) {
		operations.add(new OperationNumber(Operation.SUB, nmb));
		return this;
	}

	/**
	 * Soustrait {@code nmb} au resultat obtenue de l'extraction
	 * 
	 * @param nmb Le nombre a soustraire
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb sub(final Nmbs nmb) {
		operations.add(new OperationNumber(Operation.SUB, nmb));
		return this;
	}

	/**
	 * Multiplie par {@code nmb} le resultat obtenue de l'extraction
	 * 
	 * @param nmb Le multiplicateur
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb mul(final int nmb) {
		operations.add(new OperationNumber(Operation.MUL, nmb));
		return this;
	}

	/**
	 * Multiplie par {@code nmb} le resultat obtenue de l'extraction
	 * 
	 * @param nmb Le multiplicateur
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb mul(final Nmbs nmb) {
		operations.add(new OperationNumber(Operation.MUL, nmb));
		return this;
	}

	/**
	 * Divise par {@code nmb} le resultat obtenue de l'extraction
	 * 
	 * @param nmb Le diviseur
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb div(final int nmb) {
		operations.add(new OperationNumber(Operation.DIV, nmb));
		return this;
	}

	/**
	 * Divise par {@code nmb} le resultat obtenue de l'extraction
	 * 
	 * @param nmb Le diviseur
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb div(final Nmbs nmb) {
		operations.add(new OperationNumber(Operation.DIV, nmb));
		return this;
	}

	/**
	 * Rends negatif le resultat obtenue de l'extraction
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb negate() {
		operations.add(new NegateNumber());
		return this;
	}

	/**
	 * Arrondie le resultat des calculs a l'unite superieur
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb roundedUp() {
		operations.add(new RoundNumber(true));
		return this;
	}

	/**
	 * Arrondie le resultat des calculs a l'unite inferieur
	 * 
	 * @return Ce {@code Nmb}
	 */
	public Nmb roundedDown() {
		operations.add(new RoundNumber(false));
		return this;
	}

	/**
	 * Extrait le nombre en fonction de l'extracteur choisit. Sur ce nombre et
	 * ensuite applique diverse operation (add, mul, negate, roundUp...).
	 * 
	 * @param game     Le jeu contenant toute les informations de la partie actuelle
	 * @param thisCard Le Carte possesseur de ce {@code Nmb}
	 * @param data     Les donnés transmis par l'effet/condition ayant declanché ce
	 *                 {@code Nmb}
	 * 
	 * @return Le resultat finale
	 */
	public int get(final Game game, final Card thisCard, final EffectData data) {
		float nmb = 0f;

		if (extractor != null)
			nmb = extractor.get(game, thisCard, data);

		for (final Operations ope : operations)
			nmb = ope.apply(nmb, game, thisCard, data);

		return (int) nmb;
	}

	/**
	 * Convertit ce {@code Nmb} en {@code Nmbs}
	 * 
	 * @return Un {@code Nmbs}
	 */
	public Nmbs s() {
		return new Nmbs(this);
	}
	
	@Override
	public String toString() {
		return "X";
	}

	@Override
	public int hashCode() {
		return Objects.hash(extractor, operations);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Nmb other)
			return Objects.equals(extractor, other.extractor) && Objects.equals(operations, other.operations);

		return false;
	}

}
