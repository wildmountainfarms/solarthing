package me.retrodaredevil.solarthing.rest.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import org.jspecify.annotations.NullMarked;

@JsonExplicit
@NullMarked
public final class CommandRequestResponse {
	private final boolean success;

	public CommandRequestResponse(boolean success) {
		this.success = success;
	}

	@JsonProperty("success")
	public boolean isSuccess() {
		return success;
	}
}
