package magic.logic.utils.selector;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Objects;

import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.Colors;

public class CardColorSelector implements CardSelector {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1676470992866025406L;

    private final Colors colors;

    /**
     * Définie si une ou toute les couleurs doivent correspondre Ex : (a chaque fois
     * qu'une creature rouge ou verte arrive...) Ex1 : (a chaque fois qu'un
     * permanent bleu et blanc meurt....)
     */
    private final LogicalOp op;

    protected CardColorSelector(final LogicalOp op, final Colors colors) {
        this.op = op;
        this.colors = colors;
    }

    public boolean match(final Game game, final Card thisCard, final Card card) {
        // On extrait les couleurs à tester et les couleurs de la carte
        final CColor[] col = colors.get(game, thisCard, new EffectData(null, card));
        final ArrayList<CColor> cardCols = card.getColorIdentity();

        final boolean and = op == LogicalOp.AND;

        // On parcours parmi toute les couleurs à tester
        for (final CColor color : col) {
            boolean test = switch (color) {
                case MONOCOLORED -> cardCols.size() == 1; // Monocolored, une couleur
                case COLORLESS -> cardCols.size() == 0; // Colorless, abscence de couleur
                case MULTICOLORED -> cardCols.size() > 1; // Multicolor, plus d'une couleur
                default -> cardCols.contains(color);
            };

            if (!and && test)
                return true;

            if (and && !test)
                return false;
        }

        return and;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colors, op);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof CardColorSelector other)
            return Objects.equals(colors, other.colors) && op == other.op;

        return false;
    }
}