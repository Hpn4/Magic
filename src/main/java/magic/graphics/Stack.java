package magic.graphics;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import magic.graphics.card.AbilityUIFX;
import magic.graphics.card.CardUIFX;
import magic.graphics.card.CardUtils;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.Card;
import magic.logic.card.abilities.Abilities;
import magic.logic.utils.Counterable;

public class Stack extends GridPane {

	private long startId;

	private final ArrayList<Long> ids;

	private final StackPane stack;

	private final Button resolve;

	/**
	 * Le containeur pour le choix du X
	 */
	private final HBox chooseX;

	private final Button xValue;

	private int x;

	/**
	 * Les different controlleur pour les cartes ou il faut choisir : "choissiez
	 * pair ou impaire / choissisez land ou non land.."
	 */
	private final TilePane chooseOpt;

	private final Button firstOption;

	private final Button secondOption;

	private boolean isFirstOption;

	/**
	 * Pour les cartes avec un effet "may"
	 */
	private final TilePane mayPane;

	private final Button accept;

	private boolean may;

	public static Button createBut(final String txt, final Color color) {
		final Button button = new Button(txt);
		button.getStylesheets().add("file:resources/style/button" + (color == Color.BLUE ? "_blue.css" : ".css"));
		button.setFont(CardUtils.beleren);

		final DropShadow shadow = new DropShadow(10, color);
		shadow.setOffsetX(0);
		shadow.setOffsetY(0);
		shadow.setHeight(50);
		shadow.setWidth(50);

		button.setEffect(shadow);

		return button;
	}

	public Stack() {
		startId = 0;
		ids = new ArrayList<>();

		stack = new StackPane();

		/*
		 ************************************
		 ************ MAY EFFECT ************
		 ************************************
		 */
		mayPane = new TilePane();

		accept = createBut("Accept", Color.ORANGE);

		// Quand on click sur accepter, on précise que l'utilisateur à choisie oui et
		// on sort de la boucle
		accept.setOnAction(e -> {
			may = true;
			Platform.exitNestedEventLoop("MAY", null);
		});

		final Button decline = createBut("Decline", Color.BLUE);

		decline.setOnAction(e -> {
			may = false;
			Platform.exitNestedEventLoop("MAY", null);
		});

		mayPane.getChildren().addAll(accept, decline);
		mayPane.setVisible(false);

		/*
		 ***************************************
		 **** Le panneau pour le choix du X ****
		 ***************************************
		 */
		chooseX = new HBox(0);

		xValue = Stack.createBut("X = 0", Color.ORANGE);
		xValue.setOnAction(e -> {
			Platform.exitNestedEventLoop("CHOOSE_X", null);
		});
		xValue.setPrefWidth(140);

		final Button down = new Button("-");
		down.getStylesheets().add("file:resources/style/button_X_down.css");
		down.setOnAction(e -> {
			if (x > 0)
				xValue.setText("X = " + --x);
		});

		final Button up = new Button("+");
		up.getStylesheets().add("file:resources/style/button_X_up.css");
		up.setOnAction(e -> {
			xValue.setText("X = " + ++x);
		});

		chooseX.getChildren().addAll(down, xValue, up);
		chooseX.setVisible(false);

		/*
		 *********************************************************
		 **** Le panneau pour les effets à choix deux options ****
		 *********************************************************
		 */
		chooseOpt = new TilePane();
		chooseOpt.setHgap(20);

		firstOption = Stack.createBut("First Opt", Color.ORANGE);
		firstOption.setOnAction(e -> {
			isFirstOption = true;
			Platform.exitNestedEventLoop("CHOOSE_OPT", null);
		});

		secondOption = Stack.createBut("Second Opt", Color.BLUE);
		secondOption.setOnAction(e -> {
			isFirstOption = false;
			Platform.exitNestedEventLoop("CHOOSE_OPT", null);
		});

		chooseOpt.getChildren().addAll(firstOption, secondOption);
		chooseOpt.setVisible(false);

		// Bouton de résolution de l'objet
		resolve = createBut("Resolve", Color.ORANGE);

		setVisible(false);

		resolve.setOnAction(e -> {
			final long id = ids.remove(ids.size() - 1); // Pop
			Platform.exitNestedEventLoop(id, null); // On laisse l'action s'exécuter
			popStack();
		});

		setVgap(10);
		add(stack, 0, 0);

		final StackPane buttonPane = new StackPane(mayPane, resolve, chooseX, chooseOpt);
		resolve.setAlignment(Pos.CENTER_LEFT);
		mayPane.setAlignment(Pos.CENTER);
		chooseX.setAlignment(Pos.CENTER);
		chooseOpt.setAlignment(Pos.CENTER);

		mayPane.setPrefWidth(CardUIFX.CARD_WIDTH);
		chooseX.setPrefWidth(CardUIFX.CARD_WIDTH);
		chooseOpt.setPrefWidth(CardUIFX.CARD_WIDTH);
		add(buttonPane, 0, 1);

		prefWidth(CardUIFX.CARD_WIDTH + 10);
		prefHeight(CardUIFX.CARD_HEIGHT + 50);
	}

	public void addOnStack(final Counterable object) {
		if (object instanceof Card card) {
			final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
			stack.getChildren().add(new CardUIFX(card, img));
		} else if (object instanceof Abilities ab) {
			final Card card = ab.getParent();
			final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
			stack.getChildren().add(new AbilityUIFX(card, img, ab.getOracle(), "Ability"));
		}

		// La position des cartes en mains
		final int size = stack.getChildren().size();

		int begAngle = -(size * 4);
		for (final Node node : stack.getChildren()) {
			begAngle += 4;
			node.setRotate(begAngle);
		}

		setVisible(true);
	}

	public boolean may(final magic.logic.card.abilities.effect.Effect effect, final Abilities a) {
		// On met la capacité sur le stack pour que l'utilisateur sache de quel effet
		// on parle
		addOnStack(a);
		accept.setText(effect.toString()); // On met une petite phrase pour que ce soit plus parlant

		resolve.setVisible(false);
		mayPane.setVisible(true);

		// On entre dans la boucle infinie et on attend que l'utilisateur appuie sur un
		// des boutons
		Platform.enterNestedEventLoop("MAY");
		mayPane.setVisible(false);
		resolve.setVisible(true);

		// On vire la carte
		popStack();

		return may; // On renvoie l'était qui a était définie par les boutons accept et decline
	}

	public int chooseX(final Counterable target) {
		x = 0;
		xValue.setText("X = 0");
		addOnStack(target);

		resolve.setVisible(false);
		chooseX.setVisible(true);

		Platform.enterNestedEventLoop("CHOOSE_X");
		chooseX.setVisible(false);
		resolve.setVisible(true);

		// On vire la carte
		popStack();

		return x;
	}

	public boolean chooseOpt(final String first, final String second, final Counterable object) {
		isFirstOption = false;
		addOnStack(object);

		firstOption.setText(first);
		secondOption.setText(second);

		resolve.setVisible(false);
		chooseOpt.setVisible(true);
		Platform.enterNestedEventLoop("CHOOSE_OPT");

		chooseOpt.setVisible(false);
		resolve.setVisible(true);

		// On vire l'effet du stack
		popStack();

		return isFirstOption;
	}

	public Object pauseStack() {
		final long id = startId++;
		ids.add(id);
		return Platform.enterNestedEventLoop(id);
	}

	public void popStack() {
		final Node n = stack.getChildren().remove(stack.getChildren().size() - 1);
		n.setVisible(true);

		// La position des cartes en mains
		final int size = stack.getChildren().size();

		int begAngle = -(size);
		for (final Node node : stack.getChildren()) {
			node.setRotate(begAngle);
			begAngle += 4;
		}

		if (size == 0)
			setVisible(false);

	}

	public void counter(final int index) {
		final Node n = stack.getChildren().remove(index);
		n.setVisible(true);

		// La position des cartes en mains
		final int size = stack.getChildren().size();

		int begAngle = -(size);
		for (final Node node : stack.getChildren()) {
			node.setRotate(begAngle);
			begAngle += 4;
		}

		if (size == 0)
			setVisible(false);

		final long id = ids.remove(ids.size() - 1); // Pop
		Platform.exitNestedEventLoop(id, "countered"); // On laisse l'action s'exécuter
	}

}
