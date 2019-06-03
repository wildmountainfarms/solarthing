package me.retrodaredevil.solarthing.util;

public enum IgnoreCheckSum {
	/**
	 * Throws a {@link CheckSumException} when there's an error with the check sum
	 */
	DISABLED,
	/**
	 * Ignores errors in the check sum and saves the incorrect check sum in the packet
	 * <p>
	 * It is recommended to use {@link #IGNORE_AND_USE_CALCULATED} instead of this
	 */
	IGNORE,
	/**
	 * Ignores errors in the check sum and saves the calculated check sum, or correct check sum, in the packet
	 */
	IGNORE_AND_USE_CALCULATED
}
