package me.retrodaredevil.solarthing.solar.extra;

import me.retrodaredevil.solarthing.packets.identification.DefaultSupplementaryIdentifier;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

public class ImmutableDailyUpdatePacket implements DailyUpdatePacket {
	private final Long lastResetTimeMillis;
	private final Long lastAtZeroTimeMillis;
	private final Long lastIncrementTimeMillis;
	private final long monitorStartTimeMillis;
	private final SupplementaryIdentifier identifier;
	private final IdentityInfo identityInfo;

	public ImmutableDailyUpdatePacket(Long lastResetTimeMillis, Long lastAtZeroTimeMillis, Long lastIncrementTimeMillis, long monitorStartTimeMillis, Identifier identifier, IdentityInfo identityInfo) {
		this.lastResetTimeMillis = lastResetTimeMillis;
		this.lastAtZeroTimeMillis = lastAtZeroTimeMillis;
		this.lastIncrementTimeMillis = lastIncrementTimeMillis;
		this.monitorStartTimeMillis = monitorStartTimeMillis;
		this.identifier = new DefaultSupplementaryIdentifier<>(identifier, SolarExtraPacketType.DAILY_UPDATE.toString());
		requireNonNull(this.identityInfo = identityInfo);
	}

	@Override
	public @Nullable Long getLastResetTimeMillis() {
		return lastResetTimeMillis;
	}

	@Override
	public @Nullable Long getLastAtZeroTimeMillis() {
		return lastAtZeroTimeMillis;
	}

	@Override
	public @Nullable Long getLastIncrementTimeMillis() {
		return lastIncrementTimeMillis;
	}

	@Override
	public long getMonitorStartTimeMillis() {
		return monitorStartTimeMillis;
	}

	@Override
	public @NotNull SupplementaryIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}
}
