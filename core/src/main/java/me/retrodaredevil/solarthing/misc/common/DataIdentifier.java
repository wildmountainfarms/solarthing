package me.retrodaredevil.solarthing.misc.common;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

public final class DataIdentifier implements Identifier {
	private final int dataId;

	public DataIdentifier(int dataId) {
		this.dataId = dataId;
	}

	@Override
	public @NotNull String getRepresentation() {
		return "DataIdentifier(dataId=" + dataId + ")";
	}

	public int getDataId() {
		return dataId;
	}
}
