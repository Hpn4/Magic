package magic.main.set;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import magic.logic.card.CColor;
import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.CreatureT;
import magic.logic.card.Stat;
import magic.logic.card.State;
import magic.logic.card.abilities.A;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.Cant;
import magic.logic.card.abilities.Capacities;
import magic.logic.card.abilities.EventTrigger;
import magic.logic.card.abilities.Interdiction;
import magic.logic.card.abilities.SpellAbilities;
import magic.logic.card.abilities.condition.Condition;
import magic.logic.card.abilities.condition.Cumul;
import magic.logic.card.abilities.condition.NumberCondition;
import magic.logic.card.abilities.effect.CreateTokenEffect;
import magic.logic.card.abilities.effect.DestroyEffect;
import magic.logic.card.abilities.effect.Effect;
import magic.logic.card.abilities.effect.TapEffect;
import magic.logic.card.abilities.effect.inversable.AddInterdictionEffect;
import magic.logic.card.abilities.effect.linker.ConditionEffect;
import magic.logic.card.abilities.effect.linker.E;
import magic.logic.card.abilities.effect.linker.Effects;
import magic.logic.card.abilities.effect.linker.InversableEffects;
import magic.logic.card.abilities.effect.linker.UntilEffect.UntilMode;
import magic.logic.card.abilities.statics.AlternativeCostAbilities;
import magic.logic.card.abilities.statics.AsLongAsAbilities;
import magic.logic.card.abilities.statics.WouldAbilities;
import magic.logic.card.abilities.utils.CounterType;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.place.Place;
import magic.logic.utils.TargetType;
import magic.logic.utils.extractor.OwnerExtractor;
import magic.logic.utils.extractor.OwnerExtractor.OwnerTarget;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.extractor.number.NumberType;
import magic.logic.utils.selector.LogicalOp;
import magic.logic.utils.selector.Operator;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;

public class CoreSet2020 extends AbstractSet {

    private final Card spirit;

    private final Card soldier;

    private final Card golem;

    public CoreSet2020() {
        super("m20");

        // Tokens
        spirit = SpiritToken();
        soldier = SoldierToken();
        golem = GolemToken();

        // All lands type
        Plains();
        Island();
        Swamp();
        Mountain();
        Forest();

        // All cards
        load();

        try (final FileOutputStream fos = new FileOutputStream("resources/card/" + set + "/info");
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            final int size = datas.size();
            oos.writeInt(size);

            for (int i = 0; i < size; i++)
                oos.writeObject(datas.get(i));
        } catch (final IOException e) {
            System.err.println("Error while writing card: " + e.getMessage());
        }

        try (final FileOutputStream fos = new FileOutputStream("resources/m20.deck");
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(deck);
        } catch (final IOException e) {
            System.err.println("Error while writing deck: " + e.getMessage());
        }
    }

    public void load() {
        // Commented cards are cards that are not supported by the game logic

        // White
        AerialAssault();
        Ajani();
        AncestralBlade();
        AngelOfVitality();
        AngelicGift();
        ApostleOfPurifyingLight();
        BattalionFootSoldier();
        BishopOfWings();
        // BroughtBack();
        CavalierOfDown();
        DawningAngel();
        DaybreakChaplain();
        DevoutDecree();
        Disenchant();
        EternalIsolation();
        FencingAce();
        // GauntletsOfLight();
        GlaringAegis();
        // GodsWilling();
        GriffinProtector();
        GriffinSentinel();
        HangedExecutioner();
        HeraldOfTheSun();
        InspiredCharge();
        InspiringCaptain();
        // LeylineOfSanctity();
        LoxodonLifechanter();
        LoyalPegasus();
        MasterSplicer();
        MomentOfHeroism();
        MorlandInquisitor();
        Pacifism();
        PlanarCleansing();
        RaiseTheAlarm();
        RuleOfLaw();
        Sephara();
        Soulmender();
        SquadCaptain();
        // StarfieldMystic();
        SteadfastSentry();
        YokedOx();

        // Blue
    }

    private void AerialAssault() {
        final Selectors select = new Selectors(CardT.CREATURE).state(State.TAPPED).target(1);

        final Nmb life = new Nmb();
        life.getCardCount(creatureUCtrl().capacities(Capacities.FLYING), false);

        write("Aerial Assault", A.spell(E.any(E.destroy(select), E.gainLife(life))));
    }

    private void Ajani() {
        final Nmb life = new Nmb();
        life.getCardCount(new Selectors(CardT.CREATURE, CardT.PLANESWALKER).controller(Owner.YOU), true);

        var a = ActivatedAbilities.loyalty(1, E.gainLife(life));

        var b = ActivatedAbilities.loyalty(-2, token(AjaniToken()));

        final Condition number = new NumberCondition(new Nmb().getLife().s(), Operator.GREATER_EQUAL, 35);
        var exile = new Effects()
                .exile(new Selectors(CardT.ARTIFACT, CardT.CREATURE).controller(Owner.OPPONENT), TargetType.ALL_CARD)
                .add(exileThis);

        var c = ActivatedAbilities.loyalty(0, new ConditionEffect(number, exile));

        write("Ajani, Strength of the Pride", a, b, c);
    }

    private void AncestralBlade() {
        final Equipment equip = new Equipment(this, "Ancestral Blade");

        final Effects enter = new Effects();
        enter.add(token(soldier)).attachCard(null, TargetType.RETURNED_TARGET);

        equip.equip(1);
        equip.ETB(enter);
        equip.addStat(1);
        equip.end();
    }

    private void AngelOfVitality() {
        var card = get("Angel Of Vitality");

        card.addCapacities(Capacities.FLYING);

        var would = new WouldAbilities(Event.GAIN_LIFE, null, E.gainLife(new Nmb().getFromEffect().add(1)));

        var number = new NumberCondition(new Nmb().getLife().s(), Operator.GREATER_EQUAL, 25);
        var asLong = new AsLongAsAbilities(number, E.get(null, TargetType.THIS_CARD, new Stat(2, 2)));

        writeCard(would, asLong);
    }

    private void AngelicGift() {
        final Auras aura = new Auras(this, "Angelic Gift", CardT.CREATURE);

        aura.addCapacities(Capacities.FLYING);
        aura.ETB(draw());
        aura.end();
    }

    private void ApostleOfPurifyingLight() {
        var card = getM("Apostle Of Purifying Light");

        // Protection from black
        card.protectionFrom(new Selectors().color(CColor.DARK));

        final Selectors select = new Selectors(true, 1).place(Place.GRAVEYARD);
        card.activatedAbilities(new Mana(MCType.GENERIC, 2), E.exile(select));

        card.end();
    }

    private void BattalionFootSoldier() {
        final CardM card = new CardM(this, "Battalion Foot Soldier");

        final Selectors select = new Selectors().target(4).name("Battalion Foot Soldier");

        card.ETB(new Effects().searchLibrary(select, Place.HAND, true).shuffle());

        card.end();
    }

    private void BishopOfWings() {
        final Selectors select = new Selectors(CardT.CREATURE).controller(Owner.YOU).creatureType(CreatureT.ANGEL);
        final EventTrigger angelEnter = new EventTrigger(Event.ENTER_BATTLEFIELD, select, gainLife(4));

        final EventTrigger angelDies = new EventTrigger(Event.DIES, select, new CreateTokenEffect(spirit));

        write("Bishop of Wings", angelEnter, angelDies);
    }

    private void BroughtBack() {

    }

    final Owners o = new Owners(new OwnerExtractor(OwnerTarget.FROM_EFFECT));

    private void CavalierOfDown() {
        final CardM card = getM("Cavalier of Dawn");

        card.capacities(Capacities.VIGILANCE);

        card.ETB(
                new Effects().destroy(new Selectors(CardT.PERMANENT).not().cardType(CardT.LAND).upTo().maxTarget(1),
                        TargetType.ALL_CARD).createToken(1, golem, o));

        card.die(E.graveyardTo(new Selectors(true, 1).cardType(CardT.ARTIFACT, CardT.ENCHANTMENTS), Place.HAND));

        card.end();
    }

    private void DawningAngel() {
        final CardM card = new CardM(this, "Dawning Angel");

        card.capacities(Capacities.FLYING);
        card.ETB(gainLife(4));
        card.end();
    }

    private void DaybreakChaplain() {
        final CardM card = new CardM(this, "Daybreak Chaplain");

        card.capacities(Capacities.LIFELINK);
        card.end();
    }

    private void DevoutDecree() {
        final Selectors select = new Selectors(CardT.CREATURE, CardT.PLANESWALKER);
        select.color(CColor.RED, CColor.DARK).target(1);

        final Effects effects = new Effects().exile(select, TargetType.ALL_CARD).scry(1);

        write("Devout Decree", new SpellAbilities(effects));
    }

    private void Disenchant() {
        final Selectors select = new Selectors(CardT.ARTIFACT, CardT.ENCHANTMENTS).target(1);
        write("Disenchant", new SpellAbilities(new DestroyEffect(select, TargetType.ALL_CARD)));
    }

    private void EternalIsolation() {
        final CardM card = getM("Eternal Isolation");

        card.spellAbilities(E.putInLib(new Selectors(true, 1).cardType(CardT.CREATURE).power(4, Operator.GREATER_EQUAL),
                TargetType.ALL_CARD, o, false));

        card.end();
    }

    private void FencingAce() {
        final CardM card = new CardM(this, "Fencing Ace");

        card.capacities(Capacities.DOUBLE_STRIKE);
        card.end();
    }

    private void GauntletsOfLight() {

    }

    private void GlaringAegis() {
        final Auras auras = new Auras(this, "Glaring Aegis", CardT.CREATURE);

        final TapEffect effect = new TapEffect(new Selectors(CardT.CREATURE).controller(Owner.OPPONENT).target(1),
                TargetType.ALL_CARD, true);
        auras.ETB(effect);
        auras.addStat(new Stat(1, 3));
        auras.end();
    }

    private void GriffinProtector() {
        final CardM card = new CardM(this, "Griffin Protector");

        card.capacities(Capacities.FLYING);

        final var until = getUntilEndOfTurn(null, TargetType.THIS_CARD, new Stat(1, 1));

        card.end(new EventTrigger(Event.ENTER_BATTLEFIELD, creatureUCtrl().excludeThis(), until));
    }

    private void GriffinSentinel() {
        final Card card = get("Griffin Sentinel");

        card.addCapacities(Capacities.FLYING, Capacities.VIGILANCE);

        writeCard();
    }

    private final ManaCost w3ge = new ManaCost(new Mana(MCType.WHITE), new Mana(MCType.GENERIC, 3));

    private void HangedExecutioner() {
        final CardM card = new CardM(this, "Hanged Executioner");

        card.capacities(Capacities.FLYING);
        card.ETB(new CreateTokenEffect(spirit));

        card.activatedAbilities(w3ge, exileThis, exile(targetCreature(), TargetType.ALL_CARD));
        card.end();
    }

    private void HeraldOfTheSun() {
        final CardM card = getM("Herald of the Sun");

        card.capacities(Capacities.FLYING);

        final Selectors select = targetCreature().capacities(Capacities.FLYING).excludeThis();
        final var counter = E.addCounter(select, TargetType.ALL_CARD, CounterType.PLUS_ONE, 1);

        card.activatedAbilities(w3ge, counter);
        card.end();
    }

    private void InspiredCharge() {
        final var until = getUntilEndOfTurn(creatureUCtrl(), TargetType.ALL_CARD, new Stat(2, 1));

        write("Inspired Charge", new SpellAbilities(until));
    }

    private void InspiringCaptain() {
        final CardM card = getM("Inspiring Captain");

        final var until = getUntilEndOfTurn(creatureUCtrl(), TargetType.ALL_CARD, new Stat(1, 1));

        card.ETB(until);
        card.end();
    }

    private void LoxodonLifechanter() {
        final CardM card = getM("Loxodon Lifechanter");

        final Nmb nmb = new Nmb().getSomme(new Selectors(CardT.CREATURE).controller(), TargetType.ALL_CARD,
                NumberType.TOUGHNESS);

        card.ETB(E.may(E.setLife(nmb)));

        var getLife = E.until(E.get(null, TargetType.THIS_CARD, new Nmb().getLife()));

        card.activatedAbilities(new ManaCost(new Mana(MCType.WHITE), new Mana(MCType.GENERIC, 4)), getLife);

        card.end();
    }

    private void LoyalPegasus() {
        final CardM card = getM("Loyal Pegasus");

        // On compte le nombre de creature qui attaque. Si il y en a une, elle est toute
        // seule
        final Condition cA = new NumberCondition(
                new Nmb().getCardCount(new Selectors(CardT.CREATURE).attack(), true).s(), Operator.EQUAL, 1);

        final Condition cB = new NumberCondition(
                new Nmb().getCardCount(new Selectors(CardT.CREATURE).block(), true).s(), Operator.EQUAL, 1);

        card.interdiction(Interdiction.ATTACK, cA);
        card.interdiction(Interdiction.BLOCK, cB);

        card.end();
    }

    private void MasterSplicer() {
        final CardM card = getM("Master Splicer");

        card.ETB(E.createToken(1, golem));

        var boost = E.until(E.get(new Selectors(CardT.CREATURE).controller().creatureType(CreatureT.GOLEM),
                TargetType.ALL_CARD, new Stat(1, 1)), UntilMode.LEAVE_BATTLEFIELD_GLOBAL);

        card.ETB(boost);

        card.end();
    }

    private void MomentOfHeroism() {
        final CardM card = getM("Moment of Heroism");

        var inv = new InversableEffects();
        inv.add(E.get(new Selectors(true, 1).controller().cardType(CardT.CREATURE), TargetType.RETURNED_TARGET,
                new Stat(2, 2)));
        inv.add(E.gains(null, TargetType.RETURNED_TARGET, Capacities.LIFELINK));

        card.spellAbilities(E.until(inv));

        card.end();
    }

    private void MorlandInquisitor() {
        final CardM card = getM("Morland Inquisitor");

        var inv = E.gains(null, TargetType.THIS_CARD, Capacities.FIRST_STRIKE);

        card.activatedAbilities(new ManaCost(new Mana(MCType.WHITE), new Mana(MCType.GENERIC, 2)), E.until(inv));

        card.end();
    }

    private void Pacifism() {
        final Auras card = new Auras(this, "Pacifism", new Selectors(CardT.CREATURE));

        card.interdiction(Interdiction.ATTACK);
        card.interdiction(Interdiction.BLOCK);

        card.end();
    }

    private void PlanarCleansing() {
        final CardM card = getM("Planar Cleansing");

        card.spellAbilities(E.destroy(new Selectors().cardType(LogicalOp.NOT, CardT.LAND).cardType(CardT.PERMANENT)));

        card.end();
    }

    private void RaiseTheAlarm() {
        final CardM card = getM("Raise The Alarm");

        card.spellAbilities(E.createToken(2, soldier));

        card.end();
    }

    private void RuleOfLaw() {
        final CardM card = getM("Rule of Law");

        // On recup le nombre de sort lancé pendant le tour
        final Nmb nmb = new Nmb().getTurn(Event.CAST, null, new Owners(new OwnerExtractor(OwnerTarget.PLAYING_PLAYER)),
                Cumul.COUNT);

        // Si le nombre de sort lancé est supérieur ou égale à 1, pas bon
        final NumberCondition cond = new NumberCondition(nmb.s(), Operator.GREATER_EQUAL, 1);
        final Cant cant = new Cant(Interdiction.CAST, cond);

        card.ETB(E.until(new AddInterdictionEffect(null, null, cant), UntilMode.LEAVE_BATTLEFIELD_GLOBAL));
        card.end();
    }

    private void Sephara() {
        final CardM card = getM("Sephara, Sky's Blade");

        // Alternatif cost, one white mana and tap four untapped creature you control
        // with flying
        final Effect a = E.cost(new ManaCost(new Mana(MCType.WHITE)), E.tap(new Selectors(CardT.CREATURE)
                .state(State.UNTAPPED).controller().capacities(Capacities.FLYING).maxTarget(4), TargetType.ALL_CARD));

        final AlternativeCostAbilities cost = new AlternativeCostAbilities(a,
                "Pay one white mana and tap four creature");

        card.capacities(Capacities.FLYING, Capacities.LIFELINK);

        card.ETB(E.until(E.gains(new Selectors(CardT.CREATURE).controller().capacities(Capacities.FLYING),
                TargetType.ALL_CARD, Capacities.INDESTRUCTIBLE), UntilMode.LEAVE_BATTLEFIELD_GLOBAL));
        card.end(cost);
    }

    private void Soulmender() {
        final CardM card = getM("Soulmender");

        card.activatedAbilities(E.gainLife(1));

        card.end();
    }

    private void SquadCaptain() {
        final CardM card = getM("Squad Captain");

        card.capacities(Capacities.VIGILANCE);

        final Nmb otherC = new Nmb().getCardCount(creatureUCtrl(), false);
        card.ETB(E.addCounter(null, TargetType.THIS_CARD, CounterType.PLUS_ONE, new Nmbs(otherC)));

        card.end();
    }

    private void StarfieldMystic() {
        final CardM card = getM("Starfield Mystic");

        // Premier reduce cost

    }

    private void SteadfastSentry() {
        final CardM card = getM("Steadfast Sentry");

        card.capacities(Capacities.VIGILANCE);

        card.die(E.addCounter(creatureUCtrl().target(1), TargetType.ALL_CARD, CounterType.PLUS_ONE, 1));

        card.end();
    }

    private void YokedOx() {
        getM("YokedOx").end();
    }

    /**
     * ***********************************************************************
     * ***************************** BLUE COLOR ******************************
     * ***********************************************************************
     */
    private void AetherGust() {
        final CardM card = getM("Aether Gust");


    }

    /**
     * *****************
     * ****** LAND *****
     * *****************
     */
    private void Plains() {
        final CardM card = getM("Plains");

        card.activatedAbilities(E.addMana(MCType.WHITE));
        card.end();
    }

    private void Island() {
        final CardM card = getM("Island");

        card.activatedAbilities(E.addMana(MCType.BLUE));
        card.end();
    }

    private void Swamp() {
        final CardM card = getM("Swamp");

        card.activatedAbilities(E.addMana(MCType.DARK));
        card.end();
    }

    private void Mountain() {
        final CardM card = getM("Mountain");

        card.activatedAbilities(E.addMana(MCType.RED));
        card.end();
    }

    private void Forest() {
        final CardM card = getM("Forest");

        card.activatedAbilities(E.addMana(MCType.GREEN));
        card.end();
    }

    /**
     * ******************
     * ****** TOKEN *****
     * ******************
     */
    private Card AjaniToken() {
        var card = getToken("Ajani's Pridemate");
        var a = new EventTrigger(Event.GAIN_LIFE, E.addCounter(CounterType.PLUS_ONE, 1));

        writeCard(a);
        return card;
    }

    private Card SpiritToken() {
        var token = getToken("Spirit");
        token.addCapacities(Capacities.FLYING);

        loader.writeCard();
        return token;
    }

    private Card SoldierToken() {
        var token = getToken("Soldier");

        loader.writeCard();
        return token;
    }

    private Card GolemToken() {
        var token = getToken("Golem");

        loader.writeCard();
        return token;
    }

    public static void main(final String[] args) {
        new CoreSet2020();
    }
}
