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
import org.jspecify.annotations.NullMarked;

@JsonTypeName("DATA_INFO")
@NullMarked
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

	// TODO remove NonNull
	@Override
	public @NonNull TargetedMetaPacketType getPacketType() {
		return TargetedMetaPacketType.DATA_INFO;
	}

	// TODO remove NonNull
	@JsonProperty("name")
	public @NonNull String getName() {
		return name;
	}

	// TODO remove NonNull
	@JsonProperty("description")
	public @NonNull String getDescription() {
		return description;
	}

	// TODO remove NonNull
	@JsonProperty("location")
	public @NonNull String getLocation() {
		return location;
	}

	// TODO remove NonNull
	@Override
	public @NonNull DataIdentifier getIdentifier() {
		return dataIdentifier;
	}

	// TODO remove NonNull
	@Override
	public @NonNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public int getDataId() {
		return dataId;
	}
}
