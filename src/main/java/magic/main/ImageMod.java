package magic.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageMod {

	public ImageMod() {
		final String[] imgs = {"a", "b", "c", "g", "m", "r", "u", "w"};
		
		try {
		for(final String img : imgs) {
			final BufferedImage first = ImageIO.read(new File("resources/image/zendikar/back/" + img + "card.png"));
			final BufferedImage second = ImageIO.read(new File("resources/image/zendikar/" + img + "card.png"));
			
			first.getGraphics().drawImage(second, 0, 0, null);
			
			ImageIO.write(first, "PNG", new File("resources/image/zendikar/final/" + img + ".png"));
		}
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(final String[] args) {
		new ImageMod();
	}
}
