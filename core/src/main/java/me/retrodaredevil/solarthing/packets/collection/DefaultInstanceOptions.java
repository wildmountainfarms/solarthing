package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;

import static java.util.Objects.requireNonNull;

public final class DefaultInstanceOptions {
	/** Using this should be avoided whenever possible. It it usually better to have someone configure the defaults themselves, or have an error be thrown when
	 * default instance options have to be used */
	public static final DefaultInstanceOptions DEFAULT_DEFAULT_INSTANCE_OPTIONS = DefaultInstanceOptions.create(InstanceSourcePacket.DEFAULT_SOURCE_ID, 0);
	/** Magic values that should be used to make sure they were not used */
	public static final DefaultInstanceOptions REQUIRE_NO_DEFAULTS = DefaultInstanceOptions.create(InstanceSourcePacket.UNUSED_SOURCE_ID, Integer.MIN_VALUE);

	private final String defaultSourceId;
	private final int defaultFragmentId;

	private DefaultInstanceOptions(@NotNull String defaultSourceId, int defaultFragmentId) {
		requireNonNull(this.defaultSourceId = defaultSourceId);
		this.defaultFragmentId = defaultFragmentId;
	}
	public static DefaultInstanceOptions create(@NotNull String defaultSourceId, int defaultFragmentId) {
		return new DefaultInstanceOptions(defaultSourceId, defaultFragmentId);
	}
	public static void requireFragmentIdNoDefaults(int fragmentId) {
		if (fragmentId == Integer.MIN_VALUE) {
			throw new IllegalStateException("fragmentId is the minimum value!");
		}
	}
	public static void requireSourceIdNoDefaults(String sourceId) {
		if (sourceId.equals(InstanceSourcePacket.UNUSED_SOURCE_ID)) {
			throw new IllegalStateException("sourceId is the 'unused' value!");
		}
	}
	public static void requireNoDefaults(DefaultInstanceOptions defaultInstanceOptions) {
		requireFragmentIdNoDefaults(defaultInstanceOptions.getDefaultFragmentId());
		requireSourceIdNoDefaults(defaultInstanceOptions.getDefaultSourceId());
	}
	public static void requireNoDefaults(InstancePacketGroup instancePacketGroup) {
		int fragmentId = instancePacketGroup.getFragmentId();
		String sourceId = instancePacketGroup.getSourceId();
		requireFragmentIdNoDefaults(fragmentId);
		requireSourceIdNoDefaults(sourceId);
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
