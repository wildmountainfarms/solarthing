package me.retrodaredevil.solarthing.misc.common.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacket;
import me.retrodaredevil.solarthing.type.closed.meta.TargetedMetaPacketType;
import org.jspecify.annotations.NonNull;
import me.retrodaredevil.solarthing.misc.common.DataIdentifiable;
import me.retrodaredevil.solarthing.misc.common.DataIdentifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

@JsonTypeName("DATA_INFO")
public final class DataMetaPacket implements TargetedMetaPacket, DataIdentifiable {
	private final int dataId;
	private final String name;
	private final String description;
	private final String location;

	private final DataIdentifier dataIdentifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	public DataMetaPacket(
			@JsonProperty("dataId") int dataId,
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("location") String location) {
		this.dataId = dataId;
		this.name = name;
		this.description = description;
		this.location = location;
		dataIdentifier = new DataIdentifier(dataId);
		identityInfo = new DataMetaIdentityInfo(name, dataId);
	}

	@Override
	public @NonNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.DATA_INFO;
	}

	@JsonProperty("name")
	public @NonNull String getName() {
		return name;
	}

	@JsonProperty("description")
	public @NonNull String getDescription() {
		return description;
	}

	@JsonProperty("location")
	public @NonNull String getLocation() {
		return location;
	}

	@Override
	public @NonNull DataIdentifier getIdentifier() {
		return dataIdentifier;
	}

	@Override
	public @NonNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getDataId() {
		return dataId;
	}
}
