package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.annotations.Nullable;

import me.retrodaredevil.solarthing.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class DefaultInstanceOptions {
	public static final DefaultInstanceOptions DEFAULT_DEFAULT_INSTANCE_OPTIONS = new DefaultInstanceOptions(InstanceSourcePacket.DEFAULT_SOURCE_ID, null);

	private final String defaultSourceId;
	private final Integer defaultFragmentId;

	public DefaultInstanceOptions(String defaultSourceId, Integer defaultFragmentId) {
		requireNonNull(this.defaultSourceId = defaultSourceId);
		this.defaultFragmentId = defaultFragmentId;
	}

	public @NotNull String getDefaultSourceId() {
		return defaultSourceId;
	}

	public @Nullable Integer getDefaultFragmentId() {
		return defaultFragmentId;
	}

	@Override
	public String toString() {
		return "DefaultInstanceOptions(" +
				"defaultSourceId='" + defaultSourceId + '\'' +
				", defaultFragmentId=" + defaultFragmentId +
				')';
	}
}
