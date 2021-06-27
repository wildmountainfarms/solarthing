package me.retrodaredevil.solarthing.solar.renogy;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.retrodaredevil.solarthing.packets.CodeMode;

/**
 * The battery type
 * <p><p>
 * PDU address: 0xE004, Bytes: 2
 */
public enum BatteryType implements CodeMode {
	/**
	 * This represents the User battery type when it is directly configured from the Rover. When this is selected, charging parameters are unlocked
	 * <p>
	 * AKA self customized
	 */
	USER_UNLOCKED("user-unlocked", 0),
	/**
	 * AKA Flooded
	 */
	OPEN("open", 1),
	/** AKA AGM */
	SEALED("sealed", 2),
	/** AKA colloidal */
	GEL("gel", 3),
	LITHIUM("lithium", 4),
	/**
	 * This represents the User battery type when it is configured over Modbus (remotely). When this is selected, charging parameters are locked and cannot be changed over Modbus (remotely)
	 * <p>
	 * AKA self customized
	 */
	USER_LOCKED("user-locked", 5),
//	LITHIUM_36V("lithium-36", 5), // this is used on Rover Boost models and conflicts with the user-locked battery type, so we'll leave this commented until we find a better solution
	/** Used on Rover Boost models*/
	LITHIUM_48V("lithium-48", 6),
	;
	// manual here has battery types: https://www.renogy.com/content/RNG-CTRL-RVR60/RVR60-Manual.pdf

	private final String name;
	private final int code;

	BatteryType(String name, int code) {
		this.name = name;
		this.code = code;
	}

	@Override
	public int getValueCode() {
		return code;
	}

	@Override
	public String getModeName() {
		return name;
	}

	public boolean isUser() {
		return this == USER_LOCKED || this == USER_UNLOCKED;
	}
	public static BatteryType parseOrNull(String batteryType) {
		try {
			return parse(batteryType);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	@JsonCreator
	public static BatteryType parse(Object object) {
		if (object instanceof Integer) {
			int code = (int) object;
			for (BatteryType type : BatteryType.values()) {
				if (type.isActive(code)) {
					return type;
				}
			}
			throw new IllegalArgumentException("Unknown code: " + code);
		} else if (object instanceof String ) {
			return parseFromString((String) object);
		}
		throw new IllegalArgumentException("Unknown type to parse to a battery type: " + object.getClass());
	}
	public static BatteryType parseFromString(String batteryType) {
		switch(batteryType){
			case "self-customized": case "custom": case "customized": case "user": case "user-unlocked": return BatteryType.USER_UNLOCKED;
			case "open": case "flooded": return BatteryType.OPEN;
			case "sealed": return BatteryType.SEALED;
			case "gel": return BatteryType.GEL;
			case "lithium": return BatteryType.LITHIUM;
			case "user-locked": case "lithium-36": return BatteryType.USER_LOCKED;
			case "lithium-48": return BatteryType.LITHIUM_48V;
			default: return valueOf(batteryType);
		}
	}
}
