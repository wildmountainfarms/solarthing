package me.retrodaredevil.solarthing.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;

import java.util.List;

public interface IdentificationCacheDataPacket<T extends IdentificationCacheData> extends CacheDataPacket {
	@JsonProperty("nodes")
	List<IdentificationCacheNode<T>> getNodes();

	default @Nullable IdentificationCacheNode<T> getNodeOrNull(int fragmentId, Identifier identifier) {
		for (IdentificationCacheNode<T> node : getNodes()) {
			if (node.getFragmentId() == fragmentId && node.getData().getIdentifier().equals(identifier)) {
				return node;
			}
		}
		return null;
	}
	default @Nullable T getDataOrNull(int fragmentId, Identifier identifier) {
		IdentificationCacheNode<T> node = getNodeOrNull(fragmentId, identifier);
		if (node == null) {
			return null;
		}
		return node.getData();
	}
}
