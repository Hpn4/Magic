package magic.main.set;

import magic.logic.card.CardT;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.effect.AttachCardEffect;
import magic.logic.card.abilities.effect.CostEffect;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.utils.selector.Selectors;

public class Equipment extends AbstractAttachCard {

	private static final Selectors creature = new Selectors().cardType(CardT.CREATURE);

	private static final AttachCardEffect attach = new AttachCardEffect(creature);

	public Equipment(final AbstractSet set, final String name) {
		super(set, name);
	}

	public void equip(final int generic) {
		equip(new ManaCost(new Mana(MCType.GENERIC, generic)));
	}

	public void equip(final ManaCost cost) {
		final ActivatedAbilities activated = ActivatedAbilities.cost(cost, attach);

		set.addAbility(activated);
	}

	public void equip(final CostEffect cost) {
		final ActivatedAbilities activated = new ActivatedAbilities(cost, attach);

		set.addAbility(activated);
	}

	public void end() {
		end(Event.EQUIP);
	}
}
