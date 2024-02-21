package magic.graphics.card;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import magic.logic.card.mana.MCType;
import magic.logic.utils.Utils;

public class TextBox extends ScrollPane {

	private TextFlow flow;

	public TextBox(final String oracle, final int x, final int y, final int maxWidth, final int maxHeight,
			final Color col) {
		this(oracle, x, y, maxWidth, maxHeight, col, 0);
	}

	public TextBox(final String oracle, final int x, final int y, final int maxWidth, final int maxHeight,
			final Color col, final int type) {
		getStylesheets().add("file:resources/keyword/scroll.css");

		setLayoutX(x);
		setLayoutY(y);

		setHbarPolicy(ScrollBarPolicy.NEVER);
		setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		setPrefHeight(maxHeight);
		setPrefWidth(maxWidth);

		flow = new TextFlow();

		flow.setPrefWidth(maxWidth);
		
		setContent(flow);

		final Font font = CardUtils.plantin;

		final String[] oracles = oracle.split("\n");
		final boolean isModdal = oracle.contains("•");

		// On parcours toute les lignes deja rangé
		for (String line : oracles) {
			if (!line.equals("")) {

				// Saga
				if (type == 1) {
					String nmb = line.substring(0, line.indexOf("—")).replace(" ", "");
					line = line.substring(line.indexOf("—") + 2);

					if (nmb.contains(",")) {
						final String[] chaps = nmb.split(",");

						for (final String chap : chaps)
							drawImage(CardUtils.getSagaChap(chap), 0.5f, 0);
					} else
						drawImage(CardUtils.getSagaChap(nmb), 0.5f, 0);
				}

				// Planes
				if (type == 2) {
					if (line.contains(":")) {
						String nmb = line.substring(0, line.indexOf(":")).replace(" ", "");
						nmb = nmb.replace("−", "-");

						line = line.substring(line.indexOf(":"));

						final Image loyalty;
						if (nmb.startsWith("+"))
							loyalty = CardUtils.loyalty_up;
						else if (nmb.startsWith("-"))
							loyalty = CardUtils.loyalty_down;
						else
							loyalty = CardUtils.loyalty_zero;

						final ImageView l = new ImageView(loyalty);
						l.setFitHeight(loyalty.getHeight() * 0.2f);
						l.setFitWidth(loyalty.getWidth() * 0.2f);
						l.setTranslateY(10);

						final Text loyaltyText = new Text(nmb);
						loyaltyText.setTranslateY(10);
						loyaltyText.setFill(Color.WHITE);
						loyaltyText.setFont(CardUtils.beleren_mini);

						flow.getChildren().add(new StackPane(l, loyaltyText));
					}
				}

				// Si la ligne comporte un symbole
				if (line.contains("{")) {

					// On compte le nombre de symbole dans la ligne et on boucle pour ce nombre
					final int limit = Utils.countOccurence(line, '{');
					for (int i = 0; i < limit; i++) {
						// On récupere la chaine de caractere avant le symbole et le symbole en lui meme
						final String before = line.substring(0, line.indexOf("{"));
						final String symbol = line.substring(line.indexOf("{"), line.indexOf("}"));

						line = line.substring(line.indexOf("}") + 1);

						// On dessine ce qu'il y a avant le symbole et au incremente x
						if (!before.equals("")) {
							final Text textBefore = new Text(before);
							textBefore.setFont(font);
							textBefore.setFill(col);

							flow.getChildren().add(textBefore);
						}

						// On dessine les symboles
						drawSymbol(symbol);
					}
				}

				// On dessine la fin de la ligne
				final Text lineText = new Text(line + (isModdal ? "\n" : "\n\n"));
				lineText.setFont(font);
				lineText.setFill(col);
				lineText.setWrappingWidth(maxWidth);

				flow.getChildren().add(lineText);
			}
		}
	}

	public void drawImage(final Image im, final float scale, final int transX) {
		final ImageView img = new ImageView(im);

		img.setTranslateY(5);
		img.setTranslateX(transX);

		img.setFitHeight(im.getHeight() * scale);
		img.setFitWidth(im.getWidth() * scale);

		flow.getChildren().add(img);
	}

	public void drawSymbol(final String word) {
		final String[] symbols = word.split("}");
		final int taille = symbols.length;

		for (int i = 0; i < taille; i++) {
			final String symbol = symbols[i].substring(1);

			if (symbol.equals("T"))
				drawImage(CardUtils.tap, 0.15f, 0);
			else {
				if (symbol.contains("/")) {
					final MCType mana1 = Utils.getMana(symbol.charAt(0));
					final MCType mana2 = Utils.getMana(symbol.charAt(2));

					final ImageView img = new ImageView(CardUtils.getDoubleMana(mana1, mana2));

					flow.getChildren().add(img);
				} else {
					Image img = null;

					final MCType mana = Utils.getMana(symbol);
					if (mana == MCType.GENERIC)
						img = CardUtils.getGeneric(Integer.parseInt(symbol));
					else
						img = CardUtils.getMana(mana);

					drawImage(img, 0.15f, 0);
				}
			}
		}
	}
}
