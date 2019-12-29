package me.retrodaredevil.solarthing.solar.renogy.rover;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.beans.ConstructorProperties;

import static java.util.Objects.requireNonNull;

/**
 * An interface that indicates that an object is somehow associated by a rover either by providing data or by setting data
 */
public interface Rover {
	
	enum OperatingSetting {
		STAGE_1(0xE015),
		STAGE_2(0xE017),
		STAGE_3(0xE019),
		MORNING_ON(0xE01B)
		;
		
		private final int durationHoursRegister;
		private final int operatingPowerPercentageRegister;
		
		OperatingSetting(int register){
			durationHoursRegister = register;
			operatingPowerPercentageRegister = register + 1;
		}
		
		public int getDurationHoursRegister() {
			return durationHoursRegister;
		}
		
		public int getOperatingPowerPercentageRegister() {
			return operatingPowerPercentageRegister;
		}
	}
	@JsonExplicit
	final class OperatingSettingBundle {
		private final int durationHours;
		private final int operatingPowerPercentage;

		@ConstructorProperties({"durationHours", "operatingPowerPercentage"})
		private OperatingSettingBundle(Integer durationHours, Integer operatingPowerPercentage){
			this((int) requireNonNull(durationHours), (int) requireNonNull(operatingPowerPercentage));
		}
		public OperatingSettingBundle(int durationHours, int operatingPowerPercentage) {
			this.durationHours = durationHours;
			this.operatingPowerPercentage = operatingPowerPercentage;
		}

		@JsonProperty("durationHours")
		public int getDurationHours() {
			return durationHours;
		}

		@JsonProperty("operatingPowerPercentage")
		public int getOperatingPowerPercentage() {
			return operatingPowerPercentage;
		}
	}
	enum Sensing {
		SENSING_1(0xE022),
		SENSING_2(0xE025),
		SENSING_3(0xE028)
		;
		
		private final int workingHoursRegister;
		private final int powerWithPeopleSensedRegister;
		private final int powerWithNoPeopleSensedRegister;
		
		Sensing(int register) {
			workingHoursRegister = register;
			powerWithPeopleSensedRegister = register + 1;
			powerWithNoPeopleSensedRegister = register + 2;
		}
		
		public int getWorkingHoursRegister() {
			return workingHoursRegister;
		}
		
		public int getPowerWithPeopleSensedRegister() {
			return powerWithPeopleSensedRegister;
		}
		
		public int getPowerWithNoPeopleSensedRegister() {
			return powerWithNoPeopleSensedRegister;
		}
	}
	final class SensingBundle {
		private final int workingHoursRaw;
		private final int powerWithPeopleSensedRaw;
		private final int powerWithNoPeopleSensedRaw;

		@JsonCreator
		public SensingBundle(
				@JsonProperty(value = "workingHoursRaw", required = true) int workingHoursRaw,
				@JsonProperty(value = "powerWithPeopleSensedRaw", required = true) int powerWithPeopleSensedRaw,
				@JsonProperty(value = "powerWithNoPeopleSensedRaw", required = true) int powerWithNoPeopleSensedRaw
		) {
			this.workingHoursRaw = workingHoursRaw;
			this.powerWithPeopleSensedRaw = powerWithPeopleSensedRaw;
			this.powerWithNoPeopleSensedRaw = powerWithNoPeopleSensedRaw;
		}
		
		public int getWorkingHoursRaw() {
			return workingHoursRaw;
		}
		
		public int getPowerWithPeopleSensedRaw() {
			return powerWithPeopleSensedRaw;
		}
		
		public int getPowerWithNoPeopleSensedRaw() {
			return powerWithNoPeopleSensedRaw;
		}
	}
}
