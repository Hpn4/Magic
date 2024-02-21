package magic.logic.utils.selector;

import java.io.Serial;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.EnchantT;
import magic.logic.game.Game;

public class EnchantTypeSelector implements CardSelector {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4455987250531152635L;

    private final EnchantT type;

    public EnchantTypeSelector(final EnchantT type) {
        this.type = type;
    }

    public boolean match(final Game game, final Card thisCard, final Card card) {
        return card.getEnchantmentType() == type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof EnchantTypeSelector other)
            return type == other.type;

        return false;
    }

}