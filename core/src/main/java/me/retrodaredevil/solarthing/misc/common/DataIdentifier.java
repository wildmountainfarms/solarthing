package me.retrodaredevil.solarthing.misc.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

import java.util.Objects;

public final class DataIdentifier implements Identifier {
	private final int dataId;

	public DataIdentifier(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "DataIdentifier(dataId=" + dataId + ")";
	}

	@JsonProperty("dataId")
	public int getDataId() {
		return dataId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DataIdentifier that = (DataIdentifier) o;
		return getDataId() == that.getDataId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getDataId());
	}
}
