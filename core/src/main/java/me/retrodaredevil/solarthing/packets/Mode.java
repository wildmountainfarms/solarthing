package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;
import me.retrodaredevil.solarthing.annotations.NotNull;

/**
 * A mode represents the status of something. Mode values are usually defined in enums
 */
@JsonClassDescription("Represents many different types of modes")
public interface Mode {
	boolean isActive(int code);
	@GraphQLInclude("modeName")
	@NotNull String getModeName();
}
