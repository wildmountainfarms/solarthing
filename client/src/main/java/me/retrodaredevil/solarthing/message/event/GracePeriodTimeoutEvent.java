package me.retrodaredevil.solarthing.message.event;

import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public abstract class GracePeriodTimeoutEvent implements MessageEvent {
	private final long gracePeriod;
	private final long timeout;

	private Long startTime = null;
	private Long lastSend = null;

	protected GracePeriodTimeoutEvent(String gracePeriodDurationString, String timeoutDurationString) {
		this(Duration.parse(gracePeriodDurationString).toMillis(), Duration.parse(timeoutDurationString).toMillis());
	}

	protected GracePeriodTimeoutEvent(long gracePeriod, long timeout) {
		this.gracePeriod = gracePeriod;
		this.timeout = timeout;
	}

	protected final Long getDurationMillis() {
		Long startTime = this.startTime;
		if (startTime == null) {
			return null;
		}
		return System.currentTimeMillis() - startTime;
	}
	protected final String getPrettyDurationString() {
		long millis = requireNonNull(getDurationMillis());
		return TimeUtil.millisToPrettyString(millis);
	}

	@Override
	public final void run(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current) {
		Runnable trigger = createDesiredTrigger(sender, previous, current);
		if (trigger != null) {
			final Long startTime = this.startTime;
			long now = System.currentTimeMillis();
			if (startTime == null) {
				this.startTime = now;
			} else if (startTime + gracePeriod < now) {
				final Long lastSend = this.lastSend;
				if (lastSend == null || lastSend + timeout < now) {
					this.lastSend = now;
					trigger.run();
				}
			}
		} else {
			startTime = null;
		}
	}

	protected abstract @Nullable Runnable createDesiredTrigger(MessageSender sender, FragmentedPacketGroup previous, FragmentedPacketGroup current);
}
