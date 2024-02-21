package magic.graphics;

import java.util.ArrayList;

import javafx.animation.FillTransition;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import magic.graphics.card.CardUIFX;
import magic.logic.card.Card;
import magic.logic.card.abilities.utils.Owner;

public class ZonePane extends HBox {

	private final Owner player;

	private final StackPane graveyard;

	private final ArrayList<CardUIFX> exile;

	public ZonePane(final Owner player, final ChoosePane pane) {
		super(5);
		this.player = player;
		exile = new ArrayList<>();

		final int width = (int) (CardUIFX.CARD_WIDTH * 0.2f);
		final int height = (int) (CardUIFX.CARD_HEIGHT * 0.2f);

		graveyard = new StackPane();
		graveyard.setPrefSize(width, height);

		final ImageView deck = new ImageView("file:resources/image/common/back_th.png");
		deck.setFitWidth(width);
		deck.setFitHeight(height);

		final Rectangle rect = new Rectangle(width, height);

		final FillTransition ft = new FillTransition(Duration.millis(2000), rect, Color.BLACK, Color.GRAY);
		ft.setCycleCount(FillTransition.INDEFINITE);
		ft.setAutoReverse(true);

		ft.play();

		graveyard.setOnMouseClicked(e -> {
			final ArrayList<CardUIFX> cards = new ArrayList<>();
			for (final Node node : graveyard.getChildren())
				if (node instanceof CardUIFX card)
					cards.add(card);

			pane.showCard("Graveyard", cards, graveyard);
		});

		rect.setOnMouseClicked(e -> {
			pane.showCard("Exile", exile, null);
		});

		getChildren().addAll(graveyard, deck, rect);
	}

	public void putInGraveyard(final CardUIFX card) {
		if (card != null) {
			card.setScaleX(0.2f);
			card.setScaleY(0.2f);
			card.setTranslateX(-30);
			card.setTranslateY(-42);

			final int width = (int) (CardUIFX.CARD_WIDTH * 0.2f);
			final int height = (int) (CardUIFX.CARD_HEIGHT * 0.2f);
			
			card.setOnMouseClicked(null);
			card.setOnMouseEntered(null);
			card.setOnMouseExited(null);

			card.setPrefSize(width, height);

			graveyard.getChildren().add(card);
		}
	}

	public CardUIFX removeFromExile(final Card card) {
		for (int i = 0; i < exile.size(); i++)
			if(exile.get(i) instanceof  CardUIFX)
			{
				CardUIFX c = (CardUIFX) exile.get(i);
				if(c.getTargetable().getGameID() == card.getGameID()) {
					exile.remove(i);
					return c;
				}
			}

		return null;
	}

	public void putInExile(final CardUIFX card) {
		exile.add(card);
	}

	public Owner getPlayer() {
		return player;
	}

	public StackPane getGraveyard() {
		return graveyard;
	}
}
