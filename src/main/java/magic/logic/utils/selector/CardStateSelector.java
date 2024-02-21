package magic.logic.utils.selector;

import java.util.Arrays;
import java.util.Objects;

import magic.logic.card.Card;
import magic.logic.card.State;
import magic.logic.game.Game;

public class CardStateSelector implements CardSelector {
    /**
     *
     */
    private static final long serialVersionUID = 6798828599354527887L;

    private final State[] states;

    private final LogicalOp op;

    public CardStateSelector(final LogicalOp op, final State... states) {
        this.states = states;
        this.op = op;
    }

    public CardStateSelector(final State... states) {
        this.states = states;
        op = LogicalOp.AND;
    }

    @Override
    public boolean match(final Game game, final Card thisCard, final Card card) {
        if (op == LogicalOp.AND) {
            for (final State state : states)
                if (!(card.getState()[state.getIndex()] == state)) // Si la carte ne possede pas l'un des etats
                    return false;

            return true;
        }

        for (final State state : states)
            if (card.getState()[state.getIndex()] == state)
                return true;

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = prime + Arrays.hashCode(states);
        return prime * result + Objects.hash(op);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof CardStateSelector other)
            return op == other.op && Arrays.equals(states, other.states);

        return false;
    }

}
