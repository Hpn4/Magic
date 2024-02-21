package magic.graphics.collection;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import magic.graphics.card.CardUIFX;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.Card;

public class DeckPane extends HBox {
	
	private final ArrayList<Integer> manaValue;

	public DeckPane() {
		super(5);
		manaValue = new ArrayList<>();
		
		getChildren().add(new StackPane());
		manaValue.add(0);
	}
	
	public void addCard(final Card card) {
		final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
		final CardUIFX ui = new CardUIFX(card, img);
		
		StackPane pane = null;
		final int ccm = card.getCardCost().getCCM();
		for(int i = 0; i < manaValue.size(); i++) {
			final int ccm2 = manaValue.get(i);
			
			// On est au bon endroit
			if(ccm2 == ccm) {
				pane = (StackPane) getChildren().get(i);
				break;
			}
			// La section n'a pas encore été créer
			else if(ccm2 > ccm) {
				pane = new StackPane();
				getChildren().add(i, pane);
				manaValue.add(i, ccm);
				break;
			}
		}
		
		if(pane == null) {
			pane = new StackPane();
			getChildren().add(pane);
			manaValue.add(ccm);
		}
		
		pane.setTranslateY(-80);
		
		final float scale = 0.4f;
		
		ui.setScaleX(scale);
		ui.setScaleY(scale);

		ui.setPrefHeight(CardUIFX.CARD_HEIGHT * scale);
		ui.setPrefWidth(CardUIFX.CARD_WIDTH * scale);
		
		pane.getChildren().add(ui);
		
		int y = 0;
		for(final Node node : pane.getChildren()) {
			node.setTranslateY(y);
			y += 25;
		}
	}
}
