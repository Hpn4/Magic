package magic.logic.utils;

public enum TargetType {
	/**
	 * Les cibles possiblement renvoyés par l'appeleur. Peut etre une condition, un
	 * selecteur, un effet...
	 */
	RETURNED_TARGET,

	/**
	 * Toute les cartes du jeu. Inclus donc les cartes du champs de bataille, du
	 * cimetierre et de l'exil des deux joueurs
	 */
	ALL_CARD,

	/** La carte possesseur de ce TargetType */
	THIS_CARD,

	/**
	 * Toute les cartes attaches a la carte posseseur de ce TargetType. Les cartes
	 * possiblement attachés peuvent etre des artefact equipement, des
	 * enchantements...
	 */
	ATTACHED_CARD;
}
