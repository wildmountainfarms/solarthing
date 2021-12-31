package me.retrodaredevil.solarthing.solar.tracer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.VersionedPacket;
import me.retrodaredevil.solarthing.packets.identification.NumberedIdentifiable;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacketType;

@JsonExplicit
@JsonTypeName("TRACER_STATUS")
@JsonDeserialize(as = ImmutableTracerStatusPacket.class)
public interface TracerStatusPacket extends TracerReadTable, SolarStatusPacket, VersionedPacket, NumberedIdentifiable {

	@Deprecated
	int CHARGING_EQUIPMENT_FIX_VERSION = Version.CHARGING_EQUIPMENT_FIX;

	@UtilityClass
	class Version {
		private Version() { throw new UnsupportedOperationException(); }

		public static final int CHARGING_EQUIPMENT_FIX = 1;
		/** The version where charging equipment change event may be present in solarthing_events. Also, possible for "number" field to be present. */
		public static final int CHARGING_EQUIPMENT_EVENT = 2;
		public static final int LATEST = CHARGING_EQUIPMENT_EVENT;
	}

	@Override
	default @NotNull SolarStatusPacketType getPacketType() {
		return SolarStatusPacketType.TRACER_STATUS;
	}

	@Override
	@NotNull TracerIdentifier getIdentifier();

	@JsonInclude(JsonInclude.Include.NON_DEFAULT) // won't include 0
	@JsonProperty("number")
	@Override
	int getNumber();
}
