package magic.logic.place;

public class Graveyard extends AbstractPlace {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7972657978550737274L;

	public Graveyard(OptiPlace place) {
		super(place.getAllGraveyard());
	}
}
