package magic.graphics;

import javafx.scene.layout.Pane;
import magic.graphics.card.CardToolTip;
import magic.graphics.card.CardUIFX;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.Card;
import magic.main.Magic;

public class CardViewer extends Pane {

	private CardToolTip tip;

	public CardViewer() {
		tip = new CardToolTip();
	}

	public void setCard(final CardUIFX c) {
		final Card card = c.getTargetable();
		final CardUIFX clone = new CardUIFX(card,
				ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg"));
		
		tip.setCard(clone);
		
		// On fait que le défilement du text est lié
		clone.getOracle().vvalueProperty().bind(c.getOracle().vvalueProperty());
		clone.getOracle().hvalueProperty().bind(c.getOracle().hvalueProperty());
		
		setTranslateX((Magic.WIDTH - clone.getWidth() - tip.getWidth()) / 2);
		setTranslateY(100);
		
		getChildren().clear();
		getChildren().addAll(tip, clone);
		
		setVisible(true);
	}
}
