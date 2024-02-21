package magic.logic.utils.selector;

import java.util.ArrayList;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.utils.EffectData;
import magic.logic.game.Game;
import magic.logic.utils.value.CreatureTypes;

public class CreatureTypeSelector implements CardSelector {
    /**
     *
     */
    private static final long serialVersionUID = -1311509691990525713L;

    private final CreatureTypes creatureTypes;

    private final LogicalOp op;

    protected CreatureTypeSelector(final CreatureTypes creatureTypes) {
        this.creatureTypes = creatureTypes;
        this.op = LogicalOp.OR;
    }

    protected CreatureTypeSelector(final LogicalOp op, final CreatureTypes creatureTypes) {
        this.creatureTypes = creatureTypes;
        this.op = op;
    }

    public boolean match(final Game game, final Card thisCard, final Card creature) {
        final ArrayList<CreatureT> types = creatureTypes.get(game, thisCard, new EffectData(null, creature));
        final ArrayList<CreatureT> c = creature.getCreatureType();

        // On vérifie d'abord que la carte possède bien des types de créatures
        if (c != null) {
            if (op == LogicalOp.AND) {
                // Si c'est un changelin, il a forcement tout les types de créatures
                if (c.contains(CreatureT.CHANGELING))
                    return true;

                // Si il ne possede pas l'un des types, renvoie faux
                for (final CreatureT type : types)
                    if (!c.contains(type))
                        return false;

                return true;
            }

            final boolean b = op == LogicalOp.OR ? true : false;
            if (c.contains(CreatureT.CHANGELING))
                return b;

            for (final CreatureT type : types)
                if (c.contains(type))
                    return b;

            return !b;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(creatureTypes, op);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof CreatureTypeSelector other)
            return Objects.equals(creatureTypes, other.creatureTypes) && op == other.op;

        return false;
    }

}
