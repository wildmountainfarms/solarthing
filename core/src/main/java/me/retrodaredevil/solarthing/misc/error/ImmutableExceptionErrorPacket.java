package me.retrodaredevil.solarthing.misc.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

public class ImmutableExceptionErrorPacket implements ExceptionErrorPacket {
	private final String exceptionName;
	private final String message;
	private final String exceptionCatchLocationIdentifier;
	private final String exceptionInstanceIdentifier;

	private final ExceptionErrorIdentifier identifier;
	private final IdentityInfo identityInfo;

	@JsonCreator
	public ImmutableExceptionErrorPacket(
			@JsonProperty(value = "exceptionName", required = true) String exceptionName,
			@JsonProperty(value = "message", required = true) String message,
			@JsonProperty(value = "exceptionCatchLocationIdentifier", required = true) String exceptionCatchLocationIdentifier,
			@JsonProperty(value = "exceptionInstanceIdentifier", required = true) String exceptionInstanceIdentifier) {
		requireNonNull(this.exceptionName = exceptionName);
		requireNonNull(this.message = message);
		requireNonNull(this.exceptionCatchLocationIdentifier = exceptionCatchLocationIdentifier);
		requireNonNull(this.exceptionInstanceIdentifier = exceptionInstanceIdentifier);

		identifier = new ExceptionErrorIdentifier(exceptionCatchLocationIdentifier, exceptionInstanceIdentifier);
		identityInfo = new ExceptionErrorIdentityInfo(identifier);
	}

	@NotNull
	@Override
	public Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public @NotNull IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public String getExceptionName() {
		return exceptionName;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getExceptionCatchLocationIdentifier() {
		return exceptionCatchLocationIdentifier;
	}

	@Override
	public String getExceptionInstanceIdentifier() {
		return exceptionInstanceIdentifier;
	}
}
