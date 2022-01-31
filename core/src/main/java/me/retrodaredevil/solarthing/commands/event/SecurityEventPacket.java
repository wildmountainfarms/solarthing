package me.retrodaredevil.solarthing.commands.event;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.WorkInProgress;
import me.retrodaredevil.solarthing.marker.EventPacket;
import me.retrodaredevil.solarthing.packets.collection.StoredIdentifier;

@WorkInProgress
public interface SecurityEventPacket extends EventPacket {
	/*
	The reason that we use StoredIdentifier rather than UpdateTokens alongside a document ID is because
	we are referring to a document that is in solarthing-open, which is a MillisDatabase. MillisDatabase is setup
	to deal with StoredPacketGroups, rather than VersionedPacket<StoredPacketGroup>s. Plus, UpdateTokens are
	designed to do just that, update, which we don't need to do.

	Although it may not seem like it, StoredIdentifiers are actually higher level than UpdateTokens, which is why
	we StoredIdentifiers.
	 */

	// TODO Make sure StoredIdentifiers can be serialized to JSON
	@NotNull StoredIdentifier getStoredIdentifier();
}
