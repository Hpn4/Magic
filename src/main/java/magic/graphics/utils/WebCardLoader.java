package magic.graphics.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import magic.logic.card.ArtifactT;
import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.CreatureT;
import magic.logic.card.EnchantT;
import magic.logic.card.LandT;
import magic.logic.card.PT;
import magic.logic.card.Rarity;
import magic.logic.card.SpellT;
import magic.logic.card.abilities.effect.linker.E;
import magic.logic.card.abilities.statics.AlternativeCostAbilities;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.mana.AbstractMana;
import magic.logic.card.mana.DoubleMana;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.utils.CardIOUtils;
import magic.logic.utils.Utils;

public class WebCardLoader {

    private final static List<String> keys = List.of("multiverse_ids", "name", "art_crop", "mana_cost", "type_line",
            "oracle_text", "power", "toughness", "color_identity", "keywords", "promo", "set", "set_name",
            "collector_number", "rarity", "watermarks", "flavor_text", "artist", "frame_effects", "loyalty");

    private final HashMap<String, String> map;

    private Card card;

    private Image artCrop;

    private String path;

    private int version;

    private static final float scaleX = 0.50319f;

    private static final float scaleY = 0.50765f;

    public WebCardLoader(String name, String set, int version) {
        map = new HashMap<>();

        long time = System.nanoTime();

        URLConnection con = null;

        try {
            // On encore le nom
            name = URLEncoder.encode(name, "UTF-8");

            // Si on veut la carte normale
            if (version == 0) {
                if (set != null && !set.equals(""))
                    set = "&set=" + set;
                else
                    set = "";
                final URL url = new URL("https://api.scryfall.com/cards/named?fuzzy=" + name + set + "&pretty=true");
                // System.out.println(url.toString());

                con = url.openConnection();
            } else { // On charge les versions alternatives
                if (set != null && !set.equals(""))
                    set = "+set:" + set;
                else
                    set = "";
                final URL url = new URL("https://api.scryfall.com/cards/search?q=" + name + set
                        + "&unique=prints&as=grid&order=released&pretty=true");

                con = url.openConnection();
            }
        } catch (final IOException e) {
            System.err.println("L'url est mal formé");
        }

        try {
            final ArrayList<String> lines = new ArrayList<>();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(con.getInputStream()));

            buf.lines().forEach(lines::add);
            buf.close();

            if (version > 0) {
                final ArrayList<String> sousVersion = new ArrayList<>();
                int count = 0;

                for (final String line : lines) {
                    if (line.contains("\"object\": \"card\""))
                        count++;

                    if (count == version)
                        sousVersion.add(line);
                }

                genere(time, sousVersion); // On recupere toute les infos qui nous interesse
            } else
                genere(time, lines); // On recupere toute les infos qui nous interesse

            if (version < 0)
                this.version = Math.abs(version);
        } catch (final IOException e) {
            System.err.println("La carte n'a pas été trouvé");
        }

    }

    private void genere(final long time, final ArrayList<String> lines) throws IOException {
        boolean doubleFaced = false;
        for (final String line : lines)
            if (line.contains("card_faces")) {
                doubleFaced = true;
                break;
            }

        if (doubleFaced)
            retourn(time, lines);
        else {
            getCouple(lines);

            String nom = map.get("name");

            // On reformate le nom
            nom = nom.replace(", ", "-");
            nom = nom.replace(" ", "_");

            // On creer la carte
            card = createCard();

            System.out.println("aze: " + card.getKeywords()[0]);

            final ArrayList<CColor> cols = card.getColorIdentity();
            final CColor color = (cols == null || cols.size() == 0) ? CColor.COLORLESS
                    : cols.size() > 1 ? CColor.MULTICOLORED : cols.get(0);
            path = card.getSet().toLowerCase() + "/" + color.name().toLowerCase() + "/" + nom;

            card.setPath(path);

            path = "resources/card/" + path + "/";
            System.out.println(path);

            // On telecharge l'image
            createImage("card", card);

            System.out.println("Time to create " + nom + " : " + ((System.nanoTime() - time) / 1000000.0d));
        }
    }

    private void retourn(final long time, final ArrayList<String> lines) throws IOException {
        ArrayList<String> first = new ArrayList<String>();
        ArrayList<String> second = new ArrayList<String>();
        int when = -1;
        for (final String line : lines) {
            if (line.contains("card_faces"))
                when = 0;

            if (when == 0 && line.endsWith("},"))
                when = 1;

            if (when == 1 && line.contains("legalities"))
                when = 2;

            // On continues d'ajouter dans la premier carte tant
            if (when == -1 && !(line.contains("name") || line.contains("type_line") || line.contains("mana_cost"))) {
                first.add(line);
                second.add(line);
            }

            if (when == 0 || when == 10)
                first.add(line);

            if (when == 1)
                second.add(line);

            if (when == 2) {
                first.add(line);
                second.add(line);
            }
        }

        // On créer la premiere carte
        getCouple(first);

        String nom = map.get("name");

        // On reformate le nom
        nom = nom.replace(", ", "-");
        nom = nom.replace(" ", "_");

        // On creer la carte
        card = createCard();

        final ArrayList<CColor> cols = card.getColorIdentity();
        final CColor color = cols == null ? CColor.COLORLESS : cols.size() > 1 ? CColor.MULTICOLORED : cols.get(0);
        path = card.getSet().toLowerCase() + "/" + color.name().toLowerCase() + "/" + nom;

        card.setPath(path);

        path = "resources/card/" + path + "/";
        System.out.println(path);

        // On telecharge l'image
        createImage("card");

        // On sauvegarde l'image
        final Image tmp = artCrop;

        // On vide la liste et on créer la deuxieme carte
        map.clear();
        getCouple(second);

        final Card backCard = createCard();

        // Le verso du verso est le recto
        backCard.setBackCard(card);

        card.setBackCard(backCard);

        // On fusionne les mots clés
        if (card.getKeywords() != null && backCard.getKeywords() != null) {
            final int len = card.getKeywords().length;
            final String[] keywords = new String[len + backCard.getKeywords().length];

            System.arraycopy(card.getKeywords(), 0, keywords, 0, len);
            System.arraycopy(backCard.getKeywords(), 0, keywords, len, backCard.getKeywords().length);

            card.setKeywords(keywords);
        }

        if (backCard.getSpellType() != SpellT.ADVENTURE)
            createImage("card_back");

        artCrop = tmp;

        System.out.println("Time to create " + nom + " : " + ((System.nanoTime() - time) / 1000000.0d));

        if (card.hasType(CardT.LAND) && card.getBackCard().hasType(CardT.LAND)) {
            final String o = card.getOracle();
            String col = o.substring(o.indexOf("Add"));
            col = col.substring(col.indexOf("{") + 1, col.indexOf("}"));

            final CColor ccol = switch (col.charAt(0)) {
                case 'W' -> CColor.WHITE;
                case 'R' -> CColor.RED;
                case 'U' -> CColor.BLUE;
                case 'B' -> CColor.DARK;
                case 'G' -> CColor.GREEN;
                default -> null;
            };
            card.getColorIdentity().remove(ccol);

            card.getBackCard().setColorIdentity(card.getColorIdentity().get(0));
            card.setColorIdentity(ccol);
        }
    }

    private void getCouple(final ArrayList<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);

            // On verifie que la ligne n'est pas une accolade ou un crochet
            if (line.contains(":")) {
                final int index = line.indexOf("\":");

                // On recupere la clé en supprimant les guillements
                final String key = line.substring(line.indexOf("\"") + 1, index);

                // Rien nous interesse apres cette ligne
                if (key.equals("booster"))
                    break;

                // On recuperer la valeur, +3 car, il y a les : le " et un espace (:" )
                // La valeur est logiquement tout ce qu'il y a apres les deux points
                String value = line.substring(index + 3);

                // Si la valeur est un crochet ouvrant, on recupere les valeurs sur les lignes
                // du dessous
                if (value.equals("[")) {
                    value = "";
                    int nmbLine = i + 1;
                    while (true) {
                        final String v = lines.get(nmbLine++);
                        value += v.trim();
                        if (!v.contains(","))
                            break;
                    }

                    i = nmbLine;
                    value = value.replace("\"", "");
                } else
                    // On supprime la virgule de fin de ligne
                    value = value.substring(0, value.length() - 1);

                // Si la valeur est entre guillement on supprime celui de debut et de fin
                if (value.startsWith("\""))
                    value = value.substring(1, value.length() - 1);

                // Si la clé nous interesse, on l'ajoute au dico
                if (keys.contains(key))
                    map.putIfAbsent(key, value);

                // System.err.println(key + " : " + value);

            }
        }
    }

    private Card createCard() {
        String colors = get("color_identity") + " ";
        final String nom = get("name");
        final String type = get("type_line");

        Card card = null;

        final String limiter = "—"; // Le tiret long n'est pas cod� de la meme maniere sous windows
        int index = type.indexOf(limiter);
        final int add = limiter.length() + 1; // La taille du tiret differe. Puis +1 pour l'espace
        if (type.contains("Land")) {
            card = new Card(nom, CardT.LAND);

            if (index != -1) {
                final String[] types = type.substring(index + add).split(" ");
                final ArrayList<LandT> lTypes = new ArrayList<>(types.length);
                for (final String lType : types)
                    lTypes.add(LandT.valueOf(lType.toUpperCase()));

                card.set(Card.LAND_TYPES, lTypes);
            }
        } else {
            if (type.contains("Creature")) {
                card = new Card(nom, CardT.CREATURE);
                if (type.contains("Token"))
                    card.getType().add(CardT.TOKEN);

                card.setStat(getInt("power"), getInt("toughness"));

                final String[] types = type.substring(index + add).split(" ");
                final ArrayList<CreatureT> cTypes = new ArrayList<>(types.length);
                for (final String cType : types)
                    cTypes.add(CreatureT.valueOf(cType.toUpperCase()));

                card.set(Card.CREATURE_TYPES, cTypes);
            } else if (type.contains("Instant")) {
                card = new Card(nom, CardT.INSTANT);

                if (index != -1)
                    card.set(Card.SPELL_TYPE, SpellT.valueOf(type.substring(index + add).toUpperCase()));
            } else if (type.contains("Sorcery")) {
                card = new Card(nom, CardT.SORCERY);

                if (index != -1)
                    card.set(Card.SPELL_TYPE, SpellT.valueOf(type.substring(index + add).toUpperCase()));
            } else if (type.contains("Planeswalker")) {
                card = new Card(nom, CardT.PLANESWALKER);

                card.set(Card.PLANESWALKER_TYPE, PT.valueOf(type.substring(index + add).toUpperCase()));

                card.addCounter(CounterType.LOYALTY, getInt("loyalty"));
            } else if (type.contains("Enchantment")) {
                card = new Card(nom, CardT.ENCHANTMENTS);

                if (index != -1) {
                    System.out.println(type + " AZED" + type.substring(index + add));
                    card.set(Card.ENCHANTMENT_TYPE, EnchantT.valueOf(type.substring(index + add).toUpperCase()));
                }
            } else if (type.contains("Emblem")) {
                card = new Card(nom, CardT.EMBLEM);

                card.set(Card.PLANESWALKER_TYPE, PT.valueOf(type.substring(index + add).toUpperCase()));
            }

            if (type.contains("Artifact")) {
                if (card == null)
                    card = new Card(nom, CardT.ARTIFACT);
                else
                    card.getType().add(CardT.ARTIFACT);

                if (index != -1 && !card.hasType(CardT.CREATURE))
                    card.set(Card.ARTIFACT_TYPE, ArtifactT.valueOf(type.substring(index + add).toUpperCase()));
            }

            // Onn enregistre le cout de mana et enregistre le cout de payment normal
            final ManaCost mc = getManaCost(get("mana_cost"));
            card.set(Card.MANA_COST, mc);
            card.addAbility(new AlternativeCostAbilities(E.cost(mc), "Default"));
        }

        String keyword = get("keywords");
        if (card.getSpellType() == SpellT.ADVENTURE)
            keyword = "Adventure," + keyword;

        if (keyword != null) {
            if (keyword.contains(",")) {
                final String[] cardKeywords = keyword.split(",");
                final int size = cardKeywords.length;
                final String[] words = new String[size];
                for (int i = 0; i < size; i++) {
                    final String word = cardKeywords[i].replace(" ", "_");
                    words[i] = word.toUpperCase();
                }

                card.setKeywords(words);
            } else
                card.setKeywords(keyword.replace(" ", "").toUpperCase());
        }

        // On extrait la couleur
        if (!colors.equals(" ")) {
            final ArrayList<CColor> ccolors = new ArrayList<>();
            final int in = Utils.countOccurence(colors, ',') + 1;
            for (int i = 0; i < in; i++) {
                final char c = colors.charAt(0);
                final CColor ccol = switch (c) {
                    case 'W' -> CColor.WHITE;
                    case 'R' -> CColor.RED;
                    case 'U' -> CColor.BLUE;
                    case 'B' -> CColor.DARK;
                    case 'G' -> CColor.GREEN;
                    default -> null;
                };

                ccolors.add(ccol);

                colors = colors.substring(2);
            }

            card.setColorIdentity(ccolors);
        }

        // On extrait la rarete
        final Rarity rarete = switch (get("rarity").charAt(0)) {
            case 'm' -> Rarity.MYTHIC;
            case 'r' -> Rarity.RARE;
            case 'u' -> Rarity.UNCO;
            default -> Rarity.COMMON;
        };

        String data = get("oracle_text");
        if (data != null) {
            while ((index = data.indexOf("(")) != -1) {
                final String bef = data.substring(0, index);
                data = bef + data.substring(data.indexOf(")") + 1);
            }
            card.setOracle(data.replace("\\n", "\n "));
        }

        data = get("flavor_text");
        if (data != null) {
            data = data.replace("\\\"", "\"");
            data = data.replace("\\n", "\n");
            card.setFlavor(data);
        }

        card.setArtist(get("artist"));

        card.setSet(get("set"));

        String multiversId = get("multiverse_ids");
        if (multiversId.contains(","))
            multiversId = multiversId.substring(0, multiversId.indexOf(","));
        int id = 0;
        if (!multiversId.equals(""))
            id = Integer.parseInt(multiversId);

        card.setMultiverseID(id);

        String collectorNumber = get("collector_number");
        if (collectorNumber.endsWith("e"))
            collectorNumber = collectorNumber.substring(0, collectorNumber.length() - 1);
        card.setCollectionNumber(Integer.parseInt(collectorNumber));

        card.setRarity(rarete);

        // On met les super types
        if (type.contains("Legendary"))
            card.getType().add(CardT.LEGENDARY);
        if (type.contains("Snow"))
            card.getType().add(CardT.SNOW);
        if (type.contains("Basic"))
            card.getType().add(CardT.BASIC);

        return card;
    }

    private ManaCost getManaCost(String string) {
        final ManaCost mc = new ManaCost();

        while (string.length() > 0) {
            int index = string.indexOf("}");
            final String mana = string.substring(1, index);

            string = string.substring(index + 1);

            AbstractMana m = null;
            if (mana.contains("/")) {
                final MCType first = Utils.getMana(mana.substring(0, 1));
                final MCType second = Utils.getMana(mana.substring(2));
                m = new DoubleMana(first, second);
            } else {
                final MCType first = Utils.getMana(mana);
                if (first == MCType.GENERIC)
                    m = new Mana(MCType.GENERIC, Integer.parseInt(mana));
                else
                    m = new Mana(first);
            }

            mc.addCost(m, 0);
        }

        return mc;
    }

    /**
     * ************************************************
     * ***** METHODE POUR TELECHARGER LES IMAGES ******
     * ************************************************
     */

    private BufferedImage getImage() throws IOException {
        return getImage(new URL(get("art_crop")));
    }

    /**
     * Version modifié qui va chercher les full art et les art en version trés haute
     * qualité
     *
     * @param card La carte à aller chercher
     * @return
     * @throws Exception
     */
    private BufferedImage getImage(final Card card) throws IOException {
        String col = "" + card.getCollectorNumber();
        if (col.length() == 1)
            col = "0" + col;
        if (col.length() == 2)
            col = "0" + col;

        if (version != 0)
            col = "" + version;

        String set = card.getSet();
        if (set.equals("sta"))
            set = "stm";

        return getImage(new URL("http://mtgpics.com/pics/art/" + set + "/" + col + ".jpg"));
    }

    private BufferedImage getImage(final URL url) throws IOException {
        final URLConnection imgCon = url.openConnection();

        final DataInputStream imgBuf = new DataInputStream(new BufferedInputStream(imgCon.getInputStream()));

        final int size = imgCon.getContentLength();
        final byte[] buffer = new byte[4096], img = new byte[size];
        int count, total = 0, a = 0;

        while (total < size
                && (count = imgBuf.read(buffer, 0, a = size - total > buffer.length ? buffer.length : a)) > 0) {
            System.arraycopy(buffer, 0, img, total, count);
            total += count;
        }

        imgBuf.close();

        return ImageIO.read(new ByteArrayInputStream(img));
    }

    /**
     * ******************************************************************
     * ***** METHODE POUR REDIMENSIONNER ET ENREGISTRER LES IMAGES ******
     * ******************************************************************
     */

    private void createImage(final String name, final Card card) throws IOException {
        try {
            BufferedImage crop = getImage(card);

            final int wi = crop.getWidth();
            final int he = crop.getHeight();

            int h = 0;
            int w = 0;

            final int delta = (int) (he / 15.0f);

            if (Math.abs(he - (3.0f / 4.0f * wi)) <= delta) { // Dimension 3:4
                w = 316;
                h = 232;
            } else if (Math.abs(he - (1.3f * wi)) <= delta) { // Dimension 5:4
                w = 375;
                h = 523;
            } else if (Math.abs(he - (wi * 2.3461f)) <= delta) { // Saga
                w = 159;
                h = 381;
            } else {
                System.err.println("Unknown image format...");
                w = (int) Math.floor((float) crop.getWidth() * 0.260467f);
                h = (int) Math.floor((float) crop.getHeight() * 0.290556f);
            }

            createImage(name, w, h, crop);
        } catch (final IOException e) {
            System.err.println("Image not found try on scryfall");
            createImage(name);
        }
    }

    private void createImage(final String name) throws IOException {
        BufferedImage crop = getImage();
        final int w = (int) Math.floor((float) crop.getWidth() * scaleX);
        final int h = (int) Math.floor((float) crop.getHeight() * scaleY);

        createImage(name, w, h, crop);
    }

    private void createImage(final String name, final int w, final int h, BufferedImage crop) throws IOException {
        final java.awt.Image img = crop.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
        crop = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = crop.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // On créer le fichier et on ecrit dedans
        Files.createDirectories(Paths.get(path));
        ImageIO.write(crop, "JPG", new File(path + name + ".jpg"));

        // On convertit l'image pour qu'elle puisse être utiliser par javaFX
        final WritableImage wr = new WritableImage(w, h);
        final PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                pw.setArgb(x, y, crop.getRGB(x, y));

        artCrop = new ImageView(wr).getImage();
    }

    /**
     * ****************************
     * ********** UTILS ***********
     * ****************************
     */

    private String get(final String key) {
        return map.get(key);
    }

    private int getInt(final String key) {
        final String val = map.get(key);
        if (val == null || val.equals(""))
            return 0;

        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void writeCard() {
        CardIOUtils.writeCard(card, path + "card.card");
    }

    public Card getCard() {
        return card;
    }

    public Image getArtCrop() {
        return artCrop;
    }

    public String getPath() {
        return path;
    }
}
