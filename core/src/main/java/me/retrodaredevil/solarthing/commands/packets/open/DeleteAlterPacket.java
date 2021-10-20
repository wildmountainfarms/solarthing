package me.retrodaredevil.solarthing.commands.packets.open;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.database.UpdateToken;

@JsonDeserialize(as = ImmutableDeleteAlterPacket.class)
@JsonTypeName("DELETE_ALTER")
@JsonExplicit
public interface DeleteAlterPacket extends CommandOpenPacket {
	@Override
	default @NotNull CommandOpenPacketType getPacketType() {
		return CommandOpenPacketType.DELETE_ALTER;
	}
	/*
	Note that should we change from CouchDB to another database in the future, this class may have to be changed,
	or it might stay the same because of our use of UpdateToken
	 */

	@JsonProperty("documentIdToDelete")
	@NotNull String getDocumentIdToDelete();

	@JsonProperty("updateToken")
	@NotNull UpdateToken getUpdateToken();
}
