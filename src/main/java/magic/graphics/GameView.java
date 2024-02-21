package magic.graphics;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import magic.graphics.card.CardUIFX;
import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.main.Magic;

public class GameView extends Pane {

	private Game game;

	private Label info;

	private Pane battlefield;

	private Stack stack;

	private ChoosePane choose;

	private CardViewer cardViewer;

	private PlayerPane player1;

	private PlayerPane player2;

	private ZonePane zone1;

	private ZonePane zone2;

	public GameView(final Game game, final Owner player) {
		this.game = game;

		final AnchorPane main = new AnchorPane();

		cardViewer = new CardViewer();
		choose = new ChoosePane(main);

		player1 = new PlayerPane(20, player, false);
		AnchorPane.setBottomAnchor(player1, 0d);
		AnchorPane.setLeftAnchor(player1, (Magic.WIDTH - 600) / 2d);

		player2 = new PlayerPane(20, player == Owner.PLAYER1 ? Owner.PLAYER2 : Owner.PLAYER1, true);
		AnchorPane.setTopAnchor(player2, 0d);

		zone1 = new ZonePane(player, choose);
		AnchorPane.setLeftAnchor(zone1, 10d);
		AnchorPane.setBottomAnchor(zone1, 10d);
		
		zone2 = new ZonePane(player, choose);
		AnchorPane.setRightAnchor(zone2, 10d);
		AnchorPane.setTopAnchor(zone2, 10d);

		battlefield = new Pane();
		AnchorPane.setBottomAnchor(battlefield, 150d);

		stack = new Stack();

		AnchorPane.setTopAnchor(stack, 100d);
		AnchorPane.setRightAnchor(stack, 20d);

		info = new Label("SAMUT");
		info.setStyle(
				"-fx-background-color: rgba(0,0,0,0.8);-fx-background-radius:10px;-fx-padding: 5 600 5 600;-fx-paint-color:white;-fx-font-weight:bold;-fx-font-size: 25px;");

		info.setTextFill(Color.WHITE);
		info.setVisible(false);

		main.setPrefWidth(Magic.WIDTH);
		main.setPrefHeight(Magic.HEIGHT);

		main.getChildren().addAll(battlefield, player1, player2, zone1, zone2, stack);

		getChildren().addAll(main, choose, cardViewer, choose.getViewB());
	}

	public void putInBattlefield(final Card card, final Place from) {
		final CardUIFX c;
		if (from == Place.HAND)
			c = remove(player1.getHand(), card);
		else
			c = null;

		if (c != null) {
			battlefield.getChildren().add(c);
			c.setVisible(true);

			c.setScaleX(0.3f);
			c.setScaleY(0.3f);

			c.setRotate(0);
			c.setLayoutX(0);

			final int wid = (int) (CardUIFX.CARD_WIDTH * 0.3f) + 5;
			int x = 0;
			for (final Node node : battlefield.getChildren()) {
				node.setTranslateX(x);
				x += wid;
			}

			c.setOnMouseClicked(e -> {
				final ArrayList<ActivatedAbilities> activated = card.getCardAbilities().getActivated();
				if (activated != null) {
					if (activated.size() == 1)
						game.putOnStack(activated.get(0), card, card.getOwner());
					else {
						final Abilities a = game.chooseFrom(activated);

						if (activated != null)
							game.putOnStack(a, card, card.getOwner());
					}
				}
			});

			c.setOnMouseEntered(e -> {
				cardViewer.setCard(c);
				cardViewer.setVisible(true);
			});

			c.setOnMouseExited(e -> {
				cardViewer.setVisible(false);
			});
		}
	}

	public void setTextInfo(final String inf) {
		info.setText(inf);
		info.setVisible(true);
	}

	public CardUIFX remove(final Pane pane, final Card card) {
		for (int i = 0; i < pane.getChildren().size(); i++) {
			if (pane.getChildren().get(i) instanceof CardUIFX c && c.getTargetable().getGameID() == card.getGameID()) {
				pane.getChildren().remove(i);
				return c;
			}
		}

		return null;
	}

	public CardUIFX remove(final Owner player, final Place place, final Card card) {
		CardUIFX tmp = null;
		switch (place) {
		case HAND -> {
			if (player == player1.getPlayer()) {
				tmp = remove(player1.getHand(), card);
				player1.updateHand();
			} else {
				tmp = remove(player2.getHand(), card);
				player2.updateHand();
			}
		}
		case EXILE -> {
			if (player == zone1.getPlayer())
				tmp = zone1.removeFromExile(card);
			else
				tmp = zone2.removeFromExile(card);
		}
		case GRAVEYARD -> {
			if (player == zone1.getPlayer())
				tmp = remove(zone1.getGraveyard(), card);
			else
				tmp = remove(zone2.getGraveyard(), card);
		}
		case BATTLEFIELD -> {
			// TODO FAIRE 2 CHAMPS DE BATAILLE
			tmp = remove(battlefield, card);
		}
		default -> {
			
		}
		}

		return tmp;
	}
	
	public void putInGraveyard(final Owner player, final Card card, final Place previous) {
		if(player == zone1.getPlayer())
			zone1.putInGraveyard(remove(player, previous, card));
		else
			zone2.putInGraveyard(remove(player, previous, card));
	}

	public void putInHand(final Owner player, final Card card, final Place place) {
		if (player == player1.getPlayer())
			player1.putInHand(game, card, place);
		else
			player2.putInHand(game, card, place);
	}

	public void putIn(final Owner player, final Card card, final Place place) {

	}

	public void updateManePool(final Owner player) {
		if (player == player1.getPlayer())
			player1.updateManaPool(game);
		else
			player2.updateManaPool(game);
	}

	public void setLife(final Owner owner, final int life) {
		if (owner == player1.getPlayer())
			player1.updateLife(life);
		else
			player2.updateLife(life);
	}

	public ChoosePane getChoosePane() {
		return choose;
	}

	public Stack getStack() {
		return stack;
	}
}
