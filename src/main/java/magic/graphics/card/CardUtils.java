package magic.graphics.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import magic.graphics.utils.CardSection;
import magic.graphics.utils.ColorStyle;
import magic.graphics.utils.FrameAssembler;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.CColor;
import magic.logic.card.Rarity;
import magic.logic.card.mana.DoubleMana;
import magic.logic.card.mana.MCType;

public abstract class CardUtils {

	protected static final Font matrixBoldArtist;

	protected final static Font plantinItalic;

	protected final static Font matrixBoldSmallCaps;

	protected final static HashMap<Float, Font> plantins;
	
	public final static Font plantin;

	public final static Font beleren;

	protected final static Font beleren_mini;

	protected static final Image artist_arrow;

	protected static final Image back;

	protected static final Image emblem;

	protected static final Image foil_stamp;

	protected static final Image saga_foil_stamp;

	protected static final Image bar;

	protected static final Image tap;

	protected static final Image loyalty;

	protected static final Image loyalty_up;

	protected static final Image loyalty_down;

	protected static final Image loyalty_zero;

	protected static final HashMap<String, Image> sagaChapters;

	protected static final EnumMap<MCType, Image> manas;

	protected static final HashMap<String, Image> doubleManas;

	protected static final HashMap<Integer, Image> genericManas;

	protected static final EnumMap<Rarity, Image> rarete;

	protected static final EnumMap<ColorStyle, HashMap<String, Image>> cards;

	protected static final HashMap<String, EnumMap<CardSection, Image>> doubleCards;

	private static Font loadFont(final String path, final float size) {
		return Font.loadFont("file:" + path, size);
	}

	static {
		// Load Font
		final String font = "resources/font/";

		// Text de la carte
		plantin = loadFont(font + "Plantin.otf", 20);

		// Nom de la carte
		beleren = loadFont(font + "beleren.ttf", 20);

		plantins = new HashMap<>();
		plantins.put(20f, plantin);

		// Les types de la carte
		beleren_mini = loadFont(font + "beleren.ttf", 16);

		// Flavor
		plantinItalic = loadFont(font + "Plantin-Italic.otf", 20);

		matrixBoldArtist = loadFont(font + "Matrix-Bold.ttf", 13);

		matrixBoldSmallCaps = loadFont(font + "MatrixBoldSmallCaps.ttf", 25);

		// Load image
		final String imgFolder = "resources/image/";

		// Planeswalker counter
		String folder = imgFolder + "common/planeswalker/";
		loyalty = ImageLoader.getImage(folder + "loyalty.png");
		loyalty_up = ImageLoader.getImage(folder + "loyaltyp.png");
		loyalty_down = ImageLoader.getImage(folder + "loyaltyd.png");
		loyalty_zero = ImageLoader.getImage(folder + "loyaltyz.png");

		// Saga chapter
		final String[] chaps = { "I", "II", "III", "IV", "V" };
		sagaChapters = new HashMap<>();
		folder = imgFolder + "saga/";
		for (int i = 1; i < 6; i++)
			sagaChapters.put(chaps[i - 1], ImageLoader.getImage(folder + i + ".png"));

		// Les marque de tampon
		foil_stamp = ImageLoader.getImage(imgFolder + "common/foil_stamp.png");
		saga_foil_stamp = ImageLoader.getImage(imgFolder + "saga/foil_stamp.png");

		// Rarete
		rarete = new EnumMap<>(Rarity.class);
		for (final Rarity rarity : Rarity.values())
			rarete.put(rarity, ImageLoader.getImage(imgFolder + "rarete/" + rarity.name().charAt(0) + ".png"));

		// Tap
		tap = ImageLoader.getImage(imgFolder + "mana/tap.png");

		// Basic mana
		manas = new EnumMap<>(MCType.class);
		for (final MCType mana : MCType.values())
			if (mana != MCType.GENERIC)
				manas.put(mana, ImageLoader.getImage(imgFolder + "mana/basic/" + mana.name().toLowerCase() + ".png"));

		// Double mana
		doubleManas = new HashMap<>();
		final String[] doubleM = { "DG", "DR", "GB", "RG", "RW", "BD", "BR", "WD", "WB", "GW" };
		for (final String mana : doubleM)
			doubleManas.put(mana, ImageLoader.getImage(imgFolder + "mana/double/" + mana + ".png"));

		// Generic mana
		genericManas = new HashMap<>();
		for (int i = 0; i < 16; i++)
			genericManas.put(i, ImageLoader.getImage(imgFolder + "mana/generic/" + i + ".png"));

		// Toute les infos des cartes
		cards = new EnumMap<>(ColorStyle.class);
		doubleCards = new HashMap<>();

		// Autre
		artist_arrow = ImageLoader.getImage(imgFolder + "common/artist_arrow.png");
		back = ImageLoader.getImage(imgFolder + "common/back.png");
		emblem = ImageLoader.getImage(imgFolder + "emblem.jpg");
		bar = ImageLoader.getImage(imgFolder + "common/bar.png");
	}

	public static Image getMana(final MCType mana) {
		return manas.get(mana);
	}

	public static Image getDoubleMana(final MCType mana1, final MCType mana2) {
		final char first = mana1.name().charAt(0), second = mana2.name().charAt(0);
		if (doubleManas.containsKey(first + "" + second))
			return doubleManas.get(first + "" + second);
		else
			return doubleManas.get(second + "" + first);
	}

	public static Image getDoubleMana(final DoubleMana mana) {
		return getDoubleMana(mana.getMana(), mana.getSecondMana());
	}

	public static Image getGeneric(final int nmb) {
		return genericManas.get(nmb);
	}

	public static Image getRarity(final Rarity rarity) {
		return rarete.get(rarity);
	}

	public static ImageView getSection(final ColorStyle color, final CardSection section, final boolean artefact) {
		HashMap<String, Image> c = cards.get(color);
		if (c == null) {
			c = new HashMap<>();

			cards.put(color, c);
		}

		String key = section.name();
		key += artefact ? "a" : "";

		Image img = c.get(key);
		if (img == null) {
			if (artefact) {
				if (FrameAssembler.needArtefactAssemble(section)) {
					img = new FrameAssembler(null, color, section).getFrame();
				} else
					return getSection(color, section, false);
			} else {
				final String path = "resources/image/card/" + color.name().toLowerCase() + "/" + section.getPath();
				img = ImageLoader.getImage(path);
			}

			c.put(key, img);
		}

		return new ImageView(img);
	}

	private static final List<String> doubleM = Arrays.asList("DG", "DR", "GB", "RG", "RW", "BD", "BR", "WD", "WB",
			"GW");

	public static ImageView getDoubleSection(final ArrayList<CColor> cols, final CardSection section, final boolean hybrid,
			final boolean artefact) {
		final char first = cols.get(0).name().charAt(0), second = cols.get(1).name().charAt(0);
		String key = "";
		ColorStyle f, s;
		if (doubleM.contains(first + "" + second)) {
			f = ccolorToStyle(cols.get(0));
			s = ccolorToStyle(cols.get(1));
			key = first + "" + second;
		} else {
			f = ccolorToStyle(cols.get(1));
			s = ccolorToStyle(cols.get(0));
			key = second + "" + first;
		}

		key += hybrid ? "b" : "";
		key += hybrid ? "a" : "";
		EnumMap<CardSection, Image> c = doubleCards.get(key);
		if (c == null) {
			c = new EnumMap<>(CardSection.class);

			doubleCards.put(key, c);
		}

		Image img = c.get(section);
		if (img == null) {
			img = new FrameAssembler(f, s, section, hybrid).getFrame();

			if (artefact && FrameAssembler.needArtefactAssemble(section))
				img = new FrameAssembler(img.getPixelReader(), null, section).getFrame();

			c.put(section, img);
		}

		return new ImageView(img);
	}

	public static Image getSagaChap(final String chap) {
		return sagaChapters.get(chap);
	}

	public static Font getPlantin(final float size) {
		Font f = plantins.get(size);

		if (f == null) {
			f = loadFont("resources/font/plantin.ttf", size);
			plantins.put(size, f);
		}

		return f;
	}

	public static ColorStyle ccolorToStyle(final CColor col) {
		return switch (col) {
		case BLUE -> ColorStyle.BLUE;
		case COLORLESS -> ColorStyle.COLORLESS;
		case DARK -> ColorStyle.DARK;
		case GREEN -> ColorStyle.GREEN;
		case RED -> ColorStyle.RED;
		case WHITE -> ColorStyle.WHITE;
		default -> ColorStyle.MULTICOLORED;
		};
	}
}
