package me.retrodaredevil.solarthing.solar.outback.mx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.KnownSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.support.Support;
import me.retrodaredevil.solarthing.solar.event.SolarEventPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.mx.MXIdentityInfo;
import org.jspecify.annotations.NonNull;

public class ImmutableMXRawDayEndPacket implements MXRawDayEndPacket {

	private final int address;
	private final float dailyKWH;
	private final int dailyAH;
	private final Support dailyAHSupport;

	private final KnownSupplementaryIdentifier<OutbackIdentifier> identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	public ImmutableMXRawDayEndPacket(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "dailyKWH", required = true) float dailyKWH,
			@JsonProperty(value = "dailyAH", required = true) int dailyAH,
			@JsonProperty(value = "dailyAHSupport", required = true) Support dailyAHSupport
	) {
		this.address = address;
		this.dailyKWH = dailyKWH;
		this.dailyAH = dailyAH;
		this.dailyAHSupport = dailyAHSupport;
		identifier = new DefaultSupplementaryIdentifier<>(new OutbackIdentifier(address), SolarEventPacketType.MXFM_RAW_DAY_END.toString());
		identityInfo = new MXIdentityInfo(address);
	}

	@Override
	public @NonNull KnownSupplementaryIdentifier<OutbackIdentifier> getIdentifier() {
		return identifier;
	}

	@Override
	public @NonNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public float getDailyKWH() {
		return dailyKWH;
	}

	@Override
	public int getDailyAH() {
		return dailyAH;
	}

	@NonNull
	@Override
	public Support getDailyAHSupport() {
		return dailyAHSupport;
	}

	@Override
	public int getAddress() {
		return address;
	}
}
