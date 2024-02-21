package magic.graphics.card;

import javafx.scene.layout.Pane;
import magic.logic.card.Targetable;

public abstract class ObjectUIFX<E extends Targetable> extends Pane {

	private boolean selected;
	
	private E e;
	
	public int prevX;
	
	public int prevY;
	
	public ObjectUIFX(final E e) {
		this.e = e;
		setMaxHeight(CardUIFX.CARD_HEIGHT);
		setMaxWidth(CardUIFX.CARD_WIDTH);
	}
	
	public E getTargetable() {
		return e;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(final boolean selected) {
		this.selected = selected;
	}
	
}
