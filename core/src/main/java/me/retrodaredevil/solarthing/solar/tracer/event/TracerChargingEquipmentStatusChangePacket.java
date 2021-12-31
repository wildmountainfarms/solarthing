package me.retrodaredevil.solarthing.solar.tracer.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.DefaultFinal;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.ChangePacket;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.event.SupplementarySolarEventPacket;
import me.retrodaredevil.solarthing.solar.tracer.SupplementaryTracerPacket;
import me.retrodaredevil.solarthing.solar.tracer.TracerChargingEquipmentStatus;

/**
 * Represents a change in charging equipment status which can represent any or all changes in
 * charging mode, PV voltage status, or error modes.
 * <p>
 * Data represented by this change can be seen in {@link TracerChargingEquipmentStatus}
 */
@JsonDeserialize(as = ImmutableTracerChargingEquipmentStatusChangePacket.class)
@JsonTypeName("TRACER_CHARGING_EQUIPMENT_STATUS_CHANGE")
@JsonExplicit
public interface TracerChargingEquipmentStatusChangePacket extends SupplementarySolarEventPacket, SupplementaryTracerPacket, ChangePacket {

	@DefaultFinal
	@Override
	default @NotNull SolarEventPacketType getPacketType() {
		return SolarEventPacketType.TRACER_CHARGING_EQUIPMENT_STATUS_CHANGE;
	}

	@JsonProperty("chargingEquipmentStatusValue")
	int getChargingEquipmentStatusValue();

	@JsonProperty("previousChargingEquipmentStatusValue")
	@Nullable Integer getPreviousChargingEquipmentStatusValue();

	@Override
	default boolean isLastUnknown() {
		return getPreviousChargingEquipmentStatusValue() == null;
	}

	@GraphQLInclude("chargingEquipmentStatus")
	@NotNull TracerChargingEquipmentStatus getChargingEquipmentStatus();

	@GraphQLInclude("previousChargingEquipmentStatus")
	@Nullable TracerChargingEquipmentStatus getPreviousChargingEquipmentStatus();


}
