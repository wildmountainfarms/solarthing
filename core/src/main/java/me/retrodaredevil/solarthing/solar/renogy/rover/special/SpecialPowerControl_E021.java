package me.retrodaredevil.solarthing.solar.renogy.rover.special;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.CodeMode;
import me.retrodaredevil.solarthing.packets.Modes;
import me.retrodaredevil.solarthing.solar.renogy.rover.ChargingMethod;

/**
 * Represents the special power control values of register 0xE021.
 * Can be serialized to JSON, but cannot be deserialized. Serialization should only be used for debugging or for GraphQL schema.
 * <p>
 * Although this uses a mix of JsonProperty and GraphQLInclude annotations, it's just to maintain convention and is not for any particular purpose
 */
public interface SpecialPowerControl_E021 extends UpperLower16Bit {

	@GraphQLInclude("formattedInfo")
	default String getFormattedInfo(){
		return "charging mode controlled by voltage: " + isChargingModeControlledByVoltage() + "\n" +
			"special power control: " + (isSpecialPowerControlEnabled() ? "enabled" : "disabled") + "\n" +
			"each night on: " + (isEachNightOnEnabled() ? "enabled" : "disabled") + "\n" +
			"no charging below 0C: " + (isNoChargingBelow0CEnabled() ? "enabled" : "disabled") + "\n" +
			"charging method: " + getChargingMethod().getModeName();
	}

	// upper

	/**
	 *
	 * @return true if charging mode is controller by voltage, false if charging mode controlled by SOC
	 */
	@JsonProperty("isChargingModeControlledByVoltage")
	default boolean isChargingModeControlledByVoltage(){
		return (0b100 & getUpper()) != 0;
	}
	/**
	 * @return true if special power control is enabled, false if disabled
	 */
	@JsonProperty("isSpecialPowerControlEnabled")
	default boolean isSpecialPowerControlEnabled(){
		return (0b10 & getUpper()) != 0;
	}
	/**
	 *
	 * @return true if the "each night on" function is enabled, false if disabled
	 */
	@JsonProperty("isEachNightOnEnabled")
	default boolean isEachNightOnEnabled(){
		return (0b1 & getUpper()) != 0;
	}


	// lower

	/**
	 * @return true if "no charging below 0C" is enabled, false otherwise
	 */
	@JsonProperty("isNoChargingBelow0CEnabled")
	default boolean isNoChargingBelow0CEnabled(){
		return (0b100 & getLower()) != 0;
	}

	@JsonProperty("chargingMethodValueCode")
	default int getChargingMethodValueCode(){
		return 0b11 & getLower();
	}
	@GraphQLInclude("chargingMethod")
	default ChargingMethod_E021 getChargingMethod(){
		return Modes.getActiveMode(ChargingMethod_E021.class, getChargingMethodValueCode());
	}

	enum ChargingMethod_E021 implements CodeMode {
		DIRECT(0, ChargingMethod.DIRECT),
		PWM(1, ChargingMethod.PWM)
		;

		private final int value;
		private final ChargingMethod chargingMethod;

		ChargingMethod_E021(int value, ChargingMethod chargingMethod) {
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

}
