package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ImmutableFlagAliasAddPacket.class)
@JsonTypeName("FLAG_ALIAS_ADD")
@JsonExplicit
@NullMarked
public interface FlagAliasAddPacket extends CommandOpenPacket {
	@Override
	default CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.FLAG_ALIAS_ADD;
	}

	@JsonProperty("flagAliasData")
	FlagAliasData getFlagAliasData();
}
