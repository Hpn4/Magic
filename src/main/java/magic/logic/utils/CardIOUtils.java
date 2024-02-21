package magic.logic.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import magic.logic.card.Card;
import magic.logic.place.Deck;

public abstract class CardIOUtils {

	public static void writeCard(final Card card, final String file) {
		try (final FileOutputStream fos = new FileOutputStream(file);
				final ObjectOutputStream oos = new ObjectOutputStream(fos)) {

			oos.writeObject(card);
			oos.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static Card readCard(final String file) {
		try (final FileInputStream fis = new FileInputStream(file);
				final ObjectInputStream ois = new ObjectInputStream(fis)) {

			final Card card = (Card) ois.readObject();
			
			// On définie la carte parente des capacitiés
			card.getCardAbilities().setup(card);

			ois.close();

			return card;
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static Deck readDeck(final String file) {
		try (final FileInputStream fis = new FileInputStream(file);
				final ObjectInputStream ois = new ObjectInputStream(fis)) {

			final Deck card = (Deck) ois.readObject();

			ois.close();

			return card;
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
