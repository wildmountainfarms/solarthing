package me.retrodaredevil.solarthing.packets.identification;

import me.retrodaredevil.solarthing.annotations.GraphQLInclude;

import javax.validation.constraints.NotNull;

public interface SupplementaryIdentifiable extends Identifiable {
	@GraphQLInclude("identifier") // TODO figure out why this has to be added here too
	@Override
	@NotNull SupplementaryIdentifier getIdentifier();
}
