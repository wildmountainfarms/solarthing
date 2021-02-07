package me.retrodaredevil.solarthing.packets;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

/**
 * A mode represents the status of something. Mode values are usually defined in enums
 */
@JsonClassDescription("Represents many different types of modes")
public interface Mode {
	boolean isActive(int code);
	@GraphQLInclude("modeName")
	String getModeName();
}
