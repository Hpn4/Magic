package magic.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Magic extends Application {
	
	public static int WIDTH;
	
	public static int HEIGHT;

	@Override
	public void start(final Stage stage) throws Exception {
		stage.setTitle("SALUT");
		
		WIDTH = 1400;
		HEIGHT = 750;

		final MagicLogic ml = new MagicLogic();

		List<Integer> a = Arrays.asList(1,2,3,4);

		// On initialise le server et le client
		final Scene scene = new Scene(ml.getGame().getGameView(), WIDTH, HEIGHT, Color.WHITESMOKE);
		//final Scene scene = new Scene(new Collection(), WIDTH, HEIGHT, Color.WHITESMOKE);

		stage.setOnCloseRequest(e -> {
			ml.getGame2().close();
		});


		stage.setScene(scene);
		stage.show();

		new Thread(ml).start();
	}

	public static void main(final String[] args) {
		launch();
	}
}
