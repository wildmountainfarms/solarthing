package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;

import static java.util.Objects.requireNonNull;

public final class DefaultInstanceOptions {
	public static final DefaultInstanceOptions DEFAULT_DEFAULT_INSTANCE_OPTIONS = new DefaultInstanceOptions(InstanceSourcePacket.DEFAULT_SOURCE_ID, 0); // TODO

	private final String defaultSourceId;
	private final int defaultFragmentId;

	public DefaultInstanceOptions(String defaultSourceId, int defaultFragmentId) {
		requireNonNull(this.defaultSourceId = defaultSourceId);
		this.defaultFragmentId = defaultFragmentId;
	}

	public @NotNull String getDefaultSourceId() {
		return defaultSourceId;
	}

	public int getDefaultFragmentId() {
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
