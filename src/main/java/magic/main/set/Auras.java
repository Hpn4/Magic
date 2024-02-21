package magic.main.set;

import magic.logic.card.CardT;
import magic.logic.card.abilities.SpellAbilities;
import magic.logic.card.abilities.effect.AttachCardEffect;
import magic.logic.card.abilities.utils.Event;
import magic.logic.utils.selector.Selectors;

public class Auras extends AbstractAttachCard {

	public Auras(final AbstractSet set, final String name, final Selectors selector) {
		super(set, name);
		set.addAbility(new SpellAbilities(new AttachCardEffect(selector)));
	}

	public Auras(final AbstractSet set, final String name, final CardT type) {
		this(set, name, new Selectors().cardType(type));
	}

	public void end() {
		end(Event.ENCHANT);
	}
}
