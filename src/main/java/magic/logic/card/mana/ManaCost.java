package magic.logic.card.mana;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.game.Game;
import magic.logic.utils.Counterable;

public class ManaCost implements Comparable<ManaCost>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4254649283772375524L;

	private final List<AbstractMana> costs;

	public ManaCost(final AbstractMana... abstractCosts) {
		costs = new ArrayList<>();
		addCosts(abstractCosts);
	}

	public ManaCost(final ManaCost manaCost) {
		if (manaCost != null) {
			costs = new ArrayList<>();
			for (final AbstractMana ac : manaCost.costs) {
				if (ac instanceof Mana m)
					costs.add(new Mana(m.getMana(), m.getNumber()));
				else if (ac instanceof DoubleMana dm)
					costs.add(new DoubleMana(dm.getMana(), dm.getSecondMana()));
				else
					costs.add(new Phyrixian(ac.getMana()));
			}
		} else
			costs = new ArrayList<>();
	}

	/**
	 * Return the difference between this ManaCost and {@code mc}.
	 * 
	 * @param mc
	 * @return
	 */
	public ManaCost pay(final ManaCost mc) {
		final ArrayList<AbstractMana> complexMana = new ArrayList<>();
		final EnumMap<MCType, Integer> mana = new EnumMap<>(MCType.class);

		for (final AbstractMana cost : costs)
			if (cost instanceof Mana m)
				mana.merge(m.getMana(), m.getNumber(), (i, j) -> i + j);
			else
				complexMana.add(cost);

		final ArrayList<AbstractMana> complexManaDst = new ArrayList<>();
		final EnumMap<MCType, Integer> manaDst = new EnumMap<>(MCType.class);

		for (final AbstractMana cost : mc.getCosts())
			if (cost instanceof Mana m)
				manaDst.merge(m.getMana(), m.getNumber(), (i, j) -> i + j);
			else
				complexManaDst.add(cost);

		for (final MCType type : MCType.values()) {
			final Integer a = mana.get(type), b = manaDst.get(type);
			final int finalCost = a == null ? b == null ? 0 : -((Integer) b)
					: b == null ? (Integer) a : (Integer) a - (Integer) b;
			mana.put(type, finalCost);
		}

		for (int i = 0; i < complexMana.size(); i++) {
			final AbstractMana cost = complexMana.get(i);
			if (cost instanceof DoubleMana m) {
				boolean match = false;
				for (final Entry<MCType, Integer> set : mana.entrySet()) {
					final MCType key = set.getKey();
					if (m.match(key) && set.getValue() < 0) {
						mana.put(key, set.getValue() + 1);
						complexMana.remove(cost);
						match = true;
					}
				}

				if (!match) {
					for (int j = 0; j < complexManaDst.size(); j++) {
						final AbstractMana costDst = complexManaDst.get(j);
						if (costDst instanceof DoubleMana mDst) {
							if (m.match(mDst)) {
								match = true;
								complexMana.remove(cost);
								complexManaDst.remove(costDst);
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < complexManaDst.size(); i++) {
			final AbstractMana cost = complexManaDst.get(i);
			if (cost instanceof DoubleMana m) {
				for (Entry<MCType, Integer> set : mana.entrySet()) {
					final MCType key = set.getKey();
					if (m.match(key) && mana.get(key) > 0) {
						mana.put(key, set.getValue() - 1);
						complexManaDst.remove(cost);
					}
				}
			}
		}

		if (mana.containsKey(MCType.GENERIC)) {
			int any = mana.get(MCType.GENERIC);
			if (any < 0) {
				for (final Entry<MCType, Integer> set : mana.entrySet()) {
					final MCType key = set.getKey();
					final int value = set.getValue();
					if (value > 0) {
						if (value > Math.abs(any)) {
							mana.put(key, value - Math.abs(any));
							any = 0;
						} else {
							any += value;
							mana.put(key, 0);
						}
					}
					if (any == 0) {
						mana.put(MCType.GENERIC, 0);
						break;
					}
				}
			}
		}

		final ManaCost manas = new ManaCost();
		for (final Entry<MCType, Integer> entry : mana.entrySet()) {
			final int value = entry.getValue();
			if (value != 0)
				manas.addCosts(new Mana(entry.getKey(), value));
		}

		return manas;
	}

	/**
	 * Modifie ce mana cost. Ce mana cost ne doit contenir que des mana basiques
	 * 
	 * @param cost     Le cout � payer
	 * @param thisCard La carte qui n�cessite d etre payer ou qui possede la
	 *                 capacitie a payer
	 * @param target   L'objet a payer
	 * @param game     Le jeu
	 * 
	 * @return Si le cout {@code cost} peut bien etre pay�
	 */
	@SuppressWarnings("null") // Bug du compiler, il consid�re second comme �tant forcement null
	public ManaCost payBasic(final ManaCost cost, final Card thisCard, final Targetable target, final Game game) {
		final EnumMap<MCType, Integer> mana = new EnumMap<>(MCType.class);
		final ArrayList<DoubleMana> complexMana = new ArrayList<>();
		final ArrayList<Phyrixian> phy = new ArrayList<>();
		final ManaCost payed = new ManaCost();

		for (final AbstractMana c : cost.getListOfCosts())
			if (c instanceof Mana m)
				mana.merge(m.getMana(), m.getNumber(), (i, j) -> i + j);
			else if (c instanceof DoubleMana dm)
				complexMana.add(dm);
			else if (c instanceof Phyrixian ph)
				phy.add(ph);

		// On vire les manas neigeux
		Integer snow = mana.get(MCType.SNOW);
		if (snow != null) {
			for (int i = 0; i < costs.size();)
				if (costs.get(i) instanceof Mana m && m.isSnowMana()) { // Si c'est un mana neigeux
					snow -= m.getNumber();

					if (snow < 0) { // Il y a plus de mana dans la reserve que dans le cout
						payed.addCosts(new Mana(m.getMana(), snow + m.getNumber(), true));
						m.setNumber(-snow); // On remet du mana
						i++;
						break;
					} else {
						payed.addCosts(new Mana(m)); // On supprime de la liste de ce mana cost
						costs.remove(i);
					}
				}

			// Il reste des mana neigeux
			if (snow > 0)
				return null;
		}

		// On vire tous les mana simple
		for (final AbstractMana am : costs)
			if (am instanceof Mana m) {
				final Integer nmb = mana.get(m.getMana());

				boolean can = true;
				if (m.getRestriction() != null)
					can = m.getRestriction().match(game, thisCard, thisCard); // Spend this mana only to cast/activate

				if (nmb != null && nmb > 0 && can) {
					final int tmp = nmb - m.getNumber();
					if (tmp <= 0) {
						m.setNumber(-tmp);
						mana.put(m.getMana(), 0);

						payed.addCosts(new Mana(m.getMana(), nmb)); // On a retire le bon nombre
					} else {
						mana.put(m.getMana(), tmp);
						m.setNumber(0);

						payed.addCosts(new Mana(m.getMana())); // On a retire tout les manas
					}
				}
			}

		// Das manas simples ont pas pu etre paye car pas le mana pour
		for (final Entry<MCType, Integer> entry : mana.entrySet())
			if (entry.getValue() > 0 && entry.getKey() != MCType.GENERIC && entry.getKey() != MCType.X)
				return null;

		// On enregistre les valeurs et on en profite pour supprimer les mana à zéro
		for (int i = 0; i < costs.size();)
			if (costs.get(i) instanceof Mana m) {
				if (m.getNumber() == 0)
					costs.remove(i);
				else
					i++;
			}

		// Tant qu'on a pas traite tout les doubles mana
		boolean canDeleteDouble = false;
		int nmbSimple;
		while (complexMana.size() > 0) {
			nmbSimple = 0;
			for (int i = 0; i < complexMana.size(); i++) {
				final DoubleMana da = complexMana.get(i);

				Mana first = null;
				Mana second = null;
				for (final AbstractMana am : costs)
					if (am instanceof Mana m) {
						if (first == null && da.getMana() == m.getMana()) {
							boolean canAdd = true;

							if (m.getRestriction() != null)
								canAdd = m.getRestriction().match(game, thisCard, target);

							if (canAdd) {
								first = m;
								continue; // Pour eviter de faire une condition inutile
							}
						}

						if (second == null && da.getSecondMana() == m.getMana()) {
							boolean canAdd = true;

							if (m.getRestriction() != null)
								canAdd = m.getRestriction().match(game, thisCard, target);

							if (canAdd)
								second = m;
						}
					}

				// On a pas les couleurs donc c'est pas bon.
				if (first == null && second == null)
					return null;

				// On a plus les deux couleurs de mana
				if (first.getNumber() == 0 && second.getNumber() == 0)
					return null;

				if (first != null && second != null) {
					if (canDeleteDouble) { // Par d�faut on vire le premier
						first.setNumber(first.getNumber() - 1);
						complexMana.remove(i);
						payed.addCosts(new Mana(first.getMana(), 1, first.getData()));
					}

					continue;
				}

				if (first.getNumber() > 0) {
					first.setNumber(first.getNumber() - 1);
					complexMana.remove(i);
					nmbSimple++;
					payed.addCosts(new Mana(first.getMana(), 1, first.getData()));
				}

				if (second.getNumber() > 0) {
					second.setNumber(second.getNumber() - 1);
					complexMana.remove(i);
					nmbSimple++;
					payed.addCosts(new Mana(second.getMana(), 1, second.getData()));
				}
			}

			// Une fois que tout les doubles mana ou une seule couleur correspond on ete
			// trait�. On passe � ceux ou on possede les deux couleurs dans la reserve.
			if (nmbSimple == 0)
				canDeleteDouble = true;
		}

		// On s'occupe des mana phyrexien
		int life = 0;
		for (final Phyrixian ph : phy) {
			Mana pay = null;
			int index = 0;

			for (int i = 0; i < costs.size(); i++)
				if (costs.get(i) instanceof Mana m && m.getMana() == ph.getMana()) {

					boolean canAdd = true;
					if (m.getRestriction() != null)
						canAdd = m.getRestriction().match(game, thisCard, target);

					if (canAdd) {
						pay = m;
						index = i;
						break;
					}
				}

			if (pay == null)
				life += 2;
			else {
				final int tmp = pay.getNumber() - 1;
				if (tmp == 0)
					costs.remove(index);
				else
					pay.setNumber(tmp);
				payed.addCosts(new Mana(pay.getMana(), 1, pay.getData()));
			}
		}

		if (life > 0)
			System.out.println("Vous devez payer : " + life + " point de vie");

		// On s'occupe des mana generic/X
		Integer gene = mana.get(MCType.GENERIC);
		if (gene != null) {
			for (int i = 0; i < costs.size();) {
				if (costs.get(i) instanceof Mana m) {
					boolean canPay = true;
					if (m.getRestriction() != null)
						canPay = m.getRestriction().match(game, thisCard, target);

					if (canPay) {
						gene -= m.getNumber();

						if (gene < 0) { // Il y a plus de mana dans la reserve que dans le cout
							payed.addCosts(new Mana(m.getMana(), gene + m.getNumber(), m.getData()));
							m.setNumber(-gene); // On remet du mana
							i++;
							break;
						} else {
							payed.addCosts(new Mana(m));
							costs.remove(i); // On supprime de la liste de ce mana cost
						}
					} else
						i++;
				}
			}

			// On a pas pay� tout les couts
			if (gene > 0)
				return null;
		}

		return payed;
	}

	public void setup(final Game game, final Counterable target) {
		int x = 0;

		// On parcours tout les couts
		for (int i = 0; i < costs.size(); i++)
			// Si c'est un mana X
			if (costs.get(i) instanceof Mana m && m.getMana() == MCType.X) {
				// On demande a l'utilisateur de saisir le mana, on le supprime de la liste
				final int v = game.chooseX(target);
				x = v * m.getNumber(); // Il peut y avoir plusieurs X
				m.setNumber(v); // On sauvegarde le nouveau X
				break;
			}

		if (x != 0) {
			// On met à jour le mana générique
			for (int i = 0; i < costs.size(); i++)
				if (costs.get(i) instanceof Mana m && m.getMana() == MCType.GENERIC)
					m.setNumber(m.getNumber() + x);
		}
	}

	public List<AbstractMana> getListOfCosts() {
		return costs;
	}

	public AbstractMana[] getCosts() {
		return costs.toArray(AbstractMana[]::new);
	}

	public void setCosts(final AbstractMana... manas) {
		costs.clear();
		costs.addAll(Arrays.asList(manas));
	}

	public void setCosts(final ManaCost mc) {
		costs.clear();
		costs.addAll(mc.getListOfCosts());
	}

	public void addCosts(final AbstractMana... manas) {
		costs.addAll(Arrays.asList(manas));
	}

	public void addCost(final AbstractMana mana, final int index) {
		costs.add(index, mana);
	}

	public void addCosts(final ManaCost cost) {
		costs.addAll(cost.getListOfCosts());
	}

	public int getCCM() {
		int ccm = 0;
		for (final AbstractMana cost : costs)
			if (cost instanceof Mana m)
				ccm += m.getNumber();
			else if (cost instanceof DoubleMana)
				ccm++;

		return ccm;
	}

	public boolean isEmpty() {
		for (final AbstractMana cost : costs)
			if (cost instanceof Mana m && m.getNumber() != 0)
				return false;
			else if (cost instanceof DoubleMana)
				return false;

		return true;
	}

	@Override
	public String toString() {
		String out = "";
		for (final AbstractMana cost : costs)
			out += cost.toString() + ", ";

		return out.length() > 2 ? out.substring(0, out.length() - 2) : "";
	}

	@Override
	public int hashCode() {
		return 31 + costs.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final ManaCost other = (ManaCost) obj;
		return costs.equals(other.costs);
	}

	@Override
	public int compareTo(final ManaCost mc) {
		final AbstractMana[] costsO = mc.getCosts();
		final int size = costsO.length > costs.size() ? costs.size() : costsO.length;

		int comp = 0;
		for (int i = 0; i < size; i++)
			if ((comp = costs.get(i).compareTo(costsO[i])) != 0)
				return comp;

		return Integer.compare(costs.size(), costsO.length);
	}

	public int getCost(final MCType color) {
		for (final AbstractMana cost : costs)
			if (cost.getMana() == color && cost instanceof Mana m)
				return m.getNumber();

		return 0;
	}
}
