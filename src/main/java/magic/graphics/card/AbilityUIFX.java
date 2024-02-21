package magic.graphics.card;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import magic.graphics.utils.CardSection;
import magic.graphics.utils.ColorStyle;
import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.mana.AbstractMana;
import magic.logic.card.mana.DoubleMana;

public class AbilityUIFX extends ObjectUIFX<Abilities> {

	public AbilityUIFX(final Card card, final Image img, final Abilities ab, final String type) {
		super(ab);

		createUI(card, img, ab.getOracle(), type);
	}

	public AbilityUIFX(final Card card, final Image img, final String oracle, final String type) {
		super(null);

		createUI(card, img, oracle, type);
	}

	private void createUI(final Card card, final Image img, final String oracle, final String type) {
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
		int yNameStart = 40;
		int yTypeStart = 314;

		// Les capacités
		final boolean doubleCol = identity == null ? false : identity.size() == 2;
		final boolean artefact = card.hasType(CardT.ARTIFACT);

		ImageView frame;

		final ImageView cropImg = new ImageView(img);

		if (doubleCol)
			frame = CardUtils.getDoubleSection(identity, CardSection.FRAME, isHybrid, artefact);
		else
			frame = CardUtils.getSection(color, CardSection.FRAME, artefact);

		cropImg.setFitWidth(312);
		cropImg.setFitHeight(232);

		cropImg.setTranslateX(30);
		cropImg.setTranslateY(60);

		getChildren().addAll(frame, cropImg);

		// Text
		final int xText = 30, yText = 345;

		// Le text d'effet de la carte. La position diffère en fonction du type de la
		// carte
		if (oracle != null)
			getChildren().add(new TextBox(oracle, xText + 5, yText - 10, 310, 140, Color.BLACK));

		// Nom de la carte
		final Text nameText = new Text(xText, yNameStart + 8, card.getName());
		nameText.setFont(CardUtils.beleren);
		nameText.setFill(Color.BLACK);

		// Type de la carte
		final Text typeText = new Text(xText, yTypeStart, type);
		typeText.setFont(CardUtils.beleren_mini);
		typeText.setFill(Color.BLACK);

		getChildren().addAll(nameText, typeText);
	}

}
