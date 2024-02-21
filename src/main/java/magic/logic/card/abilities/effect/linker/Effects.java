package magic.logic.card.abilities.effect.linker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Stat;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.condition.Condition;
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
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.ManaCosts;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class Effects implements Effect {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ArrayList<Effect> effects;

	public Effects() {
		effects = new ArrayList<>();
	}

	public Effects(final Effect effect) {
		effects = new ArrayList<>();
		add(effect);
	}

	/**
	 * **************** *** ATTACH CARD EFFECT *** *****************
	 */
	public Effects attachCard(final Selectors select) {
		return add(new AttachCardEffect(select));
	}

	public Effects attachCard(final Selectors select, final TargetType targetType) {
		return add(new AttachCardEffect(select, targetType));
	}

	/**
	 * **************** *** CREATE TOKEN EFFECT *** *****************
	 */
	public Effects createToken(final int count, final Card token) {
		return add(new CreateTokenEffect(count, token));
	}

	public Effects createToken(final int count, final Card token, final Owners who) {
		return add(new CreateTokenEffect(new Nmbs(count), token, who, token.getDefaultStat(), null));
	}

	public Effects createToken(final int count, final Card token, final Nmb stat) {
		return add(new CreateTokenEffect(count, token, stat));
	}

	public Effects createToken(final Nmbs count, final Card token, final Stat stat) {
		return add(new CreateTokenEffect(count, token, new Owners(Owner.YOU), stat, null));
	}

	/**
	 * **************** *** COUNTER EFFECT *** *****************
	 */

	/**
	 * **************** *** ADD COUNTER EFFECT *** *****************
	 */
	public Effects addCounter(final CounterType type, final int count) {
		return add(new CounterEffect(null, TargetType.THIS_CARD, type, count, true));
	}

	public Effects addCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final int count) {
		return add(new CounterEffect(target, targetType, type, count, true));
	}

	public Effects addCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final Nmbs count) {
		return add(new CounterEffect(target, targetType, type, count, true));
	}

	/**
	 * **************** *** REMOVE COUNTER EFFECT *** *****************
	 */
	public Effects removeCounter(final CounterType type, final int count) {
		return add(new CounterEffect(null, TargetType.THIS_CARD, type, count, false));
	}

	public Effects removeCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final int count) {
		return add(new CounterEffect(target, targetType, type, count, false));
	}

	public Effects removeCounter(final Selectors target, final TargetType targetType, final CounterType type,
			final Nmbs count) {
		return add(new CounterEffect(target, targetType, type, count, false));
	}

	/**
	 * **************** *** DEAL DAMAGE EFFECT *** *****************
	 */
	public Effects damage(final Selectors selector, final TargetType targetType, final int count) {
		return add(new DealsDamageEffect(selector, targetType, count));
	}

	public Effects damage(final Selectors selector, final TargetType targetType, final Nmbs count) {
		return add(new DealsDamageEffect(selector, targetType, count));
	}

	/**
	 * **************** *** DESTROY EFFECT *** *****************
	 */
	public Effects destroy(final Selectors selector, final TargetType targetType) {
		return add(new DestroyEffect(selector, targetType));
	}

	/**
	 * **************** *** EXILE EFFECT *** *****************
	 */
	public Effects exile(final Selectors selector, final TargetType targetType) {
		return add(new ExileEffect(selector, targetType));
	}

	/**
	 * **************** *** TAP EFFECT *** *****************
	 */
	public Effects tap(final Selectors selector, final TargetType targetType) {
		return add(new TapEffect(selector, targetType, true));
	}

	public Effects untap(final Selectors selector, final TargetType targetType) {
		return add(new TapEffect(selector, targetType, false));
	}

	/**
	 * **************** *** MODIFY STAT EFFECT *** *****************
	 */
	public Effects get(final Selectors selector, final TargetType targetType, final Stat stat) {
		return add(new ModifyStatEffect(selector, targetType, stat));
	}

	public Effects get(final Selectors selector, final TargetType targetType, final Nmb stat) {
		return get(selector, targetType, new Nmbs(stat), new Nmbs(stat));
	}

	public Effects get(final Selectors selector, final TargetType targetType, final Nmbs power, final Nmbs toughness) {
		return add(new ModifyStatEffect(selector, targetType, power, toughness));
	}

	/**
	 * **************** *** ADD CAPACITIES EFFECT *** *****************
	 */
	public Effects gains(final Selectors select, final TargetType targetType, final Capacities... capacities) {
		return add(new AddCapacitiesEffect(select, targetType, capacities));
	}

	/**
	 * **************** *** ADD ABILITY EFFECT *** *****************
	 */
	public Effects have(final Selectors select, final TargetType targetType, final Abilities ability) {
		return add(new AddAbilitiesEffect(select, targetType, ability));
	}

	/**
	 * **************** *** ADD CARD TYPE EFFECT *** *****************
	 */
	public Effects is(final Selectors select, final TargetType targetType, final CardT... cardTypes) {
		return add(new AddCardTypeEffect(select, targetType, cardTypes));
	}

	/**
	 * **************** *** LIBRARY EFFECT *** *****************
	 */
	public Effects shuffle() {
		return shuffle(false);
	}

	public Effects shuffle(final boolean shuffleThisCard) {
		return add(new ShuffleLibrary(shuffleThisCard));
	}

	public Effects searchLibrary(final Selectors select, final boolean reveal, final boolean tapped) {
		return add(new SearchLibraryEffect(select, Place.BATTLEFIELD, reveal, tapped));
	}

	public Effects searchLibrary(final Selectors select, final Place place, final boolean reveal) {
		return add(new SearchLibraryEffect(select, place, reveal, false));
	}

	/**
	 * **************** *** DRAW EFFECT *** *****************
	 */
	public Effects draw(final int cardCount) {
		return draw(Owner.YOU, cardCount);
	}

	public Effects draw(final Owner owner, final int cardCount) {
		return add(new DrawCardEffect(owner, cardCount));
	}

	public Effects draw(final Owners target, final Nmbs cardCount) {
		return add(new DrawCardEffect(target, cardCount));
	}

	/**
	 * **************** *** SCRY EFFECT *** *****************
	 */
	public Effects scry(final int count) {
		return add(E.scry(count));
	}

	public Effects scry(final Nmbs count) {
		return add(E.scry(count));
	}

	/**
	 * **************** *** SURVEIL EFFECT *** *****************
	 */
	public Effects surveil(final int count) {
		return add(E.surveil(count));
	}

	public Effects surveil(final Nmbs count) {
		return add(E.surveil(count));
	}

	/**
	 * **************** *** LIFE EFFECT *** *****************
	 */

	/**
	 * **************** *** GAIN LIFE EFFECT *** *****************
	 */
	public Effects gainLife(final int life) {
		return gainLife(Owner.YOU, life);
	}

	public Effects gainLife(final Owner owner, final int life) {
		return add(new LifeEffect(owner, life, LifeMode.GAIN));
	}

	public Effects gainLife(final Owner owner, final Nmb life) {
		return add(new LifeEffect(owner, life, LifeMode.GAIN));
	}

	/**
	 * **************** *** LOSE LIFE EFFECT *** *****************
	 */
	public Effects loseLife(final int life) {
		return loseLife(Owner.YOU, life);
	}

	public Effects loseLife(final Owner owner, final int life) {
		return add(new LifeEffect(owner, life, LifeMode.LOSE));
	}

	public Effects loseLife(final Owner owner, final Nmb life) {
		return add(new LifeEffect(owner, life, LifeMode.LOSE));
	}

	public Effects life(final Owners owner, final Nmbs life, final LifeMode mode) {
		return add(new LifeEffect(owner, life, mode));
	}

	/**
	 * **************** *** ADD MANA EFFECT *** *****************
	 */
	public Effects addMana(final ManaCost cost) {
		return addMana(cost, Owner.YOU);
	}

	public Effects addMana(final ManaCost mc, final Owner player) {
		return add(new AddManaEffect(mc, player));
	}

	public Effects addMana(final ManaCosts mc, final Owners player) {
		return add(new AddManaEffect(mc, player));
	}

	/**
	 * **************** *** COST EFFECT *** *****************
	 */
	public Effects cost(final ManaCost manaCost) {
		return cost(new ManaCosts(manaCost), false, null);
	}

	public Effects cost(final Effect additionalCost) {
		return cost(null, false, additionalCost);
	}

	public Effects cost(final ManaCosts manaCost, final boolean tap, final Effect additionalCost) {
		return add(new CostEffect(manaCost, tap, additionalCost));
	}

	/**
	 * **************** *** LINKER METHOD *** *****************
	 */
	public Effects add(final Effect effect) {
		effects.add(effect);
		return this;
	}
	
	public Effects addAll(final Effect... effect) {
		effects.addAll(List.of(effect));
		return this;
	}

	/**
	 * **************** *** CHOOSE *** *****************
	 */
	public Effects choose(final Effect... effects) {
		return choose(1, -1, effects);
	}

	public Effects choose(final int min, final int max, final Effect... effects) {
		return choose(null, min, max, effects);
	}

	public Effects choose(final Effect afterChoosing, final int min, final int max, final Effect... effects) {
		return add(new ChooseEffect(afterChoosing, min, max, effects));
	}

	/**
	 * **************** *** CONDITION *** *****************
	 */
	public Effects condition(final Condition condition, final Effect ifTrue) {
		return add(new ConditionEffect(condition, ifTrue));
	}

	public Effects condition(final Condition condition, final Effect ifTrue, final Effect ifFalse) {
		return add(new ConditionEffect(condition, ifTrue, ifFalse));
	}

	/**
	 * **************** *** MAY *** *****************
	 */
	public Effects may(final Effect mayEffect) {
		return may(mayEffect, null);
	}

	public Effects may(final Effect mayEffect, final Effect ifEffectDo) {
		return add(new MayEffect(mayEffect, ifEffectDo));
	}

	public Effects may(final Effect mayEffect, final Effect ifEffectDo, final Effect ifEffectNotDo) {
		return add(new MayEffect(mayEffect, ifEffectDo, ifEffectNotDo));
	}

	/**
	 * **************** *** UNTIL *** *****************
	 */
	public Effects until(final InversableEffect effect) {
		return until(effect, UntilMode.END_OF_TURN);
	}

	public Effects until(final InversableEffect effect, final UntilMode mode) {
		return add(new UntilEffect(effect, mode));
	}

	@Override
	public void setup(final Game game, final Card thisCard, final Abilities a) {
		for (final Effect effect : effects)
			effect.setup(game, thisCard, a);
	}

	@Override
	public boolean canBeExecuted(final Game game, final Card thisCard, final Object object) {
		for (final Effect effect : effects)
			if (!effect.canBeExecuted(game, thisCard, object))
				return false;

		return true;
	}

	@Override
	public EffectData applyEffect(final Game game, final Card thisCard, final EffectData effectData) {
		EffectData data = effectData;
		for (final Effect effect : effects)
			data = effect.applyEffect(game, thisCard, data);

		return data;
	}

	@Override
	public void cleanup(final Game game, final Card thisCard) {
		for (final Effect effect : effects)
			effect.cleanup(game, thisCard);
	}

	@Override
	public boolean isManaEffect() {
		for (final Effect effect : effects)
			if (effect.isManaEffect())
				return true;

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(effects);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Effects other)
			Objects.equals(effects, other.effects);

		return false;
	}

}
