package magic.logic.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.CardAbilities;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.place.Place;
import magic.logic.utils.Counterable;

public class Card extends Targetable implements Comparable<Card>, Cloneable, Counterable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1689427066271786168L;

	public final static String BACK_CARD = "backCard";

	public final static String ATTACHED_CARDS = "attachedCards";

	public final static String SELECTED_BY = "selectedByID";

	public final static String COUNTERS = "counters";

	public final static String ABILITIES = "abilities";

	public final static String KEYWORDS = "keywords";

	public final static String NAME = "name";

	public final static String FLAVOR = "flavor";

	public final static String ORACLE = "oracle";

	public final static String ARTIST = "artist";

	public final static String SET = "set";

	public final static String PATH = "path";

	public final static String MULTIVERSE_ID = "multiverseID";

	public final static String COLLECTION_NUMBER = "collectionNumber";

	public final static String COLOR_IDENTITY = "colorIdentity";

	public final static String TYPE = "type";

	public final static String RARITY = "rarity";

	public final static String OWNER = "owner";

	public final static String CONTROLLER = "controller";

	public final static String PLACE = "place";

	public final static String STATE = "state";

	public final static String CURRENT_STAT = "currentStat";

	public final static String DEFAULT_STAT = "defaultStat";

	public final static String MANA_COST = "manaCost";

	public final static String PAYED_MANA_COST = "payedManaCost";

	public final static String CREATURE_TYPES = "creatureTypes";

	public final static String LAND_TYPES = "landTypes";

	public final static String PLANESWALKER_TYPE = "planeswlakerType";

	public final static String ENCHANTMENT_TYPE = "enchantmentType";

	public final static String SPELL_TYPE = "spellType";

	public final static String ARTIFACT_TYPE = "artifactType";

	private final HashMap<String, Object> datas;

	public Card(final String name, final CardT... types) {
		datas = new HashMap<>();
		setName(name);

		// On ajoute les type de la carte
		datas.put(TYPE, new ArrayList<CardT>());
		getType().addAll(List.of(types));

		datas.put(STATE, new State[] { State.UNTAPPED, State.UNFLIPPED, State.FACE_UP, State.PHASED_IN });
		setPlace(Place.DECK);
		set(ABILITIES, new CardAbilities());
		set(ATTACHED_CARDS, new ArrayList<Card>());
	}

	public Card(final Card card) {
		datas = new HashMap<>(card.datas);
	}

	public void set(final String key, final Object object) {
		datas.put(key, object);
	}

	public void setAll(final String key, final Object... objects) {
		datas.put(key, objects);
	}

	public Object get(final String key) {
		return datas.get(key);
	}

	public String getStr(final String key) {
		final Object obj = datas.get(key);
		return obj == null ? "" : (String) obj;
	}

	public int getInt(final String key) {
		final Object obj = datas.get(key);
		return obj == null ? 0 : (int) obj;
	}

	public Stat getStat(final String key) {
		final Object obj = datas.get(key);
		return obj == null ? new Stat(0, 0) : (Stat) obj;
	}

	public ManaCost getMC(final String key) {
		final Object obj = datas.get(key);
		return obj == null ? new ManaCost() : (ManaCost) obj;
	}

	public Card getBackCard() {
		return (Card) datas.get(BACK_CARD);
	}

	public void setBackCard(final Card backCard) {
		datas.put(BACK_CARD, backCard);
	}

	public boolean isDoubleFaced() {
		return getBackCard() != null;
	}

	public CardAbilities getCardAbilities() {
		return (CardAbilities) datas.get(ABILITIES);
	}

	public ArrayList<Card> getAttachedCards() {
		return (ArrayList<Card>) datas.get(ATTACHED_CARDS);
	}

	public EnumMap<CounterType, Integer> getCounters() {
		return (EnumMap<CounterType, Integer>) datas.get(COUNTERS);
	}

	public ArrayList<Integer> getSelectedBy() {
		return (ArrayList<Integer>) datas.get(SELECTED_BY);
	}

	public void setSelectedBy(final ArrayList<Integer> ids) {
		datas.put(SELECTED_BY, ids);
	}

	public void addSelectedByID(final int id) {
		ArrayList<Integer> ids = getSelectedBy();
		if (ids == null) {
			ids = new ArrayList<Integer>();
			ids.add(id);
			setSelectedBy(ids);
		} else
			ids.add(id);
	}

	public String[] getKeywords() {
		final Object obj = datas.get(KEYWORDS);
		return obj == null ? new String[] {} : (String[]) obj;
	}

	public void setKeywords(final String... strings) {
		datas.put(KEYWORDS, strings);
	}

	public String getName() {
		return getStr(NAME);
	}

	public void setName(final String name) {
		datas.put(NAME, name);
	}

	public String getFlavor() {
		return getStr(FLAVOR);
	}

	public void setFlavor(final String flavor) {
		datas.put(FLAVOR, flavor);
	}

	public String getOracle() {
		return getStr(ORACLE);
	}

	public void setOracle(final String oracle) {
		datas.put(ORACLE, oracle);
	}

	public String getArtist() {
		return getStr(ARTIST);
	}

	public void setArtist(final String artist) {
		datas.put(ARTIST, artist);
	}

	public String getSet() {
		return getStr(SET);
	}

	public void setSet(final String set) {
		datas.put(SET, set);
	}

	public String getPath() {
		return getStr(PATH);
	}

	public void setPath(final String path) {
		datas.put(PATH, path);
	}

	public int getMultiverseID() {
		return getInt(MULTIVERSE_ID);
	}

	public void setMultiverseID(final int multiverseID) {
		datas.put(MULTIVERSE_ID, multiverseID);
	}

	public int getCollectorNumber() {
		return getInt(COLLECTION_NUMBER);
	}

	public void setCollectionNumber(final int collectionNumber) {
		datas.put(COLLECTION_NUMBER, collectionNumber);
	}

	public ArrayList<CColor> getColorIdentity() {
		return (ArrayList<CColor>) datas.get(COLOR_IDENTITY);
	}

	public CColor getIdentity() {
		final ArrayList<CColor> cols = getColorIdentity();
		return (cols == null || cols.size() == 0) ? CColor.COLORLESS
				: cols.size() > 1 ? CColor.MULTICOLORED : cols.get(0);
	}

	public void setColorIdentity(final CColor... colors) {
		final ArrayList<CColor> cols = new ArrayList<>();
		for (final CColor col : colors)
			cols.add(col);
		datas.put(COLOR_IDENTITY, cols);
	}

	public void setColorIdentity(final ArrayList<CColor> colors) {
		datas.put(COLOR_IDENTITY, colors);
	}

	public ArrayList<CardT> getType() {
		return (ArrayList<CardT>) datas.get(TYPE);
	}

	public boolean hasType(final CardT type) {
		return getType().contains(type);
	}

	public Rarity getRarity() {
		return (Rarity) datas.get(RARITY);
	}

	public void setRarity(final Rarity rarity) {
		datas.put(RARITY, rarity);
	}

	public Owner getOwner() {
		return (Owner) datas.get(OWNER);
	}

	public void setOwner(final Owner owner) {
		datas.put(OWNER, owner);
	}

	public Owner getController() {
		return (Owner) datas.get(CONTROLLER);
	}

	public void setController(final Owner owner) {
		datas.put(CONTROLLER, owner);
	}

	public Place getPlace() {
		return (Place) datas.get(PLACE);
	}

	public void setPlace(final Place place) {
		datas.put(PLACE, place);
	}

	public State[] getState() {
		return (State[]) datas.get(STATE);
	}

	public void changeState(final State... states) {
		final State[] cardStates = getState();
		for (final State state : states)
			cardStates[state.getIndex()] = state;
	}

	public boolean hasState(final State state) {
		return getState()[state.getIndex()] == state;
	}

	/**
	 *****************************
	 **** CARD WITH MANA COST ****
	 *****************************
	 */
	public ManaCost getCardCost() {
		return getMC(MANA_COST);
	}

	public ManaCost getPayedCost() {
		return getMC(PAYED_MANA_COST);
	}

	/**
	 ***********************
	 **** CREATURE CARD ****
	 ***********************
	 */
	public ArrayList<CreatureT> getCreatureType() {
		return (ArrayList<CreatureT>) datas.get(CREATURE_TYPES);
	}

	/**
	 * 
	 * @return Les stats avec les modificateurs ajoutés (+1/+1, -4/-0...)
	 */
	public Stat getCurrentStat() {
		return getStat(CURRENT_STAT);
	}

	/**
	 * 
	 * @return Les stats par defaut de la créature, ils ne changent pas
	 */
	public Stat getDefaultStat() {
		return getStat(DEFAULT_STAT);
	}

	public void setDefaultStat(final int power, final int toughness) {
		datas.put(DEFAULT_STAT, new Stat(power, toughness));
	}

	public int getPower() {
		return getCurrentStat().getPower() + getCounter(CounterType.PLUS_ONE) - getCounter(CounterType.MINUS_ONE);
	}

	public int getBasicPower() {
		return getDefaultStat().getPower();
	}

	public int getToughness() {
		return getCurrentStat().getToughness() + getCounter(CounterType.PLUS_ONE) - getCounter(CounterType.MINUS_ONE);
	}

	public int getBasicToughness() {
		return getDefaultStat().getToughness();
	}

	public void setStat(final int power, final int toughness) {
		final Stat stat = new Stat(power, toughness);
		datas.put(DEFAULT_STAT, stat);
		datas.put(CURRENT_STAT, stat);
	}

	/**
	 ***********************
	 ****** LAND CARD ******
	 ***********************
	 */
	public ArrayList<LandT> getLandTypes() {
		return (ArrayList<LandT>) datas.get(LAND_TYPES);
	}

	/**
	 ***************************
	 **** PLANESWALKER CARD ****
	 ***************************
	 */
	public PT getPlaneswalker() {
		return (PT) datas.get(PLANESWALKER_TYPE);
	}

	/**
	 **************************
	 **** ENCHANTMENT CARD ****
	 **************************
	 */
	public EnchantT getEnchantmentType() {
		return (EnchantT) datas.get(ENCHANTMENT_TYPE);
	}

	/**
	 **************************
	 ******* SPELL CARD *******
	 **************************
	 */
	public SpellT getSpellType() {
		return (SpellT) datas.get(SPELL_TYPE);
	}

	/**
	 **************************
	 **** ARTIFACT CARD ****
	 **************************
	 */
	public ArtifactT getArtifactType() {
		return (ArtifactT) datas.get(ARTIFACT_TYPE);
	}

	/**
	 ******************
	 **** COUNTERS ****
	 ******************
	 */
	public boolean haveCounter(final CounterType counter) {
		return counters().containsKey(counter);
	}

	public Integer getCounterIfPresent(final CounterType counter) {
		return counters().get(counter);
	}

	public int getCounter(final CounterType counter) {
		final Integer count = getCounterIfPresent(counter);
		return count != null ? count : 0;
	}

	public int addCounter(final Counter counter) {
		return addCounter(counter.getType(), counter.getCount());
	}

	public int addCounter(final CounterType counter, int number) {
		final EnumMap<CounterType, Integer> counters = counters();

		// Si c'est un marqueur +1/+1 on verifie si il y en a des -1/-1. Si c'est le cas
		// on les annules
		if (counter == CounterType.PLUS_ONE) {
			final int co = getCounter(CounterType.MINUS_ONE);
			number = number - co;

			final Integer result = counters.put(number < 0 ? CounterType.MINUS_ONE : CounterType.PLUS_ONE,
					Math.abs(number));
			return result == null ? 0 : result;
		} else if (counter == CounterType.MINUS_ONE) {
			final int co = getCounter(CounterType.PLUS_ONE);
			number = number - co;
			final Integer result = counters.put(number > 0 ? CounterType.MINUS_ONE : CounterType.PLUS_ONE,
					Math.abs(number));
			return result == null ? 0 : result;
		}

		// On ajoute les capacitiés si c'est un marqueur de capacité
		if (!test(counter, number, CounterType.FLYING, Capacities.FLYING))
			test(counter, number, CounterType.REACH, Capacities.REACH);

		if (!test(counter, number, CounterType.TRAMPLE, Capacities.TRAMPLE))
			test(counter, number, CounterType.HEXPROOF, Capacities.HEXPROOF);

		if (!test(counter, number, CounterType.FIRST_STRIKE, Capacities.FIRST_STRIKE))
			test(counter, number, CounterType.DOUBLE_STRIKE, Capacities.DOUBLE_STRIKE);

		if (!test(counter, number, CounterType.HASTE, Capacities.HASTE))
			test(counter, number, CounterType.MENACE, Capacities.MENACE);

		if (!test(counter, number, CounterType.LIFELINK, Capacities.LIFELINK))
			test(counter, number, CounterType.INDESTRUCTIBLE, Capacities.INDESTRUCTIBLE);

		if (!test(counter, number, CounterType.VIGILANCE, Capacities.VIGILANCE))
			test(counter, number, CounterType.DEATHTOUCH, Capacities.DEATHTOUCH);

		final int nmb = counters.merge(counter, number, (i, j) -> i + j);
		datas.put(COUNTERS, counters);
		return nmb;
	}

	private boolean test(final CounterType counter, final int number, final CounterType toTest, final Capacities c) {
		if (counter == toTest) {
			if (number < 0)
				getCardAbilities().getCapacities().remove(c);
			else
				addCapacities(c);

			return true;
		}

		return false;
	}

	public int removeCounter(final CounterType counter, final int number) {
		final EnumMap<CounterType, Integer> counters = counters();
		final int nmb = counters.merge(counter, 0, (i, j) -> i - number);
		datas.put(COUNTERS, counters);
		return nmb;
	}

	public int removeCounter(final CounterType counter) {
		final EnumMap<CounterType, Integer> counters = counters();
		final int nmb = counters.remove(counter);
		datas.put(COUNTERS, counters);
		return nmb;
	}

	public void removeAllCounters() {
		final EnumMap<CounterType, Integer> counters = counters();
		counters.clear();
		datas.put(COUNTERS, counters);
	}

	private EnumMap<CounterType, Integer> counters() {
		EnumMap<CounterType, Integer> counters = getCounters();
		if (counters == null)
			counters = new EnumMap<>(CounterType.class);

		return counters;
	}

	/**
	 *******************
	 ****** UTILS ******
	 *******************
	 */
	public boolean attachCard(final Card card) {
		ArrayList<Card> attachedCards = getAttachedCards();
		if (attachedCards == null)
			attachedCards = new ArrayList<>();

		final boolean removed = attachedCards.add(card);
		set("attachedCards", attachedCards);
		return removed;
	}

	public void addAbility(final Abilities... abilities) {
		final CardAbilities ca = getCardAbilities();
		for (final Abilities ability : abilities) {
			ability.setParent(this);
			ca.addAbilitiy(ability);
		}

		set(ABILITIES, ca);
	}

	public void addCapacities(final Capacities... evergreens) {
		final CardAbilities ca = getCardAbilities();
		ca.getCapacities().addAll(List.of(evergreens));
		set(ABILITIES, ca);
	}

	@Override
	public int compareTo(final Card card) {
		int comp = getRarity().compareTo(card.getRarity());
		if (comp == 0 && datas.get(MANA_COST) != null)
			comp = getCardCost().compareTo(card.getCardCost());
		return comp == 0 ? getName().compareTo(card.getName()) : comp;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":(name:" + getName() + ", owner:" + getOwner() + ", place:" + getPlace()
				+ ", state:" + getState() + ")";
	}

	@Override
	public Card clone() {
		return new Card(this);
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + datas.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;

		return datas.equals(((Card) obj).datas);
	}

}
