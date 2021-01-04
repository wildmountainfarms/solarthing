package me.retrodaredevil.solarthing.misc.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;

@JsonDeserialize(as = ImmutableExceptionErrorPacket.class)
@JsonTypeName("EXCEPTION_ERROR")
@JsonExplicit
public interface ExceptionErrorPacket extends ErrorPacket, Identifiable {

	@Override
	default @NotNull ErrorPacketType getPacketType() {
		return ErrorPacketType.EXCEPTION_ERROR;
	}
	@JsonProperty("exceptionName")
	String getExceptionName();
	@JsonProperty("message")
	String getMessage();

	/**
	 *
	 * @return A string that is unique for each location that an exception is able to be caught
	 */
	@JsonProperty("exceptionCatchLocationIdentifier")
	String getExceptionCatchLocationIdentifier();

	/**
	 * @return A string that is unique across a single fragment during a single instant.
	 */
	@JsonProperty("exceptionInstanceIdentifier")
	String getExceptionInstanceIdentifier();

	@GraphQLInclude("identifier")
	@Override
	@NotNull ExceptionErrorIdentifier getIdentifier();
}
