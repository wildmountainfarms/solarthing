package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.packets.collection.PacketGroups;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * This is usually used as a unique key that groups a fragmentId and an {@link Identifier} together. This inherits
 * {@link Comparable<IdentifierFragment>} to allow a group of {@link IdentifierFragment}s to be compared or sorted first by their
 * fragmentId, next by {@link Identifier#compareTo(Identifier)}
 */
public final class IdentifierFragment implements Comparable<IdentifierFragment> {
	private final Integer fragmentId;
	private final Identifier identifier;

	public IdentifierFragment(Integer fragmentId, Identifier identifier) {
		this.fragmentId = fragmentId;
		requireNonNull(this.identifier = identifier);
	}
	public @Nullable Integer getFragmentId() {
		return fragmentId;
	}
	public @NotNull Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IdentifierFragment that = (IdentifierFragment) o;
		return Objects.equals(fragmentId, that.fragmentId) &&
				identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fragmentId, identifier);
	}

	@Override
	public int compareTo(@NotNull IdentifierFragment identifierFragment) {
		int compare = PacketGroups.DEFAULT_FRAGMENT_ID_COMPARATOR.compare(fragmentId, identifierFragment.fragmentId);
		if (compare != 0) {
			return compare;
		}
		return identifier.compareTo(identifierFragment.identifier);
	}
}
