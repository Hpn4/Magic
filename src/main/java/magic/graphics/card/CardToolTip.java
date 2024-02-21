package magic.graphics.card;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import magic.graphics.utils.Keyword;
import magic.logic.card.Card;

public class CardToolTip extends GridPane {

	private Text size;

	private Color bg;

	private static Image quote;

	static {
		quote = new Image("file:resources/keyword/image/quote.png");
	}

	public CardToolTip() {
		size = new Text("");
		size.setFont(CardUtils.plantinItalic);

		size.setWrappingWidth(350);
		size.setLineSpacing(3);

		bg = new Color(24.0f / 256.0f, 24.0f / 256.0f, 24.0f / 256.0f, 1);

		setMaxWidth(450);
		setPrefWidth(450);
		setMinWidth(450);

		setVgap(2);
	}

	private int getHeight(final String text) {
		size.setText(text);
		return (int) size.getLayoutBounds().getHeight();
	}

	public void setCard(final CardUIFX card) {
		final Card c = card.getTargetable();
		final String flavor = c.getFlavor();

		// Si la carte à un texte d'ambiance
		if (flavor != null && !flavor.equals("")) {
			getChildren().clear();

			int y = (int) getHeight(flavor) + 20;

			final Rectangle rect = new Rectangle(0, 0, 450, y);
			rect.setFill(bg);
			rect.setStroke(Color.LIGHTGRAY);

			rect.setArcHeight(20);
			rect.setArcWidth(20);

			final Text text = new Text(flavor);
			text.setFill(Color.LIGHTGREY);
			text.setFont(CardUtils.plantinItalic);
			text.setWrappingWidth(350);

			GridPane.setMargin(text, new Insets(5, 0, 5, 50));

			add(rect, 0, 0);

			// Image des guillemet
			final ImageView img = new ImageView(quote);
			img.setScaleX(0.5f);
			img.setScaleY(0.5f);
			img.setTranslateY(-5);
			img.setTranslateX(-10);

			add(img, 0, 0);

			add(text, 0, 0);

			show(card);
		}

		final String[] keywords = c.getKeywords();
		if (keywords != null) {
			getChildren().clear();

			for (int i = 0; i < keywords.length; i++) {
				final String ke = keywords[i];
				if (ke != null && !ke.equals(""))
					add(getKeyword(ke), 0, i + 1);
			}

			show(card);
		}
	}

	public Parent getKeyword(final String ke) {
		final String keyword = ke.charAt(0) + ke.toLowerCase().substring(1);
		final GridPane pane = new GridPane();

		int y = getHeight(keyword) * 2;

		final String reminder = Keyword.getKeyword(keyword);

		y += getHeight(reminder);

		final Rectangle rect = new Rectangle(0, 0, 450, y);

		rect.setFill(bg);
		rect.setStroke(Color.LIGHTGRAY);

		rect.setArcHeight(20);
		rect.setArcWidth(20);

		pane.add(rect, 0, 0, 2, 2);

		final Text title = new Text(keyword);
		title.setFill(Color.WHITE);
		title.setFont(CardUtils.beleren);

		GridPane.setMargin(title, new Insets(5, 0, 0, 00));

		final Text text = new Text(reminder);
		text.setFill(Color.LIGHTGREY);
		text.setFont(CardUtils.plantinItalic);
		text.setWrappingWidth(345);

		final Image img = Keyword.getImage(keyword);

		if (img != null) {
			final ImageView im = new ImageView(img);
			im.setScaleX(0.75f);
			im.setScaleY(0.75f);

			pane.add(im, 0, 0, 1, 2);
		} else {
			GridPane.setMargin(title, new Insets(5, 0, 0, 50));
			GridPane.setMargin(text, new Insets(0, 0, 0, 50));
		}

		pane.add(text, 1, 1);
		pane.add(title, 1, 0);

		return pane;
	}

	private void show(final CardUIFX card) {
		setTranslateX(card.getTranslateX() + card.getLayoutX() + 370);
		setTranslateY(card.getTranslateY() + card.getLayoutY() - 5);

		setVisible(true);
	}
}
