package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.type.alter.flag.FlagAliasData;
import org.jspecify.annotations.NonNull;

@JsonDeserialize(as = ImmutableFlagAliasAddPacket.class)
@JsonTypeName("FLAG_ALIAS_ADD")
@JsonExplicit
public interface FlagAliasAddPacket extends CommandOpenPacket {
	@Override
	default @NonNull CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.FLAG_ALIAS_ADD;
	}

	@JsonProperty("flagAliasData")
	@NonNull FlagAliasData getFlagAliasData();
}
