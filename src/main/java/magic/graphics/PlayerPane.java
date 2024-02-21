package magic.graphics;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import magic.graphics.card.CardUIFX;
import magic.graphics.card.CardUtils;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.Card;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.AbstractMana;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.place.Place;

public class PlayerPane extends AnchorPane {

	private Pane hand;

	private Text life;

	private Pane manaPool;

	private Owner player;

	public PlayerPane(final int lifeStart, final Owner player, final boolean top) {
		this.player = player;

		hand = new Pane();
		manaPool = new Pane();

		// Le text pour la vie
		life = new Text(lifeStart + "");
		life.setFill(Color.WHITE);
		life.setFont(CardUtils.beleren);

		// Le cercle derriere la vie
		final ImageView circle = new ImageView("file:resources/image/common/life.png");
		circle.setFitWidth(64);
		circle.setFitHeight(64);

		final StackPane li = new StackPane(circle, life);
		li.setLayoutX(220);
		
		hand.setPrefWidth(600);
		life.setLayoutX(20);

		if (top) {
			AnchorPane.setTopAnchor(hand, -100d);
			AnchorPane.setTopAnchor(manaPool, 90d);
			AnchorPane.setTopAnchor(li, 80d);
		} else {
			AnchorPane.setBottomAnchor(hand, -100d);
			AnchorPane.setBottomAnchor(manaPool, 90d);
			AnchorPane.setBottomAnchor(li, 80d);
		}
		
		getChildren().addAll(manaPool, li, hand);
	}

	public void putInHand(final Game game, final Card card, final Place from) {
		final CardUIFX c;
		if (from == Place.DECK)
			c = new CardUIFX(card, ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg"));
		else
			c = null;

		// Quand on click sur la carte ça la met direct dans le stack
		c.setOnMouseClicked(e -> {
			c.setVisible(false);
			if (game.putOnStack(card, card, card.getOwner()))
				c.setVisible(true);
		});

		c.setOnMouseEntered(e -> {
			c.toFront();
			c.setScaleX(0.8f);
			c.setScaleY(0.8f);
			c.setTranslateY(-330);
			c.setRotate(0);
		});

		c.setOnMouseExited(e -> {
			c.setScaleX(0.35f);
			c.setScaleY(0.35f);
			c.setTranslateY(c.prevY);
			c.setRotate(c.prevAngle);
		});

		c.setScaleX(0.35f);
		c.setScaleY(0.35f);
		final int height = (int) (CardUIFX.CARD_HEIGHT * 0.35f) + 20;
		c.setPrefHeight(height);

		hand.getChildren().add(c);
		updateHand();
	}

	public void updateHand() {
		// La position des cartes en mains
		final int width = (int) (CardUIFX.CARD_WIDTH * 0.35f);
		final int size = hand.getChildren().size();

		int mid = 0;
		int begAngle = -(5 * size);
		for (final Node node : hand.getChildren()) {
			final CardUIFX c = (CardUIFX) node;
			node.setLayoutX(mid);
			node.setRotate(begAngle);
			c.prevX = mid;
			node.setTranslateY(-50);
			c.prevY = (int) node.getTranslateY();
			c.prevAngle = begAngle;
			mid += width;
			begAngle += 20;
		}
	}

	public void updateManaPool(final Game game) {
		manaPool.getChildren().clear();

		final ManaCost cost = game.getPlayer(player, null)[0].getReserve();
		final float scale = 0.18f;
		int xMana = 0;
		int numOfSymbol = 0;

		final int widthImg = (int) ((float) 165 * scale) + 10;

		for (final AbstractMana mana : cost.getListOfCosts()) {

			if (mana instanceof Mana m) {
				final ImageView imgMana = new ImageView(CardUtils.getMana(mana.getMana()));
				
				imgMana.setFitHeight(165 * scale);
				imgMana.setFitWidth(165 * scale);

				imgMana.setLayoutX(xMana);

				manaPool.getChildren().add(imgMana);
				
				final int number = m.getNumber();
				if (number > 1) {

					final Text nmb = new Text("" + number);
					nmb.setFill(Color.BLUE);
					nmb.setFont(CardUtils.beleren);

					nmb.setLayoutX(xMana + 25);
					nmb.setLayoutY(35);

					manaPool.getChildren().add(nmb);
				}

				numOfSymbol++;
			}

			xMana += widthImg;
		}

		AnchorPane.setRightAnchor(manaPool, 200d - widthImg * numOfSymbol);
	}

	public void updateLife(final int newLife) {
		life.setText("" + newLife);
	}

	public Pane getHand() {
		return hand;
	}

	public Owner getPlayer() {
		return player;
	}
}
