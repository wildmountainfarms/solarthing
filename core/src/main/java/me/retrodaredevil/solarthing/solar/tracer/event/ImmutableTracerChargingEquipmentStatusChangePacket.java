package me.retrodaredevil.solarthing.solar.tracer.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.tracer.TracerChargingEquipmentStatus;
import me.retrodaredevil.solarthing.solar.tracer.TracerIdentifier;
import me.retrodaredevil.solarthing.solar.tracer.TracerIdentityInfo;

public class ImmutableTracerChargingEquipmentStatusChangePacket implements TracerChargingEquipmentStatusChangePacket {
	private final int chargingEquipmentStatusValue;
	private final @Nullable Integer previousChargingEquipmentStatusValue;
	private final KnownSupplementaryIdentifier<TracerIdentifier> identifier;
	private final IdentityInfo identityInfo;


	private final @NotNull TracerChargingEquipmentStatus tracerChargingEquipmentStatus;
	private final @Nullable TracerChargingEquipmentStatus previousTracerChargingEquipmentStatus;

	@JsonCreator
	private ImmutableTracerChargingEquipmentStatusChangePacket(
			@JsonProperty(value = "number", required = true) int number,
			@JsonProperty(value = "chargingEquipmentStatusValue", required = true) int chargingEquipmentStatusValue,
			@JsonProperty(value = "previousChargingEquipmentStatusValue", required = true) @Nullable Integer previousChargingEquipmentStatusValue
	) {
		this(TracerIdentifier.getFromNumber(number), chargingEquipmentStatusValue, previousChargingEquipmentStatusValue);
	}

	public ImmutableTracerChargingEquipmentStatusChangePacket(TracerIdentifier tracerIdentifier, int chargingEquipmentStatusValue, @Nullable Integer previousChargingEquipmentStatusValue) {
		this.chargingEquipmentStatusValue = chargingEquipmentStatusValue;
		this.previousChargingEquipmentStatusValue = previousChargingEquipmentStatusValue;

		identifier = new DefaultSupplementaryIdentifier<>(tracerIdentifier, SolarEventPacketType.TRACER_CHARGING_EQUIPMENT_STATUS_CHANGE.toString());
		identityInfo = new TracerIdentityInfo(null);

		tracerChargingEquipmentStatus = new ImmutableTracerChargingEquipmentStatus(chargingEquipmentStatusValue);
		previousTracerChargingEquipmentStatus = previousChargingEquipmentStatusValue == null ? null : new ImmutableTracerChargingEquipmentStatus(previousChargingEquipmentStatusValue);
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getNumber() {
		return identifier.getSupplementaryTo().getNumber();
	}

	@Override
	public @NotNull KnownSupplementaryIdentifier<TracerIdentifier> getIdentifier() {
		return identifier;
	}

	@Override
	public int getChargingEquipmentStatusValue() {
		return chargingEquipmentStatusValue;
	}

	@Override
	public @Nullable Integer getPreviousChargingEquipmentStatusValue() {
		return previousChargingEquipmentStatusValue;
	}

	@Override
	public @NotNull TracerChargingEquipmentStatus getChargingEquipmentStatus() {
		return tracerChargingEquipmentStatus;
	}

	@Override
	public @Nullable TracerChargingEquipmentStatus getPreviousChargingEquipmentStatus() {
		return previousTracerChargingEquipmentStatus;
	}

	class ImmutableTracerChargingEquipmentStatus implements TracerChargingEquipmentStatus {
		private final int chargingEquipmentStatusValue;

		ImmutableTracerChargingEquipmentStatus(int chargingEquipmentStatusValue) {
			this.chargingEquipmentStatusValue = chargingEquipmentStatusValue;
		}

		@Override
		public @NotNull Identifier getIdentifier() {
			return ImmutableTracerChargingEquipmentStatusChangePacket.this.getIdentifier();
		}

		@Override
		public @NotNull IdentityInfo getIdentityInfo() {
			return ImmutableTracerChargingEquipmentStatusChangePacket.this.getIdentityInfo();
		}

		@Override
		public int getChargingEquipmentStatus() {
			return chargingEquipmentStatusValue;
		}
	}
}
