package magic.graphics.utils;

public enum CardSection {
	// Normal card
	FRAME("basic.jpg"), STAMP("stamp.jpg"),

	// Land card
	LAND_FRAME("land.jpg"), LAND_STAMP("land_stamp.jpg"),

	// Planeswalker card
	PLANES("planeswalker/card.png"), PLANES1("planeswalker/card1.png"), PLANES_STAMP("planeswalker/stamp.jpg"),

	// Saga card
	SAGA("saga.jpg"),

	// Snow card
	SNOW("snow/basic.jpg"), SNOW_LAND("snow/land.jpg"),

	// Adventure card
	ADVENTURE("adventure/card.png"), ADVENTURE_STAMP("adventure/stamp.jpg"), ADVENTURE_STAT("adventure/stat.png"),
	ADVENTURE_ART("adventure/card1.png"),

	// Token card
	TOKEN("token/card.png"), TOKEN1("token/card1.png"), TOKEN2("token/card2.png"),

	// art for default frame
	STAT("stat.png"), LEGEND("legend.png"), COMPANION("companion.png"),

	/**
	 *****************************
	 ****** ALTERNATIVE ART ******
	 *****************************
	 */

	// Nyx alternative art
	NYX("nyx/frame.png"), NYX_LEGEND("nyx/legend.png"),

	// Devoid alternative art
	DEVOID("devoid.png"),

	// Mystical archive alternative art
	MYSTICAL("mystical.png"),

	// Promo alternative art
	PROMO("promo.png"), LPROMO("land_promo.png"),

	// M21 alternative art
	M21("m21/card.png"), LM21("m21/land.png"), M21_STAMP("m21/stamp.png"), M21_STAT("m21/stat.png"),

	// Borderless alternative art
	BL_CARD("borderless/card.png"), BL_TALL("borderless/tall.png"), BL_STAMP("borderless/stamp.png"),
	BL_LEGEND("borderless/legend.png"), BL_STAT("borderless/stat.png"),
	
	// Zendikar rising showcase alternative art
	ZNR_FRAME("zendikar/card.png"), ZNR_LEGEND("zendikar/legend.png"), ZNR_STAT("zendikar/stat.png"),

	// Kaldheim showcase alternative art
	KHM_FRAME("kaldheim/card.png"), KHM_STAT("kaldheim/stat.png"),

	/**
	 *****************************
	 ***** DOUBLE FACED CARD *****
	 *****************************
	 */

	// Style normale (zendikar rising, kaldheim, strixhaven,...)
	DF("double_faced/card.jpg"), DF_BACK("double_faced/back.jpg"), DF_LAND("double_faced/land.jpg"),
	DF_LAND_BACK("double_faced/land_back.jpg"),

	// Notched style (inistrad, eldrich moon,...)
	DF_NOTCHED("double_faced/notched.jpg"), DF_LAND_NOTCHED("double_faced/land_notched.jpg"),

	// Arrow
	DF_ARROW("double_faced/arrow.png"), DF_ARROW_BACK("double_faced/arrow_back.png"),

	// Flag
	DF_FLAG("double_faced/flag.png"), DF_FLAG_BACK("double_faced/flag1.png"), DF_STAT("double_faced/stat.png");

	private final String path;

	CardSection(final String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
