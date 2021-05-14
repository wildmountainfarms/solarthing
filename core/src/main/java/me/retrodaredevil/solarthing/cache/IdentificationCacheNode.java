package me.retrodaredevil.solarthing.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.IdentifierRepFragment;

import static java.util.Objects.requireNonNull;

public class IdentificationCacheNode<T extends IdentificationCacheData> {
	private final IdentifierRepFragment identifierRepFragment;
	private final T data;

	@JsonCreator
	public IdentificationCacheNode(
			@JsonProperty("fragment") int fragmentId,
			@JsonProperty("identifier") String identifierRepresentation,
			@JsonProperty("data") T data) {
		this.identifierRepFragment = new IdentifierRepFragment(fragmentId, identifierRepresentation);
		requireNonNull(this.data = data);
	}

	public int getFragmentId() { return identifierRepFragment.getFragmentId(); }
	public @NotNull String getIdentifierRepresentation() { return identifierRepFragment.getIdentifierRepresentation(); }
	public @NotNull IdentifierRepFragment getIdentifierRepFragment() { return identifierRepFragment; }

	public @NotNull T getData() { return data; }
}
