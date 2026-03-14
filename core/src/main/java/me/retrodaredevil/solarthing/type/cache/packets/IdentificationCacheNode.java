package me.retrodaredevil.solarthing.type.cache.packets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.type.cache.packets.data.IdentificationCacheData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@JsonExplicit
@NullMarked
public class IdentificationCacheNode<T extends IdentificationCacheData> {
	private final int fragmentId;
	private final T data;

	@JsonCreator
	public IdentificationCacheNode(
			@JsonProperty(value = "fragmentId", required = true) int fragmentId,
			@JsonProperty(value = "data", required = true) T data) {
		this.fragmentId = fragmentId;
		this.data = requireNonNull(data);
	}

	@JsonProperty("fragmentId")
	public int getFragmentId() { return fragmentId; }

	// TODO remove NonNull
	@JsonProperty("data")
	public @NonNull T getData() { return data; }
}
