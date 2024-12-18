package me.retrodaredevil.solarthing.chatbot;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.cache.DatabaseCache;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatData;
import me.retrodaredevil.solarthing.type.event.feedback.HeartbeatPacket;
import me.retrodaredevil.solarthing.util.TimeRange;
import me.retrodaredevil.solarthing.util.TimeUtil;
import me.retrodaredevil.solarthing.util.heartbeat.HeartbeatIdentifier;
import me.retrodaredevil.solarthing.util.heartbeat.HeartbeatNode;
import me.retrodaredevil.solarthing.util.sync.ResourceManager;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class HeartbeatCommandChatBotHandler implements ChatBotHandler {

	private final ResourceManager<? extends DatabaseCache> eventDatabaseCacheManager;

	public HeartbeatCommandChatBotHandler(ResourceManager<? extends DatabaseCache> eventDatabaseCacheManager) {
		requireNonNull(this.eventDatabaseCacheManager = eventDatabaseCacheManager);
	}


	private static String buildLineForHeartbeat(int fragmentId, HeartbeatData data, Instant lastHeartbeat) {
		return data.getDisplayName() + " from fragment " + fragmentId + " last heartbeat at " + TimeUtil.instantToSlackDateSeconds(lastHeartbeat);
	}

	@SuppressWarnings("DuplicatedCode") // I really don't care at the moment. Extracting a method will make this less readable IMO
	private void displayHeartbeats(MessageSender messageSender) {
		Map<HeartbeatIdentifier, HeartbeatNode> map = new HashMap<>();
		eventDatabaseCacheManager.access(eventDatabaseCache -> {
			eventDatabaseCache.createCachedPacketsInRangeStream(TimeRange.createAfter(Instant.now().minus(Duration.ofHours(3))), false)
					.map(PacketGroups::parseToInstancePacketGroupRequireNoDefaults) // We are accessing new packets, so they should have source ID and fragment ID
					.forEach(instancePacketGroup -> {
						long dateMillis = instancePacketGroup.getDateMillis();
						for (Packet packet : instancePacketGroup.getPackets()) {
							if (packet instanceof HeartbeatPacket heartbeatPacket) {
								HeartbeatIdentifier identifier = new HeartbeatIdentifier(heartbeatPacket.getData().getIdentifier(), instancePacketGroup.getFragmentId());
								HeartbeatNode node = new HeartbeatNode(dateMillis, heartbeatPacket);
								map.put(identifier, node);
							}
						}
					});
		});
		List<Map.Entry<HeartbeatIdentifier, HeartbeatNode>> activeHeartbeats = new ArrayList<>();
		List<Map.Entry<HeartbeatIdentifier, HeartbeatNode>> expectedHeartbeats = new ArrayList<>();
		List<Map.Entry<HeartbeatIdentifier, HeartbeatNode>> deadHeartbeats = new ArrayList<>();

		Instant now = Instant.now();
		map.entrySet().stream()
				.sorted(Comparator.comparingLong(entry -> entry.getValue().getDateMillis())) // iterate over oldest first
				.forEach(entry -> {
					HeartbeatData data = entry.getValue().getHeartbeatPacket().getData();
					Instant lastHeartbeat = Instant.ofEpochMilli(entry.getValue().getDateMillis());

					if (lastHeartbeat.plus(data.getExpectedDurationToNextHeartbeat()).compareTo(now) > 0) {
						activeHeartbeats.add(entry);
					} else if (lastHeartbeat.plus(data.getExpectedDurationToNextHeartbeat()).plus(data.getBufferDuration()).compareTo(now) > 0) {
						expectedHeartbeats.add(entry);
					} else {
						deadHeartbeats.add(entry);
					}
				});

		StringBuilder builder = new StringBuilder();

		if (!activeHeartbeats.isEmpty()) {
			builder.append("Active:\n");
			activeHeartbeats.forEach(entry -> {
				int fragmentId = entry.getKey().getFragmentId();
				HeartbeatData data = entry.getValue().getHeartbeatPacket().getData();
				Instant lastHeartbeat = Instant.ofEpochMilli(entry.getValue().getDateMillis());
				builder.append("  ").append(buildLineForHeartbeat(fragmentId, data, lastHeartbeat)).append('\n');
			});
		}
		if (!expectedHeartbeats.isEmpty()) {
			builder.append("Expected:\n");
			expectedHeartbeats.forEach(entry -> {
				int fragmentId = entry.getKey().getFragmentId();
				HeartbeatData data = entry.getValue().getHeartbeatPacket().getData();
				Instant lastHeartbeat = Instant.ofEpochMilli(entry.getValue().getDateMillis());
				builder.append("  ").append(buildLineForHeartbeat(fragmentId, data, lastHeartbeat)).append('\n');
			});
		}
		if (!deadHeartbeats.isEmpty()) {
			builder.append("Dead:\n");
			deadHeartbeats.forEach(entry -> {
				int fragmentId = entry.getKey().getFragmentId();
				HeartbeatData data = entry.getValue().getHeartbeatPacket().getData();
				Instant lastHeartbeat = Instant.ofEpochMilli(entry.getValue().getDateMillis());
				builder.append("  ").append(buildLineForHeartbeat(fragmentId, data, lastHeartbeat)).append('\n');
			});
		}

		String message = builder.toString();
		if (message.isEmpty()) {
			messageSender.sendMessage("No heartbeats");
		} else {
			messageSender.sendMessage(message);
		}

	}

	@Override
	public boolean handleMessage(Message message, MessageSender messageSender) {
		String text = message.getText();
		String lowerText = text.toLowerCase(Locale.ENGLISH);
		String[] split = lowerText.split(" ");
		if (split.length == 0) {
			return false;
		}
		if (split[0].equals("heartbeat")) {
			displayHeartbeats(messageSender);
			return true;
		}
		return false;
	}

	@Override
	public @NotNull List<String> getHelpLines(Message helpMessage) {
		return Arrays.asList(
				"heartbeat"
		);
	}
}
