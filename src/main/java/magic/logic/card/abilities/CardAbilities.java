package magic.logic.card.abilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.statics.StaticAbilities;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.InversableEffectData;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.selector.Selectors;

public class CardAbilities implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7833196004952716795L;

	private ArrayList<SpellAbilities> spells;

	private ArrayList<TriggeredAbilities> triggered;

	private ArrayList<ActivatedAbilities> activated;

	private ArrayList<StaticAbilities> statics;

	private final ArrayList<Capacities> capacities;

	private final ArrayList<InversableEffectData> endTurn;

	private final ArrayList<Cant> cant;

	public CardAbilities() {
		endTurn = new ArrayList<>();
		capacities = new ArrayList<>();
		cant = new ArrayList<>();
	}

	public CardAbilities(final CardAbilities toCopy) {
		endTurn = new ArrayList<>(toCopy.getEndTurn());
		capacities = new ArrayList<>(toCopy.capacities);
		cant = new ArrayList<>(toCopy.cant);

		if (toCopy.spells != null)
			spells = new ArrayList<>(toCopy.spells);

		if (toCopy.triggered != null)
			triggered = new ArrayList<>(toCopy.triggered);

		if (toCopy.activated != null)
			activated = new ArrayList<>(toCopy.activated);

		if (toCopy.statics != null)
			statics = new ArrayList<>(toCopy.statics);
	}

	public ArrayList<SpellAbilities> getSpells() {
		return spells;
	}

	public void setSpells(final ArrayList<SpellAbilities> spells) {
		this.spells = spells;
	}

	public ArrayList<TriggeredAbilities> getTriggered() {
		return triggered;
	}

	public void setTriggered(final ArrayList<TriggeredAbilities> triggered) {
		this.triggered = triggered;
	}

	public ArrayList<ActivatedAbilities> getActivated() {
		return activated;
	}

	public void setActivated(final ArrayList<ActivatedAbilities> activated) {
		this.activated = activated;
	}

	public ArrayList<StaticAbilities> getStatics() {
		return statics;
	}

	public void setStatics(final ArrayList<StaticAbilities> statics) {
		this.statics = statics;
	}

	public ArrayList<Capacities> getCapacities() {
		return capacities;
	}

	public ArrayList<InversableEffectData> getEndTurn() {
		return endTurn;
	}

	public void addSpellAbility(final SpellAbilities spell) {
		if (spells == null)
			spells = new ArrayList<>();

		spells.add(spell);
	}

	public void addTriggeredAbility(final TriggeredAbilities trigger) {
		if (triggered == null)
			triggered = new ArrayList<>();

		triggered.add(trigger);
	}

	public void addActivatedAbility(final ActivatedAbilities active) {
		if (activated == null)
			activated = new ArrayList<>();

		activated.add(active);
	}

	public void addStaticAbility(final StaticAbilities staticAbility) {
		if (statics == null)
			statics = new ArrayList<>();

		statics.add(staticAbility);
	}

	public void addAbilitiy(final Abilities ability) {
		if (ability instanceof SpellAbilities spell)
			addSpellAbility(spell);
		else if (ability instanceof TriggeredAbilities trigger)
			addTriggeredAbility(trigger);
		else if (ability instanceof ActivatedAbilities active)
			addActivatedAbility(active);
		else if (ability instanceof StaticAbilities staticAbility)
			addStaticAbility(staticAbility);
	}

	public void removeAbility(final Abilities ability) {
		if (ability instanceof SpellAbilities spell)
			spells.remove(spell);
		else if (ability instanceof TriggeredAbilities trigger)
			triggered.remove(trigger);
		else if (ability instanceof ActivatedAbilities active)
			activated.remove(active);
		else if (ability instanceof StaticAbilities staticAbility)
			statics.remove(staticAbility);
	}

	public void addCapacity(final Capacities capacity) {
		capacities.add(capacity);
	}

	public void addEndTurn(final InversableEffect effect) {
		endTurn.add(new InversableEffectData(effect, effect.getData()));
	}

	public void addInterdiction(final Cant cant) {
		this.cant.add(cant);
	}

	/**
	 * Test si la carte peut faire l'action ou est protege. </br>
	 * Ex : "ne peux pas être la cible d'un sort rouge". </br>
	 * Ex : "ne peut pas être bloquée par des créatures verte"
	 * 
	 * @param game         Le jeu
	 * @param thisCard     La carte possedant les protection / interdiction
	 * @param interdiction L'interdiction à tester
	 * @param targets      Les différentes cibles à tester
	 * 
	 * @return
	 */
	public boolean can(final Game game, final Card thisCard, final Interdiction interdiction,
			final Targetable... targets) {
		if (interdiction == Interdiction.BE_THE_TARGET) {
			if (haveCapacity(Capacities.HEXPROOF)) {
				if (new Selectors().controller(Owner.OPPONENT).match(game, thisCard, targets).size() > 0)
					return false;
			} else if (haveCapacity(Capacities.SHROUD))
				return false;
		}
		for (final Cant c : cant)
			if (c.getInterdiction() == interdiction) {
				if (c.getSelect() != null && c.getSelect().match(game, thisCard, targets).size() > 0)
					return false;
				if (c.getCondition() != null && c.getCondition().test(game, thisCard, targets))
					return false;
				if (c.getSelect() == null && c.getCondition() == null)
					return false;
			}

		return true;
	}

	public void endTurn(final Game game, final Card thisCard) {
		// Faire que sa se finit au bon tour
		for (final InversableEffectData effect : endTurn)
			effect.effect().invertEffect(game, thisCard, new EffectData(effect.data(), thisCard));

		for (final ActivatedAbilities activate : activated)
			activate.endTurn();

		endTurn.clear();
	}

	public boolean haveCapacity(final Capacities capacity) {
		return capacities.contains(capacity);
	}

	public void setup(final Card parent) {
		if (spells != null)
			for (final SpellAbilities spell : spells)
				spell.setParent(parent);

		if (activated != null)
			for (final ActivatedAbilities ab : activated)
				ab.setParent(parent);

		if (triggered != null)
			for (final Abilities ab : triggered)
				ab.setParent(parent);

		if (statics != null)
			for (final Abilities ab : statics)
				ab.setParent(parent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(activated, capacities, endTurn, spells, statics, triggered);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CardAbilities other)
			return Objects.equals(activated, other.activated) && Objects.equals(capacities, other.capacities)
					&& Objects.equals(endTurn, other.endTurn) && Objects.equals(spells, other.spells)
					&& Objects.equals(statics, other.statics) && Objects.equals(triggered, other.triggered);

		return false;
	}

}
