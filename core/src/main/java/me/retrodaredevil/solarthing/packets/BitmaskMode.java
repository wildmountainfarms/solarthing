package me.retrodaredevil.solarthing.packets;

/**
 * A {@link BitmaskMode} represents a mode where multiple of its kind can be active at the same time.
 */
public interface BitmaskMode extends Mode {
	/**
	 * @return The mask value. This should be a power of 2. (1 << x)
	 */
	int getMaskValue();

	@Override
	default boolean isActive(int code){
		return (getMaskValue() & code) != 0;
	}

}
