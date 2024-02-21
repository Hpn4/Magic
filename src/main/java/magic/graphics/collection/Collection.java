package magic.graphics.collection;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import magic.graphics.card.CardUIFX;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Rarity;
import magic.logic.card.Targetable;
import magic.logic.place.Deck;
import magic.logic.utils.CardIOUtils;
import magic.logic.utils.selector.Operator;
import magic.logic.utils.selector.Selectors;

public class Collection extends BorderPane {

	private final ArrayList<Card> cards;

	private final GridPane grid;

	private final DeckPane deckPane;

	public Collection() {
		deckPane = new DeckPane();
		cards = new ArrayList<>();

		grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(5);
		grid.setVgap(5);

		final ScrollPane pane = new ScrollPane();
		pane.setContent(grid);

		final Deck m20 = CardIOUtils.readDeck("resources/m20.deck");
		cards.addAll(m20.getAllCards());

		setup(cards);

		final TextField search = new TextField("");
		search.setPromptText("Search here...");

		final Button execute = new Button("Search");
		execute.setOnAction(e -> {
			final Selectors select = new Selectors();

			// On séprare par espace
			final String[] parts = search.getText().split(" ");

			for (String part : parts) {
				if (part.startsWith("-")) {
					select.not();
					part = part.substring(1);
				}

				final int index = part.indexOf(":");
				if (index != -1) {
					final String key = part.substring(0, index);
					String behind = part.substring(index + 1);

					// La rarete
					if (key.equalsIgnoreCase("r") || key.equalsIgnoreCase("rarity")) {
						Rarity r = Rarity.COMMON;
						if (behind.equalsIgnoreCase("r") || behind.equalsIgnoreCase("rare"))
							r = Rarity.RARE;
						else if (behind.equalsIgnoreCase("u") || behind.equalsIgnoreCase("unco"))
							r = Rarity.UNCO;
						else if (behind.equalsIgnoreCase("m") || behind.equalsIgnoreCase("mythic"))
							r = Rarity.MYTHIC;

						select.rarity(r);
						continue;
					}

					if (key.equalsIgnoreCase("o") || key.equalsIgnoreCase("oracle"))
						select.oracle(behind, true);

					if (key.equalsIgnoreCase("t") || key.equalsIgnoreCase("type")) {
						final CardT cardT = CardT.valueOf(behind.toUpperCase());
						select.cardType(cardT);
						continue;
					}
				} else {
					if (part.startsWith("mv")) {
						String behind = part.substring(2);
						final Operator ope = getOperator(behind);

						if (ope == Operator.GREATER_EQUAL || ope == Operator.LESS_EQUAL)
							behind = behind.substring(2);
						else
							behind = behind.substring(1);

						select.CCM(Integer.parseInt(behind), ope);
						continue;
					}
					select.name(part, true);
				}
			}

			final ArrayList<? extends Targetable> finded = select.matchAny(null, null, cards);
			setup(finded);
		});

		setTop(new HBox(search, execute));
		setCenter(pane);

		deckPane.setPrefHeight(300);
		setBottom(deckPane);
	}

	public Operator getOperator(String str) {
		Operator ope = switch (str.charAt(0)) {
		case '=' -> Operator.EQUAL;
		case '<' -> Operator.LESS;
		case '>' -> Operator.GREATER;
		case '%' -> Operator.EVEN;
		default -> Operator.ODD;
		};

		if (str.charAt(1) == '=')
			ope = ope == Operator.LESS ? Operator.LESS_EQUAL : Operator.GREATER_EQUAL;

		return ope;
	}

	public void setup(final ArrayList<? extends Targetable> cards) {
		grid.getChildren().clear();
		int x = 0;
		int y = 0;
		for (final Targetable target : cards) {
			if (target instanceof Card card) {
				final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
				final CardUIFX ui = new CardUIFX(card, img);

				ui.setScaleX(0.7);
				ui.setScaleY(0.7);

				ui.setPrefHeight(CardUIFX.CARD_HEIGHT * 0.7f);
				ui.setPrefWidth(CardUIFX.CARD_WIDTH * 0.7f);

				ui.setOnMouseClicked(e -> {
					deckPane.addCard(ui.getTargetable());
				});

				grid.add(ui, x, y);
				x++;
				if (x > 4) {
					x = 0;
					y++;
				}
			}
		}
	}
}
