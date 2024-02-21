package magic.graphics.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class FrameAssembler {

	// https://magicseteditor.boards.net/thread/77/cajun-style-templates-exporters-fixes?page=25
	private WritableImage frame;

	public FrameAssembler(final ColorStyle col1, ColorStyle col2, final CardSection sect, final boolean isHybrid) {
		try {
			final BufferedImage first = ImageIO
					.read(new File("resources/image/card/" + col1.name().toLowerCase() + "/" + sect.getPath()));

			final BufferedImage second = ImageIO
					.read(new File("resources/image/card/" + col2.name().toLowerCase() + "/" + sect.getPath()));

			switch (sect) {
			// Les cartes au format basique
			case FRAME -> {
				if (isHybrid)
					frame = assemble(first, second, "basic/hybrid.png", "colorless/basic.jpg");
				else
					frame = assemble(first, second, "basic/multi.png", "multicolored/basic.jpg");
			}
			
			case PLANES -> {
				if (isHybrid)
					frame = assemble(first, second, "planeswalker/hybrid.png", "colorless/planeswalker/card.png");
				else
					frame = assemble(first, second, "planeswalker/multi.png", "multicolored/planeswalker/card.png");
			}
			
			case PLANES1 -> {
				if (isHybrid)
					frame = assemble(first, second, "planeswalker/hybrid1.png", "colorless/planeswalker/card1.png");
				else
					frame = assemble(first, second, "planeswalker/multi1.png", "multicolored/planeswalker/card1.png");
			}
			
			case PLANES_STAMP -> {
				frame = assemble(first, second, "planeswalker/stamp.jpg", null, 0, 46, 1.0f / 46.0f);
			}

			case SNOW -> {
				if (isHybrid)
					frame = assemble(first, second, "basic/hybrid.png", "colorless/snow/basic.jpg");
				else
					frame = assemble(first, second, "basic/multi.png", "multicolored/snow/basic.jpg");
			}

			// Les cartes des archives mystiques
			case MYSTICAL -> frame = assemble(first, second, "mystical/multi.png", "multicolored/mystical.png");

			// Zendikar rising showcase
			case ZNR_FRAME -> {
				if (isHybrid)
					frame = assemble(first, second, "zendikar/hybrid.png", "colorless/zendikar/card.png");
				else
					frame = assemble(first, second, "zendikar/multi.png", "multicolored/zendikar/card.png");
			}

			// Kaldheim showcase
			case KHM_FRAME -> {
				if (isHybrid)
					frame = assemble(first, second, "basic/hybrid.png", "colorless/kaldheim/card.png");
				else
					frame = assemble(first, second, "kaldheim/multi.png", "multicolored/kaldheim/card.png");
			}

			case PROMO -> {
				if (isHybrid)
					frame = assemble(first, second, "basic/hybrid.png", "colorless/promo.png");
				else
					frame = assemble(first, second, "basic/multi.png", "multicolored/promo.png");
			}

			// Borderless card
			case BL_CARD -> {
				if (isHybrid)
					frame = assemble(first, second, "borderless/hybrid.png", "colorless/borderless/card.png");
				else
					frame = assemble(first, second, "borderless/multi.png", "multicolored/borderless/card.png");
			}

			// Borderless card avec beaucoup de texte
			case BL_TALL -> {
				if (isHybrid)
					frame = assemble(first, second, "borderless/hybrid_tall.png", "colorless/borderless/tall.png");
				else
					frame = assemble(first, second, "borderless/multi_tall.png", "multicolored/borderless/tall.png");
			}

			// Les terrains à double couleur
			case SNOW_LAND -> frame = assemble(first, second, "basic/multi.png", "colorless/snow/land.jpg");
			case LAND_FRAME -> frame = assemble(first, second, "basic/multi.png", "colorless/land.jpg");

			// Carte qui se retourne de face
			case DF -> {
				if (isHybrid)
					frame = assemble(first, second, "double_faced/hybrid.png", "colorless/double_faced/card.jpg");
				else
					frame = assemble(first, second, "double_faced/multi.png", "multicolored/double_faced/card.jpg");
			}

			// Carte qui se retourne d'inistrad de face
			case DF_NOTCHED -> {
				if (isHybrid)
					frame = assemble(first, second, "double_faced/hybrid.png", "colorless/double_faced/notched.jpg");
				else
					frame = assemble(first, second, "double_faced/multi_notched.png",
							"multicolored/double_faced/notched.jpg");
			}

			// Carte qui se tourne dos
			case DF_BACK -> {
				if (isHybrid)
					frame = assemble(first, second, "double_faced/hybrid.png", "colorless/double_faced/back.jpg");
				else
					frame = assemble(first, second, "double_faced/multi.png", "multicolored/double_faced/back.jpg");
			}

			// Carte qui se retourne terrain
			case DF_LAND -> frame = assemble(first, second, "double_faced/multi.png",
					"colorless/double_faced/land.jpg");
			case DF_LAND_NOTCHED -> frame = assemble(first, second, "double_faced/multi_notched.png",
					"colorless/double_faced/land_notched.jpg");
			case DF_LAND_BACK -> frame = assemble(first, second, "double_faced/multi.png",
					"colorless/double_faced/land_back.jpg");

			// Les cartes aventures
			case ADVENTURE -> {
				if (isHybrid)
					frame = assemble(first, second, "adventure/hybrid.png", "colorless/adventure/card.png");
				else
					frame = assemble(first, second, "adventure/multi.png", "multicolored/adventure/card.png");
			}

			// Les couronnes (de companion, legendaire, nyx et des borderless)
			case COMPANION, LEGEND, NYX_LEGEND, BL_LEGEND, ZNR_LEGEND -> frame = assemble(first, second, "crown.png", null);

			// Les tampons pour la rarete
			case LAND_STAMP, STAMP, ADVENTURE_STAMP, BL_STAMP -> {
				frame = assemble(first, second, "stamp.png", null, 0, 46, 1.0f / 46.0f);
			}
			
			// Les tampons pour la rarete
			case KHM_STAT -> {
				frame = assemble(first, second, "kaldheim/stat.png", null, 0, 46, 1.0f / 46.0f);
			}

			// Les sagas
			case SAGA -> {
				if (isHybrid)
					frame = assemble(first, second, "saga/hybrid.png", "colorless/saga.jpg");
				else
					frame = assemble(first, second, "saga/multi.png", "multicolored/saga.jpg");
			}

			case TOKEN -> throw new UnsupportedOperationException("Unimplemented case: " + sect);
			case TOKEN1 -> throw new UnsupportedOperationException("Unimplemented case: " + sect);
			case TOKEN2 -> throw new UnsupportedOperationException("Unimplemented case: " + sect);
			default -> throw new IllegalArgumentException("Unexpected value: " + sect);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pour les artefact
	 * 
	 * @param firstImage La premiere image assemblé
	 * @param c          Si l'artefact n'a qu'une seule couleur
	 * @param s          La section de la carte
	 */
	public FrameAssembler(final PixelReader firstImage, final ColorStyle c, final CardSection sect) {
		PixelReader px;
		if (firstImage == null)
			px = new Image("file:resources/image/card/" + c.name().toLowerCase() + "/" + sect.getPath())
					.getPixelReader();
		else
			px = firstImage;

		try {
			switch (sect) {
			case FRAME -> frame = assembleArtifact(px, "artefact/card.png", "artefact/basic.jpg");
			case SNOW -> frame = assembleArtifact(px, "artefact/card.png", "artefact/snow/basic.jpg");
			default -> throw new IllegalArgumentException("Unexpected value: " + sect);
			}
		} catch (final IOException e) {
			System.err.println(e.getMessage() + " Erreur lors de la creation pour artefact de la section : " + sect);
		}
	}

	public static boolean needArtefactAssemble(final CardSection sect) {
		return sect == CardSection.FRAME || sect == CardSection.SNOW;
	}

	private WritableImage assemble(final BufferedImage first, final BufferedImage second, final String mask,
			final String multi) throws IOException {
		return assemble(first, second, mask, multi, 162, 213, 1.0f / 51.0f);
	}

	private WritableImage assemble(final BufferedImage first, final BufferedImage second, final String m,
			final String multiPath, final int startDegra, final int stopDegra, final float degInc) throws IOException {
		// On met le facteur de dégrasé a 0
		float deg = 0.0f;

		// On load le mask ainsi que la carte par défaut si il y en a une
		final BufferedImage mask = ImageIO.read(new File("resources/image/mask/" + m));
		BufferedImage multi = null;
		if (multiPath != null)
			multi = ImageIO.read(new File("resources/image/card/" + multiPath));

		// La taille de l'image
		final int h = first.getHeight(), w = first.getWidth();

		final WritableImage wr = new WritableImage(w, h);
		final PixelWriter pw = wr.getPixelWriter();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// On recup le facteur gris
				final int greyScale = (mask.getRGB(x, y) >> 16) & 0xFF;

				// Lorsqu'on doit écrire
				if (greyScale > 1) {
					final int rgb1 = first.getRGB(x, y), rgb2 = second.getRGB(x, y);
					final float factor = (float) (greyScale) / 255.0f;

					if (rgb1 == 0 || rgb2 == 0) {
						pw.setArgb(x, y, 0);
						continue;
					}

					if (x < startDegra) {
						final float r = (float) ((rgb1 >> 16) & 0xFF) * factor;
						final float g = (float) ((rgb1 >> 8) & 0xFF) * factor;
						final float b = (float) ((rgb1 >> 0) & 0xFF) * factor;

						pw.setArgb(x, y, getRGBA((int) r, (int) g, (int) b, (rgb1 >> 24) & 0xFF));
					} else if (x >= stopDegra) {
						final float r = (float) ((rgb2 >> 16) & 0xFF) * factor;
						final float g = (float) ((rgb2 >> 8) & 0xFF) * factor;
						final float b = (float) ((rgb2 >> 0) & 0xFF) * factor;

						pw.setArgb(x, y, getRGBA((int) r, (int) g, (int) b, (rgb2 >> 24) & 0xFF));
					} else {
						deg += degInc;

						final float inv = 1.0f - deg;

						final float r = (float) ((rgb1 >> 16) & 0xFF) * factor * inv,
								g = (float) ((rgb1 >> 8) & 0xFF) * factor * inv,
								b = (float) ((rgb1 >> 0) & 0xFF) * factor * inv,
								r1 = (float) ((rgb2 >> 16) & 0xFF) * factor * deg,
								g1 = (float) ((rgb2 >> 8) & 0xFF) * factor * deg,
								b1 = (float) ((rgb2 >> 0) & 0xFF) * factor * deg;
						final int a = (rgb1 >> 24) & 0xFF;

						final int finalRgb = getRGBA((int) (r + r1), (int) (g + g1), (int) (b + b1), a);
						pw.setArgb(x, y, finalRgb);
					}
				} else {
					if (multi != null)
						pw.setArgb(x, y, multi.getRGB(x, y));
					else
						pw.setArgb(x, y, 0);
				}
			}

			// On reinitialise le degradé
			deg = 0.0f;
		}

		return wr;
	}

	private WritableImage assembleArtifact(final PixelReader reader, final String maskPath, final String fontCard)
			throws IOException {
		final BufferedImage mask = ImageIO.read(new File("resources/image/mask/" + maskPath)),
				font = ImageIO.read(new File("resources/image/card/" + fontCard));

		final int h = mask.getHeight(), w = mask.getWidth();

		final WritableImage wr = new WritableImage(w, h);
		final PixelWriter pw = wr.getPixelWriter();
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				// On recup le facteur gris
				final int greyScale = (mask.getRGB(x, y) >> 16) & 0xFF;

				// Lorsqu'on doit écrire
				if (greyScale > 1)
					pw.setArgb(x, y, reader.getArgb(x, y));
				else
					pw.setArgb(x, y, font.getRGB(x, y));
			}

		return wr;
	}

	private int getRGBA(final int r, final int g, final int b, final int a) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	public Image getFrame() {
		return frame;
	}

	public WritableImage getImage() {
		return frame;
	}
}
