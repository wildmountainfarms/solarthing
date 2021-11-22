package me.retrodaredevil.solarthing.rest.graphql.service;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLType;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.rest.graphql.SimpleQueryHandler;
import me.retrodaredevil.solarthing.type.alter.AlterPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.flag.FlagUtil;
import me.retrodaredevil.solarthing.type.alter.packets.FlagPacket;
import me.retrodaredevil.solarthing.type.alter.packets.ScheduledCommandPacket;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.retrodaredevil.solarthing.rest.graphql.service.SchemaConstants.DESCRIPTION_REQUIRED_SOURCE;

@GraphQLType
public class SolarThingGraphQLAlterService {

	private final SimpleQueryHandler simpleQueryHandler;

	public SolarThingGraphQLAlterService(SimpleQueryHandler simpleQueryHandler) {
		this.simpleQueryHandler = simpleQueryHandler;
	}

	@GraphQLQuery
	public SolarThingAlterQuery queryAlter(@GraphQLArgument(name = "sourceId", description = DESCRIPTION_REQUIRED_SOURCE) @NotNull String sourceId) {
		var packets = simpleQueryHandler.queryAlter(sourceId);
		return new SolarThingAlterQuery(packets);
	}

	public static class SolarThingAlterQuery {
		private final List<VersionedPacket<StoredAlterPacket>> packets;

		public SolarThingAlterQuery(List<VersionedPacket<StoredAlterPacket>> packets) {
			this.packets = packets;
		}
		private Stream<StoredAlterPacket> storedAlterPacketStream() {
			return packets.stream().map(VersionedPacket::getPacket);
		}

		@GraphQLQuery
		public @NotNull List<@NotNull ScheduledCommandPacket> scheduledCommands() {
			return packets.stream()
					.map(versionedPacket -> {
						AlterPacket alterPacket = versionedPacket.getPacket().getPacket();
						if (alterPacket instanceof ScheduledCommandPacket) {
							return (ScheduledCommandPacket) alterPacket;
						}
						return null;
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}

		@GraphQLQuery
		public @NotNull List<@NotNull String> activeFlagStrings() {
			Instant now = Instant.now();
			return FlagUtil.filterActivePackets(now, FlagUtil.mapToFlagPackets(storedAlterPacketStream()))
					.map(flagPacket -> flagPacket.getFlagData().getFlagName())
					.collect(Collectors.toList());
		}
		@GraphQLQuery
		public @NotNull List<@NotNull FlagPacket> flags(@GraphQLArgument(name = "mustBeActive", defaultValue = "true") boolean mustBeActive) {
			if (mustBeActive) {
				Instant now = Instant.now();
				return FlagUtil.filterActivePackets(now, FlagUtil.mapToFlagPackets(storedAlterPacketStream()))
						.collect(Collectors.toList());
			}
			return FlagUtil.mapToFlagPackets(storedAlterPacketStream())
					.collect(Collectors.toList());
		}
		@GraphQLQuery
		public @NotNull List<@NotNull FlagPacket> activeFlags(@GraphQLArgument(name = "dateMillis") long dateMillis) {
			Instant date = Instant.ofEpochMilli(dateMillis);
			return FlagUtil.filterActivePackets(date, FlagUtil.mapToFlagPackets(storedAlterPacketStream()))
					.collect(Collectors.toList());
		}
	}
}
