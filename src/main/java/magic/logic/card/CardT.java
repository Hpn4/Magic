package magic.logic.card;

public enum CardT {
	ARTIFACT, CREATURE, EMBLEM, ENCHANTMENTS, INSTANT, LAND, PLANESWALKER, SORCERY, TRIBAL,
	
	BASIC, LEGENDARY, TOKEN, SNOW,
	
	NON_CREATURE, PERMANENT, SORCERY_INSTANT, HISTORIC;

	public String toString() {
		String name = name().toLowerCase();
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;
	}
}
