package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingMethod;

/**
 * Represents the special power control values of register 0xE02D.
 * Can be serialized to JSON, but cannot be deserialized. Serialization should only be used for debugging or for GraphQL schema.
 * <p>
 * Although this uses a mix of JsonProperty and GraphQLInclude annotations, it's just to maintain convention and is not for any particular purpose
 */
public interface SpecialPowerControl_E02D extends UpperLower16Bit {

	@GraphQLInclude("formattedInfo")
	default String getFormattedInfo(){
		return "intelligent power: " + (isIntelligentPowerEnabled() ? "enabled" : "disabled") + "\n" +
			"each night on: " + (isEachNightOnEnabled() ? "enabled" : "disabled") + "\n" +
			"battery type: " + getBatteryType().getModeName() + "\n" +
			"charging method: " + getChargingMethod().getModeName() + "\n" +
			"no charging below 0C: " + (isNoChargingBelow0CEnabled() ? "enabled" : "disabled") + "\n" +
			"system voltage: " + getSystemVoltage().getModeName();
	}

	@JsonProperty("isIntelligentPowerEnabled")
	default boolean isIntelligentPowerEnabled(){
		return (0b10 & getUpper()) != 0;
	}

	@JsonProperty("isEachNightOnEnabled")
	default boolean isEachNightOnEnabled(){
		return (0b1 & getUpper()) != 0;
	}

	@JsonProperty("batteryTypeValueCode")
	default int getBatteryTypeValueCode(){
		return (0b11110000 & getLower()) >>> 4;
	}
	@GraphQLInclude("batteryType")
	default BatteryType getBatteryType(){
		return Modes.getActiveMode(BatteryType.class, getBatteryTypeValueCode());
	}
	@GraphQLInclude("isLithiumBattery")
	default boolean isLithiumBattery(){
		switch(getBatteryType()){
			case LITHIUM: return true;
			case LEAD_ACID: return false;
			default: throw new UnsupportedOperationException();
		}
	}
	default int getRawChargingMethodValueCode(){
		return (0b1000 & getLower()) >>> 3; // TODO determine if this is correct and if we need a raw/non-raw getter
	}
	@JsonProperty("chargingMethodValueCode")
	default int getChargingMethodValueCode(){ return ~ChargingMethod_E02D.IGNORED_BITS & getRawChargingMethodValueCode(); }
	@GraphQLInclude("chargingMethod")
	default ChargingMethod_E02D getChargingMethod(){
		return Modes.getActiveMode(ChargingMethod_E02D.class, getChargingMethodValueCode());
	}

	/**
	 * @return true if "no charging below 0C" is enabled, false otherwise
	 */
	@JsonProperty("isNoChargingBelow0CEnabled")
	default boolean isNoChargingBelow0CEnabled(){
		return (0b100 & getLower()) != 0;
	}

	@JsonProperty("systemVoltageValueCode")
	default int getSystemVoltageValueCode(){
		return 0b11 & getLower();
	}
	@GraphQLInclude("systemVoltage")
	default SystemVoltage getSystemVoltage(){
		return Modes.getActiveMode(SystemVoltage.class, getSystemVoltageValueCode());
	}
	@GraphQLInclude("is24VSystem")
	default boolean is24VSystem(){
		return getSystemVoltage() == SystemVoltage.V24;
	}

	enum BatteryType implements CodeMode {
		LEAD_ACID("lead-acid", 0),
		LITHIUM("lithium", 1);

		private final String name;
		private final int value;

		BatteryType(String name, int value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public int getValueCode() {
			return value;
		}

		@Override
		public @NotNull String getModeName() {
			return name;
		}
	}
	enum ChargingMethod_E02D implements CodeMode {
		PWM(0, ChargingMethod.PWM),
		DIRECT(1, ChargingMethod.DIRECT),
		;
		private static final int IGNORED_BITS = 0b11111100;

		private final int value;
		private final ChargingMethod chargingMethod;

		ChargingMethod_E02D(int value, ChargingMethod chargingMethod) {
			this.value = value;
			this.chargingMethod = chargingMethod;
		}

		public ChargingMethod getChargingMethod(){
			return chargingMethod;
		}

		@Override
		public int getValueCode() {
			return value;
		}

		@Override
		public @NotNull String getModeName() {
			return chargingMethod.getModeName();
		}
	}
	/**
	 * For use with PDU Address 0xE02D, lower 8 bits, (b1 to b0)
	 */
	enum SystemVoltage implements CodeMode {
		V12(12, 0),
		V24(24, 1)
		;

		private final int voltage;
		private final int code;

		SystemVoltage(int voltage, int code) {
			this.voltage = voltage;
			this.code = code;
		}

		@Override
		public int getValueCode() {
			return code;
		}

		@Override
		public @NotNull String getModeName() {
			return voltage + "V";
		}
	}
}
