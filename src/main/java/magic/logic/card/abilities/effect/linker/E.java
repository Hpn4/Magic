package magic.logic.card.abilities.effect.linker;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Stat;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.condition.SelectorCondition;
import magic.logic.card.abilities.effect.AddManaEffect;
import magic.logic.card.abilities.effect.AttachCardEffect;
import magic.logic.card.abilities.effect.CostEffect;
import magic.logic.card.abilities.effect.CounterEffect;
import magic.logic.card.abilities.effect.CreateTokenEffect;
import magic.logic.card.abilities.effect.DealsDamageEffect;
import magic.logic.card.abilities.effect.DestroyEffect;
import magic.logic.card.abilities.effect.DrawCardEffect;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.LifeEffect;
import magic.logic.card.abilities.effect.LifeEffect.LifeMode;
import magic.logic.card.abilities.effect.LookAtLibraryEffect;
import magic.logic.card.abilities.effect.MoveEffect;
import magic.logic.card.abilities.effect.PutInLibraryEffect;
import magic.logic.card.abilities.effect.ReturnFromGraveyardEffect;
import magic.logic.card.abilities.effect.SearchLibraryEffect;
import magic.logic.card.abilities.effect.ShuffleLibrary;
import magic.logic.card.abilities.effect.TapEffect;
import magic.logic.card.abilities.effect.inversable.AddAbilitiesEffect;
import magic.logic.card.abilities.effect.inversable.AddCapacitiesEffect;
import magic.logic.card.abilities.effect.inversable.AddCardTypeEffect;
import magic.logic.card.abilities.effect.inversable.ExileEffect;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.effect.inversable.ModifyStatEffect;
import magic.logic.card.abilities.effect.linker.UntilEffect.UntilMode;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.OwnerExtractor;
import magic.logic.utils.extractor.OwnerExtractor.OwnerTarget;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.ManaCosts;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public abstract class E {

	/**
	 * **************** *** ATTACH CARD EFFECT *** *****************
	 */
	public static Effect attachCard(final Selectors select) {
		return new AttachCardEffect(select);
	}

	public static Effect attachCard(final Selectors select, final TargetType targetType) {
		return new AttachCardEffect(select, targetType);
	}

	/**
	 * **************** *** CREATE TOKEN EFFECT *** *****************
	 */
	public static Effect createToken(final int count, final Card token) {
		return new CreateTokenEffect(count, token);
	}

	public static Effect createToken(final int count, final Card token, final Owners who) {
		return new CreateTokenEffect(new Nmbs(count), token, who, token.getDefaultStat(), null);
	}

	public static Effect createToken(final int count, final Card token, final Nmb stat) {
		return new CreateTokenEffect(count, token, stat);
	}

	public static Effect createToken(final Nmbs count, final Card token, final Stat stat) {
		return new CreateTokenEffect(count, token, new Owners(Owner.YOU), stat, null);
	}

	/**
	 * **************** *** COUNTER EFFECT *** *****************
	 */

	/**
	 * **************** *** ADD COUNTER EFFECT *** *****************
	 */
	public static Effect addCounter(final CounterType type, final int count) {
		return new CounterEffect(null, TargetType.THIS_CARD, type, count, true);
	}

	public static Effect addCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final int count) {
		return new CounterEffect(target, targetType, type, count, true);
	}

	public static Effect addCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final Nmbs count) {
		return new CounterEffect(target, targetType, type, count, true);
	}

	/**
	 * **************** *** REMOVE COUNTER EFFECT *** *****************
	 */
	public static Effect removeCounter(final CounterType type, final int count) {
		return new CounterEffect(null, TargetType.THIS_CARD, type, count, false);
	}

	public static Effect removeCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final int count) {
		return new CounterEffect(target, targetType, type, count, false);
	}

	public static Effect removeCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final Nmbs count) {
		return new CounterEffect(target, targetType, type, count, false);
	}

	/**
	 * **************** *** DEAL DAMAGE EFFECT *** *****************
	 */
	public static Effect damage(final Selectors selector, final TargetType targetType, final int count) {
		return new DealsDamageEffect(selector, targetType, count);
	}

	public static Effect damage(final Selectors selector, final TargetType targetType, final Nmbs count) {
		return new DealsDamageEffect(selector, targetType, count);
	}

	/**
	 * **************** *** DESTROY EFFECT *** *****************
	 */
	public static Effect destroy(final Selectors selector) {
		return destroy(selector, TargetType.ALL_CARD);
	}

	public static Effect destroy(final Selectors selector, final TargetType targetType) {
		return new DestroyEffect(selector, targetType);
	}

	/**
	 * **************** *** EXILE EFFECT *** *****************
	 */
	public static Effect exile(final Selectors selector, final TargetType targetType) {
		return new ExileEffect(selector, targetType);
	}

	public static Effect exile(final Selectors selector) {
		return new ExileEffect(selector, TargetType.ALL_CARD);
	}

	/**
	 * **************** *** TAP EFFECT *** *****************
	 */
	public static Effect tap(final Selectors selector, final TargetType targetType) {
		return new TapEffect(selector, targetType, true);
	}

	public static Effect untap(final Selectors selector, final TargetType targetType) {
		return new TapEffect(selector, targetType, false);
	}

	/**
	 * **************** *** MODIFY STAT EFFECT *** *****************
	 */
	public static InversableEffect get(final Selectors selector, final TargetType targetType, final Stat stat) {
		return new ModifyStatEffect(selector, targetType, stat);
	}

	public static InversableEffect get(final Selectors selector, final TargetType targetType, final Nmb stat) {
		return get(selector, targetType, new Nmbs(stat), new Nmbs(stat));
	}

	public static InversableEffect get(final Selectors selector, final TargetType targetType, final Nmbs power,
			final Nmbs toughness) {
		return new ModifyStatEffect(selector, targetType, power, toughness);
	}

	/**
	 * **************** *** ADD CAPACITIES EFFECT *** *****************
	 */
	public static InversableEffect gains(final Selectors select, final TargetType targetType,
			final Capacities... capacities) {
		return new AddCapacitiesEffect(select, targetType, capacities);
	}

	public static InversableEffect gains(final Capacities... capacities) {
		return new AddCapacitiesEffect(null, TargetType.THIS_CARD, capacities);
	}

	/**
	 * **************** *** ADD ABILITY EFFECT *** *****************
	 */
	public static InversableEffect have(final Selectors select, final TargetType targetType, final Abilities ability) {
		return new AddAbilitiesEffect(select, targetType, ability);
	}

	/**
	 * **************** *** ADD CARD TYPE EFFECT *** *****************
	 */
	public static InversableEffect is(final Selectors select, final TargetType targetType, final CardT... cardTypes) {
		return new AddCardTypeEffect(select, targetType, cardTypes);
	}

	/**
	 * **************** *** LIBRARY EFFECT *** *****************
	 */
	public static Effect shuffle() {
		return shuffle(false);
	}

	public static Effect shuffle(final boolean shuffleThisCard) {
		return new ShuffleLibrary(shuffleThisCard);
	}

	public static Effect searchLibrary(final Selectors select, final boolean reveal, final boolean tapped) {
		return new SearchLibraryEffect(select, Place.BATTLEFIELD, reveal, tapped);
	}

	public static Effect searchLibrary(final Selectors select, final Place place, final boolean reveal) {
		return new SearchLibraryEffect(select, place, reveal, false);
	}

	/**
	 * **************** *** DRAW EFFECT *** *****************
	 */
	public static Effect draw(final int cardCount) {
		return draw(Owner.YOU, cardCount);
	}

	public static Effect draw(final Owner owner, final int cardCount) {
		return new DrawCardEffect(owner, cardCount);
	}

	public static Effect draw(final Owners target, final Nmbs cardCount) {
		return new DrawCardEffect(target, cardCount);
	}

	/**
	 * **************** *** LIFE EFFECT *** *****************
	 */

	/**
	 * **************** *** GAIN LIFE EFFECT *** *****************
	 */
	public static Effect gainLife(final int life) {
		return gainLife(Owner.YOU, life);
	}

	public static Effect gainLife(final Nmb life) {
		return gainLife(Owner.YOU, life);
	}

	public static Effect gainLife(final Owner owner, final int life) {
		return new LifeEffect(owner, life, LifeMode.GAIN);
	}

	public static Effect gainLife(final Owner owner, final Nmb life) {
		return new LifeEffect(owner, life, LifeMode.GAIN);
	}

	public static Effect gainLife(final Owners owner, final Nmbs life) {
		return new LifeEffect(owner, life, LifeMode.GAIN);
	}

	/**
	 * **************** *** LOSE LIFE EFFECT *** *****************
	 */
	public static Effect loseLife(final int life) {
		return loseLife(Owner.YOU, life);
	}

	public static Effect loseLife(final Owner owner, final int life) {
		return new LifeEffect(owner, life, LifeMode.LOSE);
	}

	public static Effect loseLife(final Owner owner, final Nmb life) {
		return new LifeEffect(owner, life, LifeMode.LOSE);
	}

	public static Effect life(final Owners owner, final Nmbs life, final LifeMode mode) {
		return new LifeEffect(owner, life, mode);
	}

	public static Effect setLife(final Nmb life) {
		return new LifeEffect(Owner.YOU, life, LifeMode.SET);
	}

	/**
	 * **************** *** ADD MANA EFFECT *** *****************
	 */
	public static Effect addMana(final MCType... manas) {
		final ManaCost cost = new ManaCost();
		for (final MCType mana : manas)
			cost.addCosts(new Mana(mana));

		return addMana(cost, Owner.YOU);
	}

	public static Effect addMana(final Mana... manas) {
		return addMana(new ManaCost(manas), Owner.YOU);
	}

	public static Effect addMana(final ManaCost cost) {
		return addMana(cost, Owner.YOU);
	}

	public static Effect addMana(final ManaCost mc, final Owner player) {
		return new AddManaEffect(mc, player);
	}

	public static Effect addMana(final ManaCosts mc, final Owners player) {
		return new AddManaEffect(mc, player);
	}

	/**
	 * **************** *** COST EFFECT *** *****************
	 */
	public static Effect cost(final ManaCost manaCost) {
		return cost(new ManaCosts(manaCost), false, null);
	}

	public static Effect cost(final Effect additionalCost) {
		return cost(null, false, additionalCost);
	}

	public static Effect cost(final ManaCosts manaCost, final boolean tap, final Effect additionalCost) {
		return new CostEffect(manaCost, tap, additionalCost);
	}

	public static Effect cost(final ManaCost manaCost, final Effect additionalCost) {
		return new CostEffect(new ManaCosts(manaCost), false, additionalCost);
	}

	private final static Owners fromEffect = new Owners(new OwnerExtractor(OwnerTarget.FROM_EFFECT));

	/**
	 * 
	 * KEYWORD EFFECT
	 * 
	 */
	/**
	 * **************** *** MILL EFFECT *** *****************
	 */
	public static Effect mill(Owner owner, final int nmb) {
		return mill(new Owners(owner), new Nmbs(nmb));
	}

	public static Effect mill(final Owners owner, final Nmbs nmb) {
		final Effect grav = new MoveEffect(null, TargetType.RETURNED_TARGET, Place.GRAVEYARD, fromEffect);
		return new LookAtLibraryEffect(owner, nmb, grav, null, false);
	}

	/**
	 * **************** *** SURVEIL EFFECT *** *****************
	 */
	public static Effect surveil(final int nmb) {
		return surveil(new Nmbs(nmb));
	}

	public static Effect surveil(final Nmbs nmb) {
		final Owners o = new Owners(Owner.YOU);

		// On choisit les cartes à mettre au cimetierre
		final Effect grav = new MoveEffect(new Selectors().upTo().maxTarget(-1), TargetType.RETURNED_TARGET,
				Place.GRAVEYARD, o);

		// Et le reste au dessus de notre bibliothèque dans l'ordre de notre choix
		final Effect onTop = new PutInLibraryEffect(null, TargetType.RETURNED_TARGET, o, true, false, null);

		return new LookAtLibraryEffect(o, nmb, grav, onTop, false);
	}

	/**
	 * **************** *** FATESEAL EFFECT *** *****************
	 */
	public static Effect fateseal(final int nmb) {
		return surveil(new Nmbs(nmb));
	}

	public static Effect fateseal(final Nmbs nmb) {
		final Owners o = new Owners(Owner.OPPONENT);

		// On choisit les cartes à mettre au cimetierre
		final Effect grav = new MoveEffect(new Selectors().upTo().maxTarget(-1), TargetType.RETURNED_TARGET,
				Place.GRAVEYARD, o);

		// Et le reste au dessus de notre bibliothèque dans l'ordre de notre choix
		final Effect onTop = new PutInLibraryEffect(null, TargetType.RETURNED_TARGET, o, true, false, null);

		return new LookAtLibraryEffect(o, nmb, grav, onTop, false);
	}

	/**
	 * **************** *** SCRY EFFECT *** *****************
	 */
	public static Effect scry(final int nmb) {
		return scry(new Nmbs(nmb));
	}

	public static Effect scry(final Nmbs nmb) {
		final Owners o = new Owners(Owner.YOU);

		// On choisit les cartes à mettre en dessous de la bibliothèques
		final Effect onBot = new PutInLibraryEffect(new Selectors().upTo().maxTarget(-1), TargetType.RETURNED_TARGET, o,
				false, true, null);

		// On met le reste au dessus dans l'ordre de notre choix
		final Effect onTop = new PutInLibraryEffect(null, TargetType.RETURNED_TARGET, o, true, false, null);

		return new LookAtLibraryEffect(o, nmb, onBot, onTop, false);
	}

	/**
	 * **************** *** EXPLORE EFFECT *** *****************
	 */
	public static Effect explore() {
		return explore(null, TargetType.THIS_CARD);
	}

	public static Effect explore(final Selectors select, final TargetType target) {
		final Condition isLand = new SelectorCondition(new Selectors(CardT.LAND), TargetType.RETURNED_TARGET);
		final Effect inHand = new MoveEffect(null, TargetType.RETURNED_TARGET, Place.HAND, fromEffect);
		final Effect explore = E.condition(isLand, inHand, E.addCounter(CounterType.PLUS_ONE, 1));

		return new LookAtLibraryEffect(fromEffect, new Nmbs(1), explore, null, true);
	}

	/**
	 * **************** *** SUPPORT EFFECT *** *****************
	 */
	public static Effect support(final int count) {
		return support(new Nmbs(count));
	}

	public static Effect support(final Nmbs nmb) {
		final Selectors select = new Selectors(CardT.CREATURE).excludeThis().setTarget(true).maxTarget(nmb).upTo();

		return E.addCounter(select, TargetType.ALL_CARD, CounterType.PLUS_ONE, 1);
	}

	/**
	 * **************** *** RETURN FROM GRAVEYARD *** *****************
	 */
	public static Effect graveyardTo(final Selectors select, final Place where) {
		return new ReturnFromGraveyardEffect(select, TargetType.ALL_CARD, where);
	}

	public static Effect graveyardTo(final Selectors select, final TargetType targetType, final Place where) {
		return new ReturnFromGraveyardEffect(select, targetType, where);
	}

	/**
	 * **************** *** PUT ON LIBRARY *** *****************
	 */
	public static Effect putInLib(final Selectors select, final TargetType target, final boolean top) {
		return new PutInLibraryEffect(select, target, new Owners(Owner.YOU), top, true, null);
	}

	public static Effect putInLib(final Selectors select, final TargetType target, final Owners o, final boolean top) {
		return new PutInLibraryEffect(select, target, o, top, true, null);
	}

	/**
	 * **************** *** CHOOSE *** *****************
	 */
	public static Effect choose(final Effect... effects) {
		return choose(1, -1, effects);
	}

	public static Effect choose(final int min, final int max, final Effect... effects) {
		return choose(null, min, max, effects);
	}

	public static Effect choose(final Effect afterChoosing, final int min, final int max, final Effect... effects) {
		return new ChooseEffect(afterChoosing, min, max, effects);
	}

	/**
	 * **************** *** CHOOSE OPT *** *****************
	 */
	public static Effect chooseOpt(final String firstOracle, final Effect isFirstChoosed, final String secondOracle,
			final Effect isSecondChoosed) {
		return new ChooseOptEffect(firstOracle, isFirstChoosed, secondOracle, isSecondChoosed);
	}

	/**
	 * **************** *** CONDITION *** *****************
	 */
	public static Effect condition(final Condition condition, final Effect ifTrue) {
		return new ConditionEffect(condition, ifTrue);
	}

	public static Effect condition(final Condition condition, final Effect ifTrue, final Effect ifFalse) {
		return new ConditionEffect(condition, ifTrue, ifFalse);
	}

	/**
	 * **************** *** MAY *** *****************
	 */
	public static Effect may(final Effect mayEffect) {
		return may(mayEffect, null);
	}

	public static Effect may(final Effect mayEffect, final Effect ifEffectDo) {
		return new MayEffect(mayEffect, ifEffectDo);
	}

	public static Effect may(final Effect mayEffect, final Effect ifEffectDo, final Effect ifEffectNotDo) {
		return new MayEffect(mayEffect, ifEffectDo, ifEffectNotDo);
	}

	/**
	 * **************** *** UNTIL *** *****************
	 */
	public static Effect until(final InversableEffect effect) {
		return until(effect, UntilMode.END_OF_TURN);
	}

	public static Effect until(final InversableEffect effect, final UntilMode mode) {
		return new UntilEffect(effect, mode);
	}

	/**
	 * **************** *** ANY *** *****************
	 */
	public static Effects any(final Effect... effects) {
		final Effects e = new Effects();
		return e.addAll(effects);
	}
}
