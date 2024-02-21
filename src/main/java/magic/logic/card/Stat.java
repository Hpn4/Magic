package magic.logic.card;

import java.io.Serializable;

/**
 * This class is a container for the stat of a card. It means it's power and it's toughness
 */
public class Stat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9007061867720050347L;

	private int power;

	private int toughness;

	public Stat(final Stat stat) {
		if (stat != null) {
			power = stat.power;
			toughness = stat.toughness;
		} else
			power = toughness = 0;
	}

	public Stat(final int power, final int toughness) {
		this.power = power;
		this.toughness = toughness;
	}

	public int getPower() {
		return power;
	}

	public void setPower(final int power) {
		this.power = power;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(final int toughness) {
		this.toughness = toughness;
	}

	public void addToughness(final int add) {
		toughness += add;
	}

	public void set(final int power, final int toughness) {
		this.power = power;
		this.toughness = toughness;
	}

	public void set(final Stat stat) {
		power = stat.power;
		toughness = stat.toughness;
	}

	public void add(final int power, final int toughness) {
		this.power += power;
		this.toughness += toughness;
	}

	public void add(final Stat stat) {
		this.power += stat.power;
		this.toughness += stat.toughness;
	}

	public void remove(final Stat stat) {
		this.power -= stat.power;
		this.toughness -= stat.toughness;
	}

	@Override
	public String toString() {
		return power + "/" + toughness;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + power;
		result = prime * result + toughness;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Stat other = (Stat) obj;
		return power == other.power && toughness == other.toughness;
	}

}
