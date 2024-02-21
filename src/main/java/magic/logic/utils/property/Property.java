package magic.logic.utils.property;

import java.io.Serializable;
import java.util.Objects;

public abstract class Property<E> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6320919423158511051L;

	protected E e;

	public Property() {

	}

	public Property(final E e) {
		this.e = e;
	}

	public E get() {
		return e;
	}

	public void set(final E e) {
		this.e = e;
	}

	@Override
	public String toString() {
		return e.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(e);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Property))
			return false;

		final Property<E> other = (Property<E>) obj;
		return Objects.equals(e, other.e);
	}

}
