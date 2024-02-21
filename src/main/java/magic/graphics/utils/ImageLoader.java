package magic.graphics.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javafx.scene.image.Image;

public class ImageLoader {

	private static final HashMap<String, Image> img;

	static {
		img = new HashMap<>();
	}

	public static Image getImage(final String path) {
		Image bufImg = img.get(path);

		if (bufImg == null) {
			if (!Files.exists(Paths.get(path)))
				System.err.println(path + " n'existe pas !");

			bufImg = new Image("file:" + path);
			img.put(path, bufImg);
		}

		return bufImg;
	}

	public static boolean contains(final String path) {
		return img.containsKey(path);
	}
}
