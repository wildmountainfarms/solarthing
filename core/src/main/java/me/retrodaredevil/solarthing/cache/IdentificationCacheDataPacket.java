package me.retrodaredevil.solarthing.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.Nullable;

import java.util.List;

public interface IdentificationCacheDataPacket<T extends IdentificationCacheData> extends CacheDataPacket {
	@JsonProperty("nodes")
	List<IdentificationCacheNode<T>> getNodes();

	default @Nullable IdentificationCacheNode<T> getNodeOrNull(int fragmentId, String identifierRepresentation) {
		for (IdentificationCacheNode<T> node : getNodes()) {
			if (node.getFragmentId() == fragmentId && node.getIdentifierRepresentation().equals(identifierRepresentation)) {
				return node;
			}
		}
		return null;
	}
	default @Nullable T getDataOrNull(int fragmentId, String identifierRepresentation) {
		IdentificationCacheNode<T> node = getNodeOrNull(fragmentId, identifierRepresentation);
		if (node == null) {
			return null;
		}
		return node.getData();
	}
}
