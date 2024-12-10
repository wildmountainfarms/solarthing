package me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragment;

public interface DataProvider {
	@Nullable Result getResult(FragmentedPacketGroup fragmentedPacketGroup);

	class Result {
		private final float value;
		private final IdentifierFragment identifierFragment;
		private final Long dateMillis;
		private final boolean isPossiblyBadData;

		public Result(float voltage, IdentifierFragment identifierFragment, Long dateMillis, boolean isPossiblyBadData) {
			this.value = voltage;
			this.identifierFragment = identifierFragment;
			this.dateMillis = dateMillis;
			this.isPossiblyBadData = isPossiblyBadData;
		}
		public static Result ofVoltage(float voltage) {
			return new Result(voltage, null, null, false);
		}

		public float getValue() {
			return value;
		}

		public @Nullable IdentifierFragment getIdentifierFragment() {
			return identifierFragment;
		}

		public @Nullable Long getDateMillis() {
			return dateMillis;
		}

		public boolean isPossiblyBadData() {
			return isPossiblyBadData;
		}
	}
}
