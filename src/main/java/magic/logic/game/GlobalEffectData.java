package magic.logic.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.effect.inversable.InversableEffect;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.InversableEffectData;
import magic.logic.utils.AGMEvent;
import magic.logic.utils.Paint;

public class GlobalEffectData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final boolean isGlobal;

	private long id;

	private final InversableEffectData data;

	private final ArrayList<Targetable> targets;

	private final Card thrower;

	public GlobalEffectData(final InversableEffect effect, final Card thrower, final boolean isGlobal,
			final Targetable[] targetables, final long id) {
		data = new InversableEffectData(effect, effect.getData());
		System.err.println(effect.getData());
		this.thrower = thrower;
		this.isGlobal = isGlobal;
		targets = new ArrayList<Targetable>();
		targets.addAll(List.of(targetables));
		this.id = id;
	}

	/**
	 * Met a jour automatiquement le jeu. Si une carte arrive sur le champs de
	 * bataille, l'effet est appliqué sur elle. Si la carte possedant l'effet global
	 * quitte le champ de bataille, l'effet est annulé pour toute les cibles de sa
	 * capacitié
	 * 
	 * @param part Le dernier element a avoir été mis dans la pile
	 * @param game Le jeu contenant toute les donnés de la partie en cours
	 * 
	 * @return Si l'effet global doit etre supprimé ou non
	 */
	public boolean push(final PilePart part, final Game game) {
		if (part.getEvent() instanceof Event event) {
			if (event == Event.PUT_INTO_GRAVEYARD || event == Event.PUT_INTO_EXILE || event == Event.PUT_INTO_HAND
					|| event == Event.PUT_INTO_LIBRARY) {
				final int id = thrower.getGameID();
				for (final Targetable target : part.getTargets()) {
					System.out.println(Paint.YELLOW + target.getGameID() + " : " + id + Paint.RESET);
					if (target.getGameID() == id) {
						System.out.println(Paint.YELLOW + "SALUT" + Paint.RESET);
						return true;
					}

					// si une des cibles de l'effet globale quitte le champ de bataille, on
					// réinitialise l'effet pour cette carte.
					if (targets.contains(target)) {
						data.effect().invertEffect(game, thrower, new EffectData(data.data(), target));
						targets.remove(target);
					}
				}
			}
		}

		return false;
	}

	public void invert(final Game game) {
		data.effect().invertEffect(game, thrower, new EffectData(data.data(), targets.toArray(Targetable[]::new)));
		System.out.println(Paint.BLUE + "[GLOBAL EFFECT] : inverse stat for " + targets + Paint.RESET);
	}

	public void enterBattlefield(final Game game, final Targetable... targetables) {
		if (isGlobal) {

			// On execute que si c'est pas la carte elle même (sinon double execution)
			if (thrower.getGameID() != targetables[0].getGameID()) {
			//	System.err.println("[GLOBAL EFFECT] : new targets : " + List.of(targetables) + " " + thrower);
				final EffectData d = data.effect().applyEffect(game, thrower, new EffectData(null, targetables));

				final List<Targetable> tar = List.of(d.getTargets());
				System.out.println(Paint.BLUE + "[GLOBAL EFFECT] : new targets : " + tar + Paint.RESET);
				System.out.println(Paint.BLUE + "[GLOBAL EFFECT] : all targets : " + targets + Paint.RESET);

				game.pushPile(thrower, AGMEvent.ADD_TO_GLOBAL, id, d.getTargets());
			}
		}
	}

	public ArrayList<Targetable> getTargets() {
		return targets;
	}

	public long getId() {
		return id;
	}
}
