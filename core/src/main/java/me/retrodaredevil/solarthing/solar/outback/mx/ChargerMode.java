package me.retrodaredevil.solarthing.solar.outback.mx;


import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.solar.common.SolarMode;
import me.retrodaredevil.solarthing.solar.common.SolarModeType;

/**
 * The charger mode of the MX
 */
@SuppressWarnings("unused")
public enum ChargerMode implements CodeMode, SolarMode {
	/**
	 * Not charging the battery
	 */
	SILENT(0, "Silent", SolarModeType.CHARGE_CONTROLLER_OFF),
	/**
	 * Once the battery reaches somewhere between 85% and 95%, float mode is entered. Safely maintains the capacity
	 * of the battery at a very high percentage for a period of time
	 */
	FLOAT(1, "Float", SolarModeType.FLOAT),
	/**
	 * The normal way to charge a battery. This uses a constant current to charge the battery
	 */
	BULK(2, "Bulk", SolarModeType.BULK),
	/**
	 * After the battery reaches 80%, it should enter this mode. This charges the battery with a constant voltage
	 * while lowering the current as the battery reaches full capacity
	 */
	ABSORB(3, "Absorb", SolarModeType.ABSORB),
	/**
	 * This is used to "equalize" the water in the batteries. It does this by charging the batteries with a high
	 * voltage.
	 */
	EQ(4, "EQ", SolarModeType.EQUALIZE);

	private final int value;
	private final String name;
	private final SolarModeType solarModeType;

	ChargerMode(int value, String name, SolarModeType solarModeType){
		this.value = value;
		this.name = name;
		this.solarModeType = solarModeType;
	}

	@Override
	public int getValueCode() {
		return value;
	}

	@Override
	public String getModeName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public @NotNull SolarModeType getSolarModeType() {
		return solarModeType;
	}
}
