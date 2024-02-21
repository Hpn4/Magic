package magic.graphics.card;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import magic.graphics.utils.CardSection;
import magic.graphics.utils.ColorStyle;
import magic.graphics.utils.Style;
import magic.graphics.utils.WebCardLoader;
import magic.logic.card.ArtifactT;
import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.CreatureT;
import magic.logic.card.EnchantT;
import magic.logic.card.LandT;
import magic.logic.card.Rarity;
import magic.logic.card.SpellT;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.mana.AbstractMana;
import magic.logic.card.mana.DoubleMana;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.utils.Utils;

public class CardUIFX extends ObjectUIFX<Card> {

	public static final int CARD_WIDTH = 375;

	public static final int CARD_HEIGHT = 523;

	private TextBox oracle;

	private String type;

	private Text statText;

	public float prevAngle, prevScale;
	
	public CardUIFX(final WebCardLoader loader) {
		this(loader.getCard(), loader.getArtCrop(), Style.NORMAL);
	}

	public String get(final Enum<?> term) {
		String str = term.name().toLowerCase();
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public TextBox getOracle() {
		return oracle;
	}

	public CardUIFX(final Card card, final Image img) {
		this(card, img, Style.NORMAL);
	}

	public CardUIFX(final Card card, final Image img, final Style style) {
		super(card);

		// Les super types en premier
		final String stat = updateCard(card);

		// On extrait l'identité
		final ColorStyle color = CardUtils.ccolorToStyle(card.getIdentity());

		final ArrayList<CColor> identity = card.getColorIdentity();
		boolean isHybrid = false;
		if (card.getCardCost() != null)
			for (final AbstractMana cost : card.getCardCost().getListOfCosts())
				if (cost instanceof DoubleMana) {
					isHybrid = true;
					break;
				}

		// Les couleurs des différents textes de la carte
		Color nameColor = Color.BLACK;
		Color textColor = Color.BLACK;
		Color typeColor = Color.BLACK;
		Color statColor = Color.BLACK;

		int yNameStart = 40;
		int yTypeStart = 314;

		// Les capacités
		final String[] ab = card.getOracle().split("\n");
		final int numAbilities = ab.length;

		// On extrait le type
		final boolean planes = card.hasType(CardT.PLANESWALKER);
		final boolean saga = card.getEnchantmentType() == EnchantT.SAGA;
		final boolean token = card.hasType(CardT.TOKEN);
		final boolean emblem = card.hasType(CardT.EMBLEM);
		final boolean adventure = card.getBackCard() == null ? false
				: card.getBackCard().getSpellType() == SpellT.ADVENTURE;

		// Si c'est une carte recto verso
		final boolean hasBack = card.getBackCard() != null && !adventure;
		final boolean doubleCol = identity == null ? false : identity.size() == 2;
		final boolean artefact = card.hasType(CardT.ARTIFACT);
		final boolean isSnow = card.hasType(CardT.SNOW);

		ImageView frame;
		final ImageView cropImg = new ImageView(img);
		if (planes) {
			CardSection frameS = CardSection.PLANES;
			if (numAbilities > 3)
				frameS = CardSection.PLANES1;

			if (doubleCol)
				frame = CardUtils.getDoubleSection(identity, frameS, isHybrid, artefact);
			else
				frame = CardUtils.getSection(color, frameS, artefact);

			final Rectangle noirRect = new Rectangle(25, 110, 330, 130);

			cropImg.setTranslateX(28);
			cropImg.setTranslateY(53);

			getChildren().addAll(noirRect, cropImg, frame);

			yNameStart = 32;
		} else if (saga) {
			if (doubleCol)
				frame = CardUtils.getDoubleSection(identity, CardSection.SAGA, isHybrid, artefact);
			else
				frame = CardUtils.getSection(color, CardSection.SAGA, artefact);

			cropImg.setTranslateX(189);
			cropImg.setTranslateY(58);
			cropImg.setScaleY(0.99f);

			getChildren().addAll(frame, cropImg);

			yTypeStart = 462;
		} else if (token) {
			// Textless token
			if (card.getOracle().length() == 0) {
				frame = CardUtils.getSection(color, CardSection.TOKEN, artefact);

				cropImg.setTranslateY(60);

				yTypeStart = 448;
			} else if (ab.length >= 1 && ab.length <= 3) {
				frame = CardUtils.getSection(color, CardSection.TOKEN1, artefact);

				cropImg.setTranslateY(63);
				cropImg.setScaleY(1.01f);

				yTypeStart = 373;
			} else {
				frame = CardUtils.getSection(color, CardSection.TOKEN2, artefact);

				cropImg.setTranslateY(60);

				yTypeStart = 373;
			}

			cropImg.setTranslateX(10);

			getChildren().addAll(cropImg, frame);
			yNameStart = 39;
			nameColor = Color.DARKORANGE;
		} else if (emblem) {
			frame = new ImageView(CardUtils.emblem);

			yTypeStart = 373;

			getChildren().addAll(frame, cropImg);
		} else if (adventure) {
			if (doubleCol)
				frame = CardUtils.getDoubleSection(identity, CardSection.ADVENTURE, isHybrid, artefact);
			else
				frame = CardUtils.getSection(color,
						style == Style.ADVENTURE ? CardSection.ADVENTURE_ART : CardSection.ADVENTURE, artefact);

			cropImg.setTranslateX(30);
			cropImg.setTranslateY(60);

			yNameStart = 39;
			yTypeStart += 1;

			getChildren().addAll(frame, cropImg);
		} else {
			CardSection f;
			if (card.hasType(CardT.LAND)) {
				if (isSnow)
					f = CardSection.SNOW_LAND;
				else
					f = switch (style) {
					case NORMAL -> hasBack ? CardSection.DF_LAND : CardSection.LAND_FRAME;
					case M21 -> CardSection.LM21;
					case BACK -> CardSection.DF_LAND_BACK;
					default -> CardSection.LPROMO;
					};
			} else {
				if (isSnow)
					f = CardSection.SNOW;
				else
					f = switch (style) {
					case NORMAL -> hasBack ? CardSection.DF : CardSection.FRAME;
					case M21 -> {
						nameColor = typeColor = Color.WHITE;
						yield CardSection.M21;
					}
					case BACK -> CardSection.DF_BACK;
					case MYSTICAL -> CardSection.MYSTICAL;
					case BORDERLESS -> CardSection.BL_TALL;
					case ZENDIKAR -> CardSection.ZNR_FRAME;
					case KALDHEIM -> {
						nameColor = typeColor = Color.WHITE;
						yTypeStart += 10;
						yNameStart += 3;
						yield CardSection.KHM_FRAME;
					}
					default -> {
						nameColor = typeColor = textColor = Color.WHITE;
						yield CardSection.PROMO;
					}
					};
			}

			if (doubleCol)
				frame = CardUtils.getDoubleSection(identity, f, isHybrid, artefact);
			else
				frame = CardUtils.getSection(color, f, artefact);

			if (style == Style.BORDERLESS || style == Style.ZENDIKAR) {
				nameColor = typeColor = textColor = statColor = Color.WHITE;
				getChildren().addAll(cropImg, frame);
			} else {
				cropImg.setTranslateX(30);
				cropImg.setTranslateY(60);

				if (style == Style.KALDHEIM) {
					cropImg.setScaleY(1.1f);
					getChildren().addAll(cropImg, frame);
				} else
					getChildren().addAll(frame, cropImg);
			}
		}

		// Si carte legendaire, on ajoute l'image et on noircie le haut de la carte
		if (card.hasType(CardT.LEGENDARY) && !planes && style != Style.KALDHEIM) {
			if (style != Style.BORDERLESS)
				getChildren().add(new Rectangle(0, 10, 375, 15));
			ImageView legend;

			// Si la carte est un compagnion on change la barre de legendaire
			final boolean comp = Utils.contains(card.getKeywords(), "COMPANION");
			final CardSection sect = comp ? CardSection.COMPANION
					: style == Style.BORDERLESS ? CardSection.BL_LEGEND
							: style == Style.ZENDIKAR ? CardSection.ZNR_LEGEND : CardSection.LEGEND;

			// On test si la carte est à double couleur
			if (doubleCol)
				legend = CardUtils.getDoubleSection(identity, sect, isHybrid, artefact);
			else
				legend = CardUtils.getSection(color, sect, artefact);

			getChildren().add(legend);
		}

		// Fleche d'artiste
		final ImageView arrow = new ImageView(CardUtils.artist_arrow);
		arrow.setTranslateX(50);
		arrow.setTranslateY(499);

		// Si la carte est rare ou mythic on dessine le tampon brillant
		if ((card.getRarity() == Rarity.RARE || card.getRarity() == Rarity.MYTHIC) && style != Style.BACK) {
			// Le contour differe en fonction de la couleur de la carte et du type
			if (!saga && style != Style.MYSTICAL) {
				CardSection s;
				if (style == Style.M21)
					s = CardSection.M21_STAMP;
				else if (planes)
					s = CardSection.PLANES_STAMP;
				else if (card.hasType(CardT.LAND))
					s = CardSection.LAND_STAMP;
				else if (adventure)
					s = CardSection.ADVENTURE_STAMP;
				else if (style == Style.BORDERLESS)
					s = CardSection.BL_STAMP;
				else
					s = CardSection.STAMP;

				ImageView stampImg;
				if (doubleCol)
					stampImg = CardUtils.getDoubleSection(identity, s, isHybrid, artefact);
				else
					stampImg = CardUtils.getSection(color, s, artefact);

				stampImg.setTranslateX(164);
				stampImg.setTranslateY(472);

				getChildren().add(stampImg);
			}

			final ImageView stamp = new ImageView(saga ? CardUtils.saga_foil_stamp : CardUtils.foil_stamp);
			stamp.setTranslateX(164);
			stamp.setTranslateY(471);

			getChildren().add(stamp);
		}

		// Le petit symbole de rarete
		final ImageView imgRarity = new ImageView(CardUtils.getRarity(card.getRarity()));
		imgRarity.setTranslateX(300);
		imgRarity.setTranslateY(yTypeStart - 17);

		getChildren().addAll(arrow, imgRarity);

		// Mana
		drawMana(yNameStart - 78, CARD_WIDTH - 95, 0.15f, card.getCardCost());

		// Text
		final int xText = 30, yText = 345;

		// Le text d'effet de la carte. La position diffère en fonction du type de la
		// carte
		if (planes)
			oracle = new TextBox(card.getOracle(), xText + 10, yText - 20, 300, 150, textColor, 2);
		else if (saga)
			oracle = new TextBox(card.getOracle(), xText + 12, yNameStart + 30, 145, 370, textColor, 1);
		else if (adventure)
			oracle = new TextBox(card.getOracle(), xText + 160, yText, 170, 110, textColor);
		else
			oracle = new TextBox(card.getOracle(), xText + 5, yText - 10, 310, 140, textColor);

		getChildren().add(oracle);

		// Nom de la carte
		final Text nameText = new Text(xText, yNameStart + 8, card.getName());
		nameText.setFont(CardUtils.beleren);
		nameText.setFill(nameColor);

		// Type de la carte
		final Text typeText = new Text(xText, yTypeStart, type);
		typeText.setFont(CardUtils.beleren_mini);
		typeText.setFill(typeColor);

		// Si la carte à des stats
		if (stat != null) {
			ImageView statImg;

			// Pour les planeswalker, marqueur de loyauté
			if (planes) {
				statImg = new ImageView(CardUtils.loyalty);
				statImg.setTranslateX(302);
				statImg.setTranslateY(465);

				statText = new Text(328, 493, stat);
				statText.setFill(Color.WHITE);

				getChildren().addAll(statImg);

			} else if (!stat.equals("")) {
				final CardSection s = switch (style) {
				case M21 -> CardSection.M21_STAT;
				case BACK -> CardSection.DF_STAT;
				case ADVENTURE -> CardSection.ADVENTURE_STAT;
				case ZENDIKAR -> CardSection.ZNR_STAT;
				case KALDHEIM -> CardSection.KHM_STAT;
				case BORDERLESS -> CardSection.BL_STAT;
				default -> CardSection.STAT;
				};

				if (isHybrid)
					statImg = CardUtils.getSection(ColorStyle.COLORLESS, s, artefact);
				else
					statImg = CardUtils.getSection(color, s, artefact);
				statImg.setTranslateX(285);
				statImg.setTranslateY(478);

				statText = new Text(305, 501, stat);
				statText.setFill(statColor);

				getChildren().addAll(statImg);
			} else
				statText = new Text(305, 501, "");

			statText.setFont(CardUtils.beleren);
			statText.setText(stat);
			getChildren().addAll(statText);
		}

		// Si c'est une carte d'aventure on déssine la sous carte
		if (adventure) {
			// On recup la carte aventure
			final Card adv = card.getBackCard();

			// On prepare le nom de la carte
			final Text nameAdv = new Text(xText, 345, adv.getName());
			nameAdv.setFont(CardUtils.beleren_mini);
			nameAdv.setFill(Color.WHITE);

			// On genere la phrase du type
			final String typeAdv = (adv.hasType(CardT.SORCERY) ? "Sorcery" : "Instant") + " - Adventure";

			// On prepare le type de la carte
			final Text typeAdvText = new Text(xText, 368, typeAdv);
			typeAdvText.setFont(CardUtils.beleren_mini);
			typeAdvText.setFill(Color.WHITE);

			// On déssine le cout de mana de la carte aventure
			drawMana(262, 110, 0.12f, adv.getCardCost());

			// On place la phrase d'effet de la carte en lui spécifiant les contraintes de
			// positions
			final TextBox text = new TextBox(adv.getOracle(), xText, 380, 130, 150, textColor);

			// On ajoute le tout et on sert
			getChildren().addAll(nameAdv, typeAdvText, text);
		}

		if (hasBack) {
			ImageView flag;
			ImageView arrowB;
			final Text flagText = new Text(30, 480, get(card.getBackCard().getType().get(0)));
			flagText.setFont(CardUtils.matrixBoldArtist);

			if (style == Style.BACK) {
				nameText.setFill(Color.WHITE);
				typeText.setFill(Color.WHITE);
				flag = CardUtils.getSection(color, CardSection.DF_FLAG_BACK, artefact);

				if (doubleCol)
					arrowB = CardUtils.getSection(CardUtils.ccolorToStyle(identity.get(1)), CardSection.DF_ARROW_BACK,
							artefact);
				else
					arrowB = CardUtils.getSection(color, CardSection.DF_ARROW_BACK, artefact);
			} else {
				flag = CardUtils.getSection(color, CardSection.DF_FLAG, artefact);
				if (doubleCol)
					arrowB = CardUtils.getSection(CardUtils.ccolorToStyle(identity.get(1)), CardSection.DF_ARROW,
							artefact);
				else
					arrowB = CardUtils.getSection(color, CardSection.DF_ARROW, artefact);
				flagText.setFill(Color.WHITE);
			}

			nameText.setTranslateX(26);

			arrowB.setTranslateX(10);
			arrowB.setTranslateY(23);

			flag.setTranslateX(15);
			flag.setTranslateY(466);

			getChildren().addAll(arrowB, flag, flagText);
		}

		// Artiste
		final Text artistText = new Text(70, 510, card.getArtist());
		artistText.setFill(Color.WHITE);
		artistText.setFont(CardUtils.matrixBoldArtist);

		getChildren().addAll(nameText, typeText, artistText);
	}

	public void drawMana(final int yStart, int xMana, final float scale, final ManaCost cost) {
		final int widthImg = (int) ((float) 165 * scale);

		for (final AbstractMana mana : cost.getListOfCosts()) {
			xMana -= widthImg;

			ImageView imgMana = null;
			if (mana instanceof Mana m) {
				final int number = m.getNumber();
				if (mana.getMana() == MCType.GENERIC)
					imgMana = new ImageView(CardUtils.getGeneric(number));
				else if (number > 1) {
					imgMana = new ImageView(CardUtils.getMana(mana.getMana()));
					// Si il y a 3 ile on dessine 3 fois l'image ile
					xMana -= widthImg;
					for (int i = 1; i < number; i++) {
						final ImageView imgMana2 = new ImageView(CardUtils.getMana(mana.getMana()));
						imgMana2.setTranslateX(xMana);
						imgMana2.setTranslateY(yStart - 10);

						getChildren().add(imgMana2);
					}
				} else
					imgMana = new ImageView(CardUtils.getMana(mana.getMana()));
			} else
				imgMana = new ImageView(CardUtils.getDoubleMana((DoubleMana) mana));

			imgMana.setScaleX(scale);
			imgMana.setScaleY(scale);

			imgMana.setTranslateX(xMana);
			imgMana.setTranslateY(yStart - 10);

			getChildren().add(imgMana);
		}
	}

	private String updateCard(final Card card) {
		// Les super types en premier
		final boolean isSnow = card.hasType(CardT.SNOW);
		type = card.hasType(CardT.TOKEN) ? "Token " : "";
		type += card.hasType(CardT.LEGENDARY) ? "Legendary " : "";
		type += card.hasType(CardT.BASIC) ? "Basic " : "";
		type += isSnow ? "Snow " : "";
		String stat = "";

		// On créer le libelle du type
		if (card.hasType(CardT.ARTIFACT)) {
			final boolean crea = card.hasType(CardT.CREATURE);
			type += (crea ? "Creature " : "") + "Artifact";

			final ArtifactT aType = card.getArtifactType();
			type += aType != null ? " — " + get(aType) : "";

			if (crea) {
				type += " — ";
				for (final CreatureT cType : card.getCreatureType())
					type += " " + get(cType);

				stat = card.getPower() + "/" + card.getToughness();
			}
		} else {
			switch (card.getType().get(0)) {
			case CREATURE -> {
				type += "Creature -";

				for (final CreatureT cType : card.getCreatureType())
					type += " " + get(cType);

				stat = card.getPower() + "/" + card.getToughness();
			}
			case PLANESWALKER -> {
				type += "Planeswalker - " + get(card.getPlaneswalker());
				stat = "" + card.getCounter(CounterType.LOYALTY);
			}
			case INSTANT -> type += "Instant";
			case SORCERY -> type += "Sorcery";
			case ENCHANTMENTS -> {
				final EnchantT eType = card.getEnchantmentType();
				type += "Enchantment" + (eType != null ? " — " + get(eType) : "");
			}
			case LAND -> {
				final ArrayList<LandT> lTypes = card.getLandTypes();
				if (lTypes != null) {
					type += "Land -";
					for (final LandT landType : card.getLandTypes())
						type += " " + get(landType);
				} else
					type += "Land";
			}
			default -> type += "Emblem - " + get(card.getPlaneswalker());
			}
		}

		return stat;
	}

	public void updateStat(final String str) {
		statText.setText(str);
	}
}
