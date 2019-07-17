package me.retrodaredevil.solarthing.solar.renogy.rover;

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
	final class OperatingSettingBundle {
		private final int durationHours;
		private final int operatingPowerPercentageRegister;
		
		public OperatingSettingBundle(int durationHours, int operatingPowerPercentageRegister) {
			this.durationHours = durationHours;
			this.operatingPowerPercentageRegister = operatingPowerPercentageRegister;
		}
		
		public int getDurationHours() {
			return durationHours;
		}
		
		public int getOperatingPowerPercentageRegister() {
			return operatingPowerPercentageRegister;
		}
	}
	enum PowerSensing {
		POWER_SENSING_1(0xE022),
		POWER_SENSING_2(0xE025),
		POWER_SENSING_3(0xE028)
		;
		
		private final int workingHoursRegister;
		private final int powerWithPeopleSensedRegister;
		private final int powerWithNoPeopleSensedRegister;
		
		PowerSensing(int register) {
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
	final class PowerSensingBundle {
		private final int workingHoursRaw;
		private final int powerWithPeopleSensedRaw;
		private final int powerWithNoPeopleSensedRaw;
		
		public PowerSensingBundle(int workingHoursRaw, int powerWithPeopleSensedRaw, int powerWithNoPeopleSensedRaw) {
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
