package me.retrodaredevil.solarthing.packets.identification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an {@link Identifier} where the only difference between similar {@link Identifier}s
 * is a number defined by the user to differentiate them. By default (and to maintain compatibility), the number
 * is and should be 0 if not defined by the user.
 * <p>
 * To have the option of reverting the use of this, a number of 0 should not be serialized, as it will become 0 upon deserialization
 * as 0 is the default value.
 */
public interface NumberedIdentifier extends Identifier {
	int DEFAULT_NUMBER = 0;

	@JsonInclude(value = JsonInclude.Include.NON_DEFAULT) // non-default won't include a value of 0
	@JsonProperty("number")
	int getNumber();
}
