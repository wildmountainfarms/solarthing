package me.retrodaredevil.solarthing.solar.renogy.rover;

import me.retrodaredevil.solarthing.solar.renogy.BatteryType;
import me.retrodaredevil.solarthing.solar.renogy.Voltage;

import java.util.Collection;
import java.util.EnumSet;

import static me.retrodaredevil.solarthing.solar.renogy.BatteryType.*;
import static me.retrodaredevil.solarthing.solar.renogy.Voltage.*;

public enum KnownRoverController {
	ROVER_20("RNG-CTRL-RVR20", EnumSet.of(OPEN, SEALED, GEL, LITHIUM, USER_UNLOCKED), EnumSet.of(V12, V24, AUTO), 20),
	ROVER_20_PG("RNG-CTRL-RVRPG20", ROVER_20),
	ROVER_30("RNG-CTRL-RVR30", EnumSet.of(OPEN, SEALED, GEL, LITHIUM, USER_UNLOCKED), EnumSet.of(V12, V24, AUTO), 30),
	ROVER_30_PG("RNG-CTRL-RVRPG30", ROVER_30),
	ROVER_40("RNG-CTRL-RVR40", EnumSet.of(OPEN, SEALED, GEL, LITHIUM, USER_UNLOCKED), EnumSet.of(V12, V24, AUTO), 40),
	ROVER_40_PG("RNG-CTRL-RVRPG40", ROVER_40),

	ROVER_60("RNG-CTRL-RVR60", EnumSet.of(OPEN, SEALED, GEL, LITHIUM, USER_UNLOCKED), EnumSet.of(V12, V24, V36, V48, AUTO), 60),
	ROVER_60_PG("RNG-CTRL-RVRPG60", ROVER_60),
	ROVER_100("RNG-CTRL-RVR100", EnumSet.of(OPEN, SEALED, GEL, LITHIUM, USER_UNLOCKED), EnumSet.of(V12, V24, V36, V48, AUTO), 100),

	ROVER_ELITE_20("RCC20RVRE-G1", EnumSet.of(OPEN, SEALED, GEL, LITHIUM), EnumSet.of(V12, V24, AUTO), 20),
	ROVER_ELITE_40("RCC40RVRE-G1", EnumSet.of(OPEN, SEALED, GEL, LITHIUM), EnumSet.of(V12, V24, AUTO), 40),

	WANDERER_10("RNG-CTRL-WND10", EnumSet.of(OPEN, SEALED, GEL, LITHIUM), EnumSet.of(V12, V24, AUTO), 10),
	WANDERER_PG_10("RNG-CTRL-WNDPG10", EnumSet.of(OPEN, SEALED, GEL), EnumSet.of(V12, V24, AUTO), 10),

	BOOST_10("RCC10RVRB", EnumSet.of(OPEN, SEALED, GEL, LITHIUM_48V, USER_LOCKED, USER_UNLOCKED), EnumSet.of(V36, V48, AUTO), 10) // user locked represents lithium-36, right now, which we need to change

//	ADVENTURER_LI_30("RNG-CTRL-ADV30-LI", EnumSet.of(OPEN, SEALED, GEL, LITHIUM, USER_UNLOCKED), EnumSet.of(V12, V24, AUTO), 30),
	;
	private static final Collection<KnownRoverController> HAS_LOAD = EnumSet.of(
			ROVER_20, ROVER_20_PG, ROVER_30, ROVER_30_PG, ROVER_40, ROVER_40_PG, ROVER_60, ROVER_60_PG, WANDERER_10, WANDERER_PG_10
	);
	private final String model;
	private final Collection<BatteryType> batteryTypes;
	private final Collection<Voltage> voltages;
	private final int ratedChargingCurrent;


	KnownRoverController(String model, Collection<BatteryType> batteryTypes, Collection<Voltage> voltages, int ratedChargingCurrent) {
		this.model = model;
		this.batteryTypes = batteryTypes;
		this.voltages = voltages;
		this.ratedChargingCurrent = ratedChargingCurrent;
	}
	KnownRoverController(String model, KnownRoverController from) {
		this(model, from.batteryTypes, from.voltages, from.ratedChargingCurrent);
	}

	public String getModel() {
		return model;
	}
	public Collection<BatteryType> getBatteryTypes() {
		return batteryTypes;
	}
	public Collection<Voltage> getVoltages() {
		return voltages;
	}

	public int getRatedChargingCurrent() {
		return ratedChargingCurrent;
	}

	public boolean hasLoad() {
		return HAS_LOAD.contains(this);
	}
	public boolean canChangeLithiumSettings() {
		return this == ROVER_ELITE_20 || this == ROVER_ELITE_40;
	}
}
