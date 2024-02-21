package magic.logic.utils;

import java.util.Objects;
import java.util.Random;

import magic.logic.card.Card;
import magic.logic.card.Counter;
import magic.logic.card.Targetable;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.ManaCost;
import magic.logic.utils.extractor.number.NumberType;

public abstract class Utils {

	private final static Random rand = new Random();

	public static MCType getMana(final String str) {
		return switch (str) {
		case "W" -> MCType.WHITE;
		case "U" -> MCType.BLUE;
		case "B" -> MCType.DARK;
		case "R" -> MCType.RED;
		case "G" -> MCType.GREEN;
		case "C" -> MCType.COLORLESS;
		case "S" -> MCType.SNOW;
		case "X" -> MCType.X;
		default -> MCType.GENERIC;
		};
	}

	public static String numberToWord(final int number) {
		return switch (number) {
		case 0 -> "Zero";
		case 1 -> "One";
		case 2 -> "Two";
		case 3 -> "Three";
		case 4 -> "Four";
		case 5 -> "Five";
		case 6 -> "Six";
		case 7 -> "Seven";
		case 8 -> "Height";
		case 9 -> "Nine";
		case 10 -> "Ten";
		default -> "" + number;
		};
	}

	public static MCType getMana(final char str) {
		return getMana("" + str);
	}

	public static int getNumber(final Targetable target, final NumberType type) {
		if (target instanceof Card card) {
			return switch (type) {
			case CCM -> card.getCardCost().getCCM();
			case POWER -> card.getPower();
			case TOUGHNESS -> card.getToughness();
			case COLORS -> card.getColorIdentity().size();
			};
		}

		return 0;
	}

	public static int countOccurence(final String str, final char seq) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == seq)
				count++;
		}

		return count;
	}

	public static <E> boolean contains(final E[] tab, final E test) {
		if (tab != null)
			for (final E element : tab)
				if (Objects.equals(element, test))
					return true;
		return false;
	}

	public static Object getData(final Object data) {
		if (data == null)
			return data;

		if (data instanceof Integer i)
			return i;
		if (data instanceof Counter count)
			return count.getCount();
		if (data instanceof ManaCost mc)
			return mc.getCCM();

		return data;
	}

	public static <E> E[] shuffle(final E[] toShuffle) {
		final int size = toShuffle.length;

		for (int i = 0; i < size; i++) {
			final int randIndex = rand.nextInt(size);
			final E temp = toShuffle[randIndex];
			toShuffle[randIndex] = toShuffle[i];
			toShuffle[i] = temp;
		}

		return toShuffle;
	}
}
