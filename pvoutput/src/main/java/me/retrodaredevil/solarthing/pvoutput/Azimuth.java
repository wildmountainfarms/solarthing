package me.retrodaredevil.solarthing.pvoutput;

/**
 * Represents common azimuths/orientations of solar panels
 */
public enum Azimuth {
	NORTH("N", 0),
	EAST("E", 90),
	SOUTH("S", 180),
	WEST("W", 270),

	NORTH_EAST("NE", 45),
	NORTH_WEST("NW", 315),
	SOUTH_EAST("SE", 135),
	SOUTH_WEST("SW", 225)
	;
	private final String name;
	private final int degrees;

	Azimuth(String name, int degrees) {
		this.name = name;
		this.degrees = degrees;
	}

	public int getDegrees() {
		return degrees;
	}

	public String getName() {
		return name;
	}
}
