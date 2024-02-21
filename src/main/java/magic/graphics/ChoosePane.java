package magic.graphics;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import magic.graphics.card.AbilityUIFX;
import magic.graphics.card.CardUIFX;
import magic.graphics.card.ObjectUIFX;
import magic.graphics.utils.ImageLoader;
import magic.logic.card.Card;
import magic.logic.card.Targetable;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.game.Game;
import magic.logic.utils.Utils;
import magic.main.Magic;

public class ChoosePane extends BorderPane {

	private Button submit;

	private Button cancel;

	private Button viewBattlefield;

	private Text info;

	private Parent view;

	private final ArrayList<Effect> effectsChoosed;

	private final ArrayList<Targetable> choosedTargets;

	private Abilities choosedAbility;

	private Owner choosedPlayer;

	public ChoosePane(final Parent view) {
		this.view = view;
		setStyle("-fx-background-color: rgba(0, 0, 0, 0.65);");

		info = new Text();
		info.setFill(Color.WHITESMOKE);
		info.setFont(Font.loadFont("file:resources/font/beleren.ttf", 50));

		effectsChoosed = new ArrayList<>();
		choosedTargets = new ArrayList<>();

		viewBattlefield = Stack.createBut("View Battlefield", Color.ORANGE);
		viewBattlefield.setVisible(false);
		viewBattlefield.setLayoutX(50);
		viewBattlefield.setLayoutY(50);

		submit = Stack.createBut("Submit", Color.ORANGE);

		submit.setOnAction(e -> {
			Platform.exitNestedEventLoop("CHOOSE", null);
		});
		submit.setDisable(true);

		viewBattlefield.setOnAction(e -> {
			if (isVisible()) {
				setVisible(false);
				view.setEffect(null);
				viewBattlefield.setText("View Browser");
			} else {
				setVisible(true);
				view.setEffect(new GaussianBlur());
				viewBattlefield.setText("View Battlefield");
			}
		});

		cancel = Stack.createBut("Cancel", Color.BLUE);

		final TilePane pane = new TilePane(submit, cancel);
		pane.setHgap(50);

		pane.setAlignment(Pos.TOP_CENTER);
		pane.setPadding(new Insets(50));
		setBottom(pane);

		BorderPane.setMargin(info, new Insets(50));
		BorderPane.setAlignment(info, Pos.CENTER);
		setTop(info);

		setVisible(false);

		setPrefWidth(Magic.WIDTH);
		setPrefHeight(Magic.HEIGHT);
	}

	public <T extends Enum<T>> T chooseEnumFrom(final Enum<T>... enums) {
		setVisible(true);
		submit.setDisable(false);
		viewBattlefield.setVisible(true);

		info.setText("Choose a label");

		final VBox box = new VBox(5);
		box.setMaxWidth(600);

		// La liste observable contenant tout les elements de l'énumeration
		final ObservableList<Enum<T>> data = FXCollections.observableArrayList();
		data.addAll(enums);
		
		final FilteredList<Enum<T>> filtered = new FilteredList<>(data, s -> true);

		// Le champ de text qui va filtrer les résultats
		final TextField search = new TextField();
		search.setPromptText("Search here");
		search.textProperty().addListener((obs, oldValue, newValue) -> {
			filtered.setPredicate(p -> p.name().toLowerCase().contains(newValue.toLowerCase().trim()));
		});

		// Le tableau contenant tout les types
		final TableView<Enum<T>> table = new TableView<>();
		
		// La colonne avec les info
		final TableColumn<Enum<T>, String> column = new TableColumn<>(enums[0].getClass().getCanonicalName());
	    column.setCellValueFactory(new PropertyValueFactory<>("name"));
	    column.setMinWidth(600);
	    
	    table.getColumns().clear();
	    table.getColumns().add(column);
	    
		table.setItems(filtered);
		table.getStylesheets().add("file:resources/style/tableView.css");
		
		box.getChildren().addAll(search, table);
		box.setAlignment(Pos.CENTER);
		setCenter(box);

		view.setEffect(new GaussianBlur());
		Platform.enterNestedEventLoop("CHOOSE");

		view.setEffect(null);
		setVisible(false);
		viewBattlefield.setVisible(false);
		submit.setDisable(true);

		return (T) table.getSelectionModel().getSelectedItem();
	}

	/**
	 * Permet à l'utilisateur de choisir le joueur (en raison de la cible d'un sort
	 * ou autre
	 * 
	 * @param game Le jeu contenant les informations
	 * @param card La carte qui lance cette effet
	 * 
	 * @return Le joueur choisie
	 */
	public Owner choosePlayer(final Game game, final Card card) {
		choosedPlayer = null;
		submit.setVisible(false);
		cancel.setVisible(false);
		setVisible(true);
		viewBattlefield.setVisible(true);

		info.setText("Choose a player");

		final TilePane pane = new TilePane();

		// Le bouton nous
		final Button you = Stack.createBut("You", Color.ORANGE);
		you.setOnAction(e -> {
			choosedPlayer = Owner.YOU;
			Platform.exitNestedEventLoop("CHOOSE", null);
		});

		// Le bouton pour selectionner l'adversaire
		final Button opp = Stack.createBut("Opponent", Color.BLUE);
		opp.setOnAction(e -> {
			choosedPlayer = Owner.OPPONENT;
			Platform.exitNestedEventLoop("CHOOSE", null);
		});

		// On ajoute tout et on centre
		pane.getChildren().addAll(you, opp);
		pane.setAlignment(Pos.CENTER);
		setCenter(pane);

		// On met un effet de floue et on entre dans la boucle inifine
		view.setEffect(new GaussianBlur());
		Platform.enterNestedEventLoop("CHOOSE");

		// On réinitialise tout les etats
		view.setEffect(null);
		setVisible(false);
		viewBattlefield.setVisible(false);
		submit.setVisible(true);
		cancel.setVisible(true);

		// On renvoie en faisanr une conversion
		return game.ownerToPlayer(choosedPlayer, card);
	}

	public Abilities chooseAb(final Abilities... abilities) {
		choosedAbility = null;
		setVisible(true);
		viewBattlefield.setVisible(true);
		cancel.setDisable(false);

		cancel.setOnAction(e -> {
			Platform.exitNestedEventLoop("CHOOSE", null);
			choosedAbility = null;
		});

		info.setText("Choose One");

		final HBox box = new HBox(10);

		final Card card = abilities[0].getParent();
		final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
		for (final Abilities ab : abilities) {
			final AbilityUIFX ui = new AbilityUIFX(card, img, ab.getOracle(), "Abilities");

			ui.setPrefWidth(CardUIFX.CARD_WIDTH * 0.8f);
			ui.setScaleX(0.75f);
			ui.setScaleY(0.75f);

			final DropShadow s = new DropShadow(10, Color.CYAN);
			s.setOffsetX(0);
			s.setOffsetY(0);
			s.setHeight(70);
			s.setWidth(70);

			ui.setEffect(s);

			ui.setOnMouseClicked(e -> {
				ui.setSelected(!ui.isSelected());

				if (ui.isSelected()) {
					choosedAbility = ab;

					final DropShadow shadow = new DropShadow(10, Color.YELLOW);
					shadow.setOffsetX(0);
					shadow.setOffsetY(0);
					shadow.setHeight(70);
					shadow.setWidth(70);

					ui.setEffect(shadow);

				} else {
					choosedAbility = null;
					ui.setEffect(s);
				}

				final int nmb = effectsChoosed.size();
				if (nmb == 1)
					submit.setDisable(false);
				else
					submit.setDisable(true);

				submit.setText("Submit " + nmb);
			});

			ui.setOnMouseEntered(e -> {
				ui.setScaleX(0.85f);
				ui.setScaleY(0.85f);
			});

			ui.setOnMouseExited(e -> {
				ui.setScaleX(0.75f);
				ui.setScaleY(0.75f);
			});
		}

		box.setAlignment(Pos.CENTER);
		setCenter(box);

		view.setEffect(new GaussianBlur());
		Platform.enterNestedEventLoop("CHOOSE");

		view.setEffect(null);
		setVisible(false);
		viewBattlefield.setVisible(false);
		cancel.setDisable(true);

		return choosedAbility;
	}

	public Effect[] chooseModal(final int min, final int max, final Abilities ab, final Effect... effects) {
		setVisible(true);
		viewBattlefield.setVisible(true);
		effectsChoosed.clear();

		if (min == max)
			info.setText("Choose " + Utils.numberToWord(min) + " effect" + ((min > 1) ? "s" : ""));
		else
			info.setText("Choose " + Utils.numberToWord(min) + " up to " + Utils.numberToWord(max) + " effects");

		final HBox box = new HBox(5);

		final Card card = ab.getParent();
		final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
		final String[] texts = ab.getOracle().split("\n");
		for (int i = 0; i < effects.length; i++) {
			final AbilityUIFX ui = new AbilityUIFX(card, img, texts[i + 1].substring(1), "Modal");
			final Effect ef = effects[i];

			ui.setPrefWidth(CardUIFX.CARD_WIDTH * 0.8f);
			ui.setScaleX(0.75f);
			ui.setScaleY(0.75f);

			final DropShadow s = new DropShadow(10, Color.CYAN);
			s.setOffsetX(0);
			s.setOffsetY(0);
			s.setHeight(70);
			s.setWidth(70);

			ui.setEffect(s);

			ui.setOnMouseClicked(e -> {
				ui.setSelected(!ui.isSelected());

				if (ui.isSelected()) {
					effectsChoosed.add(ef);

					final DropShadow shadow = new DropShadow(10, Color.YELLOW);
					shadow.setOffsetX(0);
					shadow.setOffsetY(0);
					shadow.setHeight(70);
					shadow.setWidth(70);

					ui.setEffect(shadow);

				} else {
					effectsChoosed.remove(ef);
					ui.setEffect(s);
				}

				final int nmb = effectsChoosed.size();
				if (nmb >= min && nmb <= max)
					submit.setDisable(false);
				else
					submit.setDisable(true);

				submit.setText("Submit " + nmb);
			});

			ui.setOnMouseEntered(e -> {
				ui.setScaleX(0.85f);
				ui.setScaleY(0.85f);
			});

			ui.setOnMouseExited(e -> {
				ui.setScaleX(0.75f);
				ui.setScaleY(0.75f);
			});

			box.getChildren().add(ui);
		}

		box.setAlignment(Pos.CENTER);
		setCenter(box);

		view.setEffect(new GaussianBlur());
		Platform.enterNestedEventLoop("CHOOSE");
		view.setEffect(null);
		setVisible(false);
		viewBattlefield.setVisible(false);

		return effectsChoosed.toArray(Effect[]::new);
	}

	public <A extends Targetable> List<Targetable> chooseTargets(final int min, final int max, final List<A> targets) {
		setVisible(true);
		viewBattlefield.setVisible(true);
		choosedTargets.clear();

		if (min == max)
			info.setText("Choose " + Utils.numberToWord(min) + " target" + ((min > 1) ? "s" : ""));
		else
			info.setText("Choose " + Utils.numberToWord(min) + " up to " + Utils.numberToWord(max) + " targets");

		final ScrollPane scroll = new ScrollPane();
		final HBox box = new HBox(5);

		for (final Targetable target : targets) {
			final ObjectUIFX<?> ui;
			if (target instanceof Card card) {
				final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
				ui = new CardUIFX(card, img);
			} else {
				final Abilities ab = (Abilities) target;
				final Card card = ab.getParent();
				final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
				ui = new AbilityUIFX(card, img, ab.getOracle(), "Ability");
			}

			ui.setPrefWidth(CardUIFX.CARD_WIDTH * 0.8f);
			ui.setScaleX(0.75f);
			ui.setScaleY(0.75f);
			ui.setTranslateY(-25);

			final DropShadow s = new DropShadow(10, Color.CYAN);
			s.setOffsetX(0);
			s.setOffsetY(0);
			s.setHeight(70);
			s.setWidth(70);

			ui.setEffect(s);

			ui.setOnMouseClicked(e -> {
				ui.setSelected(!ui.isSelected());

				if (ui.isSelected()) {
					choosedTargets.add(target);

					final DropShadow shadow = new DropShadow(10, Color.YELLOW);
					shadow.setOffsetX(0);
					shadow.setOffsetY(0);
					shadow.setHeight(70);
					shadow.setWidth(70);

					ui.setEffect(shadow);

				} else {
					choosedTargets.remove(target);
					ui.setEffect(s);
				}

				final int nmb = choosedTargets.size();
				if (nmb >= min && nmb <= max)
					submit.setDisable(false);
				else
					submit.setDisable(true);

				submit.setText("Submit " + nmb);
			});

			ui.setOnMouseEntered(e -> {
				ui.setScaleX(0.85f);
				ui.setScaleY(0.85f);
			});

			ui.setOnMouseExited(e -> {
				ui.setScaleX(0.75f);
				ui.setScaleY(0.75f);
			});

			box.getChildren().add(ui);
		}

		scroll.setContent(box);
		scroll.setPrefHeight(CardUIFX.CARD_HEIGHT + 200);
		scroll.setPrefViewportHeight(CardUIFX.CARD_HEIGHT + 200);
		box.setPrefHeight(CardUIFX.CARD_HEIGHT - 90);
		scroll.getStylesheets().add("file:resources/style/scroll.css");
		setCenter(scroll);

		view.setEffect(new GaussianBlur());
		Platform.enterNestedEventLoop("CHOOSE");
		view.setEffect(null);
		setVisible(false);
		viewBattlefield.setVisible(false);

		return choosedTargets;
	}

	public Targetable[] chooseOrder(final String gauche, final String droite, final Targetable... targets) {
		setVisible(true);
		viewBattlefield.setVisible(true);

		info.setText(gauche + " - Choose order - " + droite);

		final Pane pane = new Pane();
		for (final Targetable target : targets) {
			final ObjectUIFX<?> ui;
			if (target instanceof Card card) {
				final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
				ui = new CardUIFX(card, img);
			} else {
				final Abilities ab = (Abilities) target;
				final Card card = ab.getParent();
				final Image img = ImageLoader.getImage("resources/card/" + card.getPath() + "/card.jpg");
				ui = new AbilityUIFX(card, img, ab, "Ability");
			}

			ui.setPrefWidth(CardUIFX.CARD_WIDTH * 0.8f);
			ui.setScaleX(0.75f);
			ui.setScaleY(0.75f);
			ui.setTranslateY(-25);

			ui.setOnMouseDragged(e -> {
				final int x = (int) (e.getSceneX()), y = (int) (e.getSceneY());
				if (ui.prevX == 0) {
					ui.prevX = x;
					ui.prevY = y;
				}

				int dragX = x - ui.prevX, dragY = y - ui.prevY;

				ui.prevX = x;
				ui.prevY = y;

				ui.setTranslateX(ui.getTranslateX() + dragX);
				ui.setTranslateY(ui.getTranslateY() + dragY);
			});

			ui.setOnMouseClicked(e -> {
				ui.prevX = ui.prevY = 0;
			});

			ui.setOnKeyReleased(e -> {
				final int x = (int) ui.getTranslateX();

				pane.getChildren().remove(ui);
				for (int i = 0; i < pane.getChildren().size(); i++) {
					final Node node = pane.getChildren().get(i);

					if (node.getTranslateX() > x) {
						pane.getChildren().add(i, ui);
						break;
					}
				}

				int wid = 0;
				for (final Node node : pane.getChildren()) {
					node.setTranslateX(wid);
					wid += 100;
				}
			});

			pane.getChildren().add(ui);
		}

		int wid = 0;
		for (final Node node : pane.getChildren()) {
			node.setTranslateX(wid);
			wid += 100;
		}

		view.setEffect(new GaussianBlur());
		Platform.enterNestedEventLoop("CHOOSE");
		view.setEffect(null);
		setVisible(false);
		viewBattlefield.setVisible(false);

		final Targetable[] targs = new Targetable[pane.getChildren().size()];
		for (int i = 0; i < pane.getChildren().size(); i++) {
			final Node node = pane.getChildren().get(i);
			targs[i] = ((ObjectUIFX<?>) node).getTargetable();
		}

		return targs;
	}

	public void showCard(final String name, final ArrayList<CardUIFX> nodes, final StackPane node) {
		setVisible(true);
		viewBattlefield.setVisible(true);
		submit.setDisable(true);
		view.setEffect(new GaussianBlur());

		info.setText(name);

		if (node != null)
			node.getChildren().clear();

		final ScrollPane scroll = new ScrollPane();
		final HBox box = new HBox(5);

		for (final CardUIFX ui : nodes) {
			ui.prevAngle = (float) ui.getRotate();
			ui.prevScale = (float) ui.getScaleX();
			ui.prevX = (int) ui.getTranslateX();
			ui.prevY = (int) ui.getTranslateY();

			ui.setPrefWidth(CardUIFX.CARD_WIDTH * 0.8f);
			ui.setScaleX(0.75f);
			ui.setScaleY(0.75f);
			ui.setTranslateY(-25);
			ui.setTranslateX(0);

			final DropShadow s = new DropShadow(10, Color.YELLOW);
			s.setOffsetX(0);
			s.setOffsetY(0);
			s.setHeight(70);
			s.setWidth(70);

			ui.setEffect(s);

			ui.setOnMouseEntered(e -> {
				ui.setScaleX(0.85f);
				ui.setScaleY(0.85f);
			});

			ui.setOnMouseExited(e -> {
				ui.setScaleX(0.75f);
				ui.setScaleY(0.75f);
			});

			box.getChildren().add(ui);
		}

		scroll.setContent(box);
		scroll.setPrefHeight(CardUIFX.CARD_HEIGHT + 200);
		scroll.setPrefViewportHeight(CardUIFX.CARD_HEIGHT + 200);
		box.setPrefHeight(CardUIFX.CARD_HEIGHT - 90);
		scroll.getStylesheets().add("file:resources/style/scroll.css");
		setCenter(scroll);

		cancel.setDisable(false);
		cancel.setOnAction(e -> {
			viewBattlefield.setVisible(false);
			setVisible(false);
			submit.setDisable(false);
			cancel.setDisable(true);

			for (final CardUIFX card : nodes) {
				// On réinitialise tout
				card.setScaleX(card.prevScale);
				card.setScaleY(card.prevScale);

				card.setRotate(card.prevAngle);

				card.setPrefWidth(CardUIFX.CARD_WIDTH * card.prevScale);
				card.setPrefHeight(CardUIFX.CARD_HEIGHT * card.prevScale);

				card.setTranslateX(card.prevX);
				card.setTranslateY(card.prevY);

				card.setEffect(null);

				card.setOnMouseClicked(null);
				card.setOnMouseEntered(null);
				card.setOnMouseExited(null);

				node.getChildren().add(card);
			}

			view.setEffect(null);
		});
	}

	public Button getViewB() {
		return viewBattlefield;
	}
}
