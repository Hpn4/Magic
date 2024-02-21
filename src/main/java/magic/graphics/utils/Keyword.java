package magic.graphics.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.image.Image;

public class Keyword {

	private static HashMap<String, String> keywords;

	private static HashMap<String, Image> images;

	public static void init() {
		keywords = new HashMap<>();
		images = new HashMap<>();

		try {
			final ArrayList<String> lines = new ArrayList<>();
			Files.lines(Paths.get("resources/keyword/keywords")).forEach(lines::add);

			boolean in = false;
			String key = "";
			for (final String line : lines) {
				if (line.startsWith("keyword:")) {
					in = true;
					continue;
				}

				if (in && line.contains("\tkeyword:"))
					key = line.substring(line.indexOf(":") + 2);

				if (in && line.contains("reminder:")) {
					in = false;
					// System.out.println(key + " " + line);
					final String value = line.substring(line.indexOf(":") + 2);
					keywords.put(key, value);
				}
			}
		} catch (final IOException e) {

		}
	}

	public static String getKeyword(final String keyword) {
		if (keywords == null)
			init();
		return keywords.get(keyword);
	}

	public static Image getImage(final String keyword) {
		Image img = images.get(keyword);

		if (img == null) {
			final String path = "resources/keyword/image/" + keyword.toLowerCase() + ".png";
			if (Files.exists(Paths.get(path))) {
				img = new Image("file:" + path);
				images.put(keyword, img);
			}
		}

		return img;
	}
}
