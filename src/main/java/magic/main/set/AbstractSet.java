package magic.main.set;

import java.util.ArrayList;
import java.util.List;

import magic.graphics.utils.SetData;
import magic.graphics.utils.WebCardLoader;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.Stat;
import magic.logic.card.abilities.Abilities;
import magic.logic.card.abilities.effect.CreateTokenEffect;
import magic.logic.card.abilities.effect.DrawCardEffect;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.LifeEffect;
import magic.logic.card.abilities.effect.LifeEffect.LifeMode;
import magic.logic.card.abilities.effect.inversable.ExileEffect;
import magic.logic.card.abilities.effect.inversable.ModifyStatEffect;
import magic.logic.card.abilities.effect.linker.UntilEffect;
import magic.logic.card.abilities.effect.linker.UntilEffect.UntilMode;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.place.Deck;
import magic.logic.place.DeckType;
import magic.logic.place.OptiPlace;
import magic.logic.utils.TargetType;
import magic.logic.utils.selector.Selectors;

public abstract class AbstractSet {

    protected static final ExileEffect exileThis = new ExileEffect(null, TargetType.THIS_CARD);

    protected WebCardLoader loader;

    protected final ArrayList<SetData> datas;

    protected final Deck deck;

    protected final String set;

    private final ArrayList<Abilities> tmp;

    public AbstractSet(final String set) {
        tmp = new ArrayList<>();
        datas = new ArrayList<>();
        deck = new Deck("M20", DeckType.NORMAL, new OptiPlace());
        this.set = set;
    }

    protected Card get(final String name) {
        loader = new WebCardLoader(name, set, 0);

        final Card card = loader.getCard();

        System.out.println(name + " " + card);

        final String path = name + ":" + loader.getPath();
        datas.add(new SetData(card.getName(), card.getCardCost(), path, card.getType()));
        deck.addCard(card);

        return card;
    }

    protected CardM getM(final String name) {
        return new CardM(this, name);
    }

    protected Card getToken(final String name) {
        loader = new WebCardLoader(name, "t" + set, 0);

        final Card card = loader.getCard();

        System.out.println(name + " " + card);

        final String path = name + ":" + loader.getPath();
        datas.add(new SetData(card.getName(), card.getCardCost(), path, card.getType()));
        deck.addCard(card);

        return card;
    }

    protected void writeCard(final Abilities... abilities) {
        addAbility(abilities);
        writeAb();

        loader.writeCard();
    }

    protected void addAbility(final Abilities... abilities) {
        tmp.addAll(List.of(abilities));
    }

    protected void writeAb() {
        final boolean cap = !loader.getCard().getCardAbilities().getCapacities().isEmpty();
        int start = cap ? 1 : 0;

        final String[] oracles = loader.getCard().getOracle().split("\n");

        for (Abilities abilities : tmp) abilities.setOracle(oracles[start++]);

        loader.getCard().addAbility(tmp.toArray(Abilities[]::new));
        tmp.clear();
    }

    protected void write(final String name, final Abilities... abilities) {
        loader = new WebCardLoader(name, set, 0);

        final Card card = loader.getCard();
        addAbility(abilities);
        writeAb();

        loader.writeCard();

        System.out.println(name + " " + card);

        final String path = name + ":" + loader.getPath();
        datas.add(new SetData(card.getName(), card.getCardCost(), path, card.getType()));
        deck.addCard(card);

    }

    // Utility function for recurrent selector
    public Selectors targetCreature() {
        return new Selectors(CardT.CREATURE).target(1);
    }

    public Selectors creatureUCtrl() {
        return new Selectors(CardT.CREATURE).controller();
    }

    // Utility function for recurrent effect
    public Effect draw() {
        return draw(1);
    }

    public Effect draw(final int count) {
        return new DrawCardEffect(Owner.YOU, count);
    }

    public Effect gainLife(final int life) {
        return new LifeEffect(Owner.YOU, life, LifeMode.GAIN);
    }

    public Effect token(final Card token) {
        return new CreateTokenEffect(token);
    }

    public Effect exile(final Selectors select, final TargetType targetType) {
        return new ExileEffect(select, targetType);
    }

    public Effect getUntilEndOfTurn(final Selectors select, final TargetType targetType, final Stat stat) {
        return new UntilEffect(new ModifyStatEffect(select, targetType, stat), UntilMode.END_OF_TURN);
    }
}
