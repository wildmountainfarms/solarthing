package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import javax.validation.constraints.NotNull;

public interface Identifiable {
	@GraphQLInclude("identifier")
	@NotNull Identifier getIdentifier();
}
