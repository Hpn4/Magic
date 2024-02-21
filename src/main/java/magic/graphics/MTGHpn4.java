package magic.graphics;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.management.ObjectName;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import magic.graphics.card.CardToolTip;
import magic.graphics.card.CardUIFX;
import magic.graphics.utils.ImageLoader;
import magic.graphics.utils.Style;
import magic.graphics.utils.WebCardLoader;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.SpellT;
import magic.logic.card.State;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.utils.CardIOUtils;

public class MTGHpn4 extends Application {

	private int power;

	private int x;

	private CardToolTip cardToolTip;

	@Override
	public void start(final Stage primaryStage) throws Exception {
		final GridPane node = new GridPane();
		node.setPadding(new Insets(5));
		node.setHgap(5);
		node.setVgap(5);

		cardToolTip = new CardToolTip();
		cardToolTip.setVisible(false);

		x = 0;

		final ScrollPane parent = new ScrollPane();

		parent.setContent(node);

		// final int[] pos = set(node, "m20", 0, 0);
		// set(node, "tm20", pos[0], pos[1]);
		// new WebCardLoader("Animating Faerie", false, null).writeCard();
		// affiche(node, "eld/blue/Animating_Faerie/");
		download(node, "Ugin", "m21", Style.NORMAL, 0);

		// final FrameAssembler fra = new FrameAssembler(ColorStyle.GREEN,
		// ColorStyle.BLUE, CardSection.BLCARD, false);

		// node.add(new ImageView(fra.getFrame()), 0, 0);

		final Scene scene = new Scene(parent, 1400, 700, Color.WHITESMOKE);

		primaryStage.setTitle("Salut");
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.setOnKeyPressed(e1 -> {
			log();
		});

		node.getChildren().add(cardToolTip);
	}

	public void log() {
		try {
			final String histo = (String) ManagementFactory.getPlatformMBeanServer().invoke(
					new ObjectName("com.sun.management:type=DiagnosticCommand"), "gcClassHistogram",
					new Object[] { null }, new String[] { "[Ljava.lang.String;" });

			Files.writeString(Paths.get("test.log"), histo);
			System.out.println("log printed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void affiche(final GridPane node, final String card1) {
		final String path2 = "resources/card/" + card1;
		final Card front = CardIOUtils.readCard(path2 + "card.card");

		final CardUIFX card2 = new CardUIFX(front, ImageLoader.getImage(path2 + "card.jpg"));

		genereCard(card2);
		node.add(card2, 0, 0);

		if (front.isDoubleFaced() && front.getBackCard().getSpellType() != SpellT.ADVENTURE) {
			final CardUIFX card = new CardUIFX(front.getBackCard(), ImageLoader.getImage(path2 + "card_back.jpg"),
					Style.BACK);

			genereCard(card);
			node.add(card, 1, 0);
		}
	}

	public void download(final GridPane node, final String card, final String set, final Style style, final int who) {
		final WebCardLoader a = new WebCardLoader(card, set, who);
		final Card front = a.getCard();

		final CardUIFX card2 = new CardUIFX(front, a.getArtCrop(), style);

		genereCard(card2);
		node.add(card2, x++, 0);

		if (front.isDoubleFaced() && front.getBackCard().getSpellType() != SpellT.ADVENTURE) {
			final CardUIFX card1 = new CardUIFX(front.getBackCard(),
					ImageLoader.getImage("resources/card/" + front.getPath() + "/card_back.jpg"), Style.BACK);

			genereCard(card1);
			node.add(card1, x++, 0);
		}
	}

	public int[] set(final GridPane node, final String set, int x, int y) {
		String path = "resources/card/" + set + "/white/";
		final String[] fichier = new File(path).list();
		Arrays.sort(fichier);

		for (final String file : fichier) {
			if (!file.startsWith(".")) {
				final String path2 = path + file + "/";
				final Card front = CardIOUtils.readCard(path2 + "card.card");
				final CardUIFX card2 = new CardUIFX(front, ImageLoader.getImage(path2 + "card.jpg"));

				genereCard(card2);
				node.add(card2, x, y);
				x++;
				if (x >= 3) {
					x = 0;
					y++;
				}
			}
		}

		return new int[] { x, y };
	}

	public void genereCard(final CardUIFX card) {
		card.setOnMouseClicked(e -> {

			card.prevX = card.prevY = 0;
			if (card.getTargetable().hasType(CardT.LAND)) {
				final boolean turn = card.getTargetable().hasState(State.TAPPED);
				final RotateTransition rt = new RotateTransition(Duration.millis(300), card);
				rt.setByAngle(turn ? -90 : 90);

				final TranslateTransition tt = new TranslateTransition(Duration.millis(300), card);
				tt.setByX(turn ? -80 : 80);

				tt.play();
				rt.play();

				card.getTargetable().changeState(turn ? State.UNTAPPED : State.TAPPED);
			} else {
				power++;
				card.updateStat(power + "/" + card.getTargetable().getToughness());
				final ArrayList<ActivatedAbilities> act = card.getTargetable().getCardAbilities().getActivated();
				if (act != null) {

				}
			}
		});

		card.setOnMouseEntered(e -> {
			cardToolTip.setCard(card);
		});

		card.setOnMouseExited(e -> {
			cardToolTip.setVisible(false);
		});

		card.setOnMouseDragged(e -> {
			final int x = (int) (e.getSceneX()), y = (int) (e.getSceneY());
			if (card.prevX == 0) {
				card.prevX = x;
				card.prevY = y;
			}

			int dragX = x - card.prevX, dragY = y - card.prevY;

			card.prevX = x;
			card.prevY = y;

			card.setTranslateX(card.getTranslateX() + dragX);
			card.setTranslateY(card.getTranslateY() + dragY);

			cardToolTip.setTranslateX(cardToolTip.getTranslateX() + dragX);
			cardToolTip.setTranslateY(cardToolTip.getTranslateY() + dragY);
		});

		card.setOnZoom(e -> {
			final float scale = (float) (card.getScaleX() * e.getZoomFactor());

			card.setScaleX(scale);
			card.setScaleY(scale);
		});
	}

	public static void main(final String[] args) {
		launch();
	}

}
