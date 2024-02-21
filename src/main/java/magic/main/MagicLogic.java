package magic.main;

import magic.logic.card.Card;
import magic.logic.card.CardT;
import magic.logic.card.CreatureT;
import magic.logic.card.abilities.A;
import magic.logic.card.abilities.ActivatedAbilities;
import magic.logic.card.abilities.EventTrigger;
import magic.logic.card.abilities.condition.ThisCardCondition;
import magic.logic.card.abilities.effect.choose.ChooseCreatureTEffect;
import magic.logic.card.abilities.effect.linker.E;
import magic.logic.card.abilities.effect.linker.Effects;
import magic.logic.card.abilities.statics.AlternativeCostAbilities;
import magic.logic.card.abilities.utils.Event;
import magic.logic.card.abilities.utils.Owner;
import magic.logic.card.mana.MCType;
import magic.logic.card.mana.Mana;
import magic.logic.card.mana.ManaCost;
import magic.logic.game.Game;
import magic.logic.place.Place;
import magic.logic.utils.CardIOUtils;
import magic.logic.utils.extractor.number.Nmb;
import magic.logic.utils.property.CreatureTypeProperty;
import magic.logic.utils.selector.Selectors;
import magic.logic.utils.value.CreatureTypes;
import magic.logic.utils.value.Nmbs;
import magic.logic.utils.value.Owners;
import magic.reseau.client.ClientSide;
import magic.reseau.server.ServerSide;

public class MagicLogic implements Runnable {

	private Game client;

	private Game server;

	public MagicLogic() {
		client = new Game(20, new ClientSide(), Owner.PLAYER1);
		server = new Game(20, new ServerSide(), Owner.PLAYER2);
	}

	@Override
	public void run() {
		// On lance les socket
		server.start();
		client.start();

		// On charge nos deux cartes préalablement programmé et enregistré sous forme
		// de
		// fichier
		final Card c = CardIOUtils.readCard("resources/card/m20/white/Yoked_Ox/card.card");
		final ManaCost mc = new ManaCost(new Mana(MCType.X), new Mana(MCType.GENERIC, 3));
		c.set(Card.MANA_COST, mc);
		c.setStat(1, 4);
		c.setOwner(Owner.PLAYER1);
		c.setPlace(Place.DECK);
		c.setOracle("{G} You lose X life.");

		c.getCardAbilities().getStatics().clear();
		c.addAbility(new AlternativeCostAbilities(E.cost(mc), "Default"));

		final var a = ActivatedAbilities.cost(new ManaCost(new Mana(MCType.GREEN)), E.loseLife(Owner.YOU, new Nmb().getX()));
		a.setOracle("{G}: You lose X life.");
		c.addAbility(a);

		final Card c1 = CardIOUtils.readCard("resources/card/m20/white/Loyal_Pegasus/card.card");
		final ManaCost mc2 = new ManaCost(new Mana(MCType.GREEN));
		c1.set(Card.MANA_COST, mc2);
		c1.setStat(1, 4);
		c1.setOwner(Owner.PLAYER1);
		c1.setPlace(Place.DECK);

		c1.getCardAbilities().getStatics().clear();
		c1.addAbility(new AlternativeCostAbilities(E.cost(mc2), "Default"));

		// Tant qu'on à plus de 23 point de vie, elle a le vol final NumberCondition
		c1.setOracle("Whenever Loyal Pegasus enter the battlefield, choose a creature type. Destroy all the creature with this type");

	 	final CreatureTypeProperty property = new CreatureTypeProperty(null);

		final var effect = E.destroy(new Selectors(false, -1).creatureType(new CreatureTypes(property)));

		final var trigger1 = new EventTrigger(Event.ENTER_BATTLEFIELD, new ThisCardCondition(), effect);
		trigger1.setOracle(c1.getOracle());
		c1.addAbility(A.spell(new ChooseCreatureTEffect(property, new Owners(Owner.YOU))));
		c1.addAbility(trigger1);


		final ManaCost mc1 = new ManaCost(new Mana(MCType.DARK), new Mana(MCType.GREEN, 5), new Mana(MCType.RED, 1));

		// On insert les cartes dans la BDD du server et du client
		server.putCardInBDD(c, c1);

		int doi = -2;
		while (true) {
			if (doi == 0) {
				System.out.println("Client : " + client.getPlayer1().getLife());
				System.out.println("Client : " + client.getPlayer2().getLife());

				System.out.println("Server : " + server.getPlayer1().getLife());
				System.out.println("Server : " + server.getPlayer2().getLife());

				Card card = (Card) client.getBDD().get(c1.getGameID());
				System.out.println("Client : " + card.getCardAbilities().getCapacities());

				card = (Card) server.getBDD().get(c1.getGameID());
				System.out.println("Server : " + card.getCardAbilities().getCapacities());

				break;
			}
			if (doi == -1) {
				// On ajoute du mana à notre reserve
				client.pushEvent(Event.ADD_MANA, c, mc1, client.getPlayer(Owner.YOU, c));
				doi++;
			}
			if (doi == -2) {
				// On ajoute les deux cartes dans la main du joueur 1
				server.pushEvent(Event.PUT_INTO_HAND, c, null, c, c1);
				doi++;
			}

			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println();
		}
	}

	public Game getGame() {
		return client;
	}

	public Game getGame2() {
		return server;
	}

}
