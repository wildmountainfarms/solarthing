package me.retrodaredevil.solarthing.config.request;

import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.packets.handling.PacketListReceiver;

import static java.util.Objects.requireNonNull;

public final class DataRequesterResult {
	private final PacketListReceiver statusPacketListReceiver;
	private final EnvironmentUpdater environmentUpdater;
	private final PacketListReceiver statusEndPacketListReceiver;

	public DataRequesterResult(PacketListReceiver statusPacketListReceiver, EnvironmentUpdater environmentUpdater, PacketListReceiver statusEndPacketListReceiver) {
		this.statusPacketListReceiver = statusPacketListReceiver;
		this.environmentUpdater = environmentUpdater;
		this.statusEndPacketListReceiver = statusEndPacketListReceiver;
	}
	public static Builder builder() {
		return new Builder();
	}

	public PacketListReceiver getStatusPacketListReceiver() {
		return statusPacketListReceiver;
	}

	public EnvironmentUpdater getEnvironmentUpdater() {
		return environmentUpdater;
	}

	/**
	 * This should be added before the call to {@code packetListReceiverList.addAll(bundle.createDefaultPacketListReceivers())}
	 * @return Returns a {@link PacketListReceiver} which should be run at the end of the "pipeline". It should not add any additional packets,
	 *         but typically may be used to remove all packets so that nothing is uploaded to the database.
	 *         These types of {@link PacketListReceiver} can be useful to stop the uploading to a database for any reason (usually bad data).
	 */
	public PacketListReceiver getStatusEndPacketListReceiver() {
		return statusEndPacketListReceiver;
	}

	public static final class Builder {

		private PacketListReceiver statusPacketListReceiver = packets -> {};
		private EnvironmentUpdater environmentUpdater = EnvironmentUpdater.DO_NOTHING;
		private PacketListReceiver statusEndPacketListReceiver = packets -> {};
		private Builder() {
		}

		public Builder statusPacketListReceiver(PacketListReceiver statusPacketListReceiver) {
			this.statusPacketListReceiver = requireNonNull(statusPacketListReceiver);
			return this;
		}

		public Builder environmentUpdater(EnvironmentUpdater environmentUpdater) {
			this.environmentUpdater = requireNonNull(environmentUpdater);
			return this;
		}

		public Builder statusEndPacketListReceiver(PacketListReceiver statusEndPacketListReceiver) {
			this.statusEndPacketListReceiver = requireNonNull(statusEndPacketListReceiver);
			return this;
		}

		public DataRequesterResult build() {
			return new DataRequesterResult(
					statusPacketListReceiver,
					environmentUpdater,
					statusEndPacketListReceiver
			);
		}
	}
}
