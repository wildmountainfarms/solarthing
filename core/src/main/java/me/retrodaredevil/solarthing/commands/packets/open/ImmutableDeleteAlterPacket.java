package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.database.UpdateToken;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ImmutableDeleteAlterPacket implements DeleteAlterPacket {
	private final String documentIdToDelete;
	private final UpdateToken updateToken;

	@JsonCreator
	public ImmutableDeleteAlterPacket(
			@JsonProperty(value = "documentIdToDelete", required = true) String documentIdToDelete,
			@JsonProperty(value = "updateToken", required = true) UpdateToken updateToken) {
		this.documentIdToDelete = requireNonNull(documentIdToDelete);
		this.updateToken = requireNonNull(updateToken);
	}

	@Override
	public String getDocumentIdToDelete() {
		return documentIdToDelete;
	}

	@Override
	public UpdateToken getUpdateToken() {
		return updateToken;
	}

	@Override
	public String getUniqueString() {
		return "DeleteAlterPacket(" +
				"documentIdToDelete=" + documentIdToDelete +
				", updateToken=" + updateToken +
				')';
	}

	@Override
	public String toString() {
		return getUniqueString();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImmutableDeleteAlterPacket that = (ImmutableDeleteAlterPacket) o;
		return documentIdToDelete.equals(that.documentIdToDelete) && updateToken.equals(that.updateToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(documentIdToDelete, updateToken);
	}
}
