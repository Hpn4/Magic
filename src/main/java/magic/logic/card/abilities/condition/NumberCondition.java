package magic.logic.card.abilities.condition;

import java.io.Serial;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.Utils;
import magic.logic.utils.selector.Operator;
import magic.logic.utils.value.Nmbs;

/**
 * Gere une condition entre deux entier ou deux Nmbs en fonction d'un Operateur
 * de comparaison definissable.
 * 
 * Si l'extracotr firstValue est null alors le test se ferra sur l'entier
 * possiblement retourner par l'effet ou le trigger. Ex : Engagé n'importe
 * quelle nombre de Creature. Si le nombre de creature engagé de cette maniere
 * est inferieur a 3, piochez une carte, sinon defaussez vous d'une carte
 * 
 * @author Hpn4
 *
 */
public class NumberCondition extends Condition {
	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = -2536431376200842925L;

	private final Nmbs firstValue;

	private final Nmbs secondValue;

	private final Operator operator;

	public NumberCondition(final Nmbs firstValue, final Operator operator, final int secondValue) {
		this.firstValue = firstValue;
		this.operator = operator;
		this.secondValue = new Nmbs(secondValue);
	}

	public NumberCondition(final Nmbs firstValue, final Operator operator, final Nmbs secondValue) {
		this.firstValue = firstValue;
		this.operator = operator;
		this.secondValue = secondValue;
	}

	public boolean test(final Game game, final Card thisCard, final EffectData effectData) {
		// Tente d'extraire un nombre de la donnée (soit un nombre, soit le cout
		// convertit de mana ou soit le nombre de marqueur)
		final Object data = Utils.getData(effectData.getData());

		// On fixe une valeur par défaut
		int value = -1;

		// Si l'extractor n'est pas null, on extrait le nombre
		if (firstValue != null)
			value = firstValue.get(game, thisCard, effectData);

		// Sinon on verifie que les données transmises puisent être un nombre et on
		// l'extrait
		else if (data != null && data instanceof Integer i)
			value = i;

		// On recupère la valeur à comparer
		final int compared = secondValue.get(game, thisCard, effectData);

		// On effectue ensuite la condition
		return switch (operator) {
		case EQUAL -> value == compared;
		case GREATER -> value > compared;
		case GREATER_EQUAL -> value >= compared;
		case LESS -> value < compared;
		case LESS_EQUAL -> value <= compared;
		case EVEN -> value % 2 == 0;
		case ODD -> value % 2 == 1;
		};
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(firstValue, operator, secondValue);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj))
			return false;
		if (obj instanceof NumberCondition other)
			return Objects.equals(firstValue, other.firstValue) && operator == other.operator
					&& secondValue == other.secondValue;

		return false;
	}

}
