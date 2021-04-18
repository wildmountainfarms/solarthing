package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.graphql.packets.nodes.PacketNode;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;

import static java.util.Objects.requireNonNull;

public class IdentifierFilter implements PacketFilter {
	public enum DefaultAction {
		KEEP,
		NO_KEEP,
		ERROR
	}
	private final String identifierRepresentation;
	private final boolean acceptSupplementary;
	private final DefaultAction defaultAction;

	public IdentifierFilter(String identifierRepresentation, boolean acceptSupplementary, DefaultAction defaultAction) {
		requireNonNull(this.identifierRepresentation = identifierRepresentation);
		this.acceptSupplementary = acceptSupplementary;
		requireNonNull(this.defaultAction = defaultAction);
	}
	public IdentifierFilter(String identifierRepresentation, boolean acceptSupplementary) {
		this(identifierRepresentation, acceptSupplementary, DefaultAction.NO_KEEP);
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		Object packet = packetNode.getPacket();
		if (packet instanceof Identifiable) {
			if (acceptSupplementary && packet instanceof SupplementaryIdentifiable) {
				SupplementaryIdentifiable supplementaryIdentifiable = (SupplementaryIdentifiable) packet;
				if (supplementaryIdentifiable.getIdentifier().getSupplementaryTo().getRepresentation().equals(identifierRepresentation)) {
					return true;
				}
			}
			return ((Identifiable) packet).getIdentifier().getRepresentation().equals(identifierRepresentation);
		}
		switch (defaultAction) {
			case KEEP:
				return true;
			case NO_KEEP:
				return false;
			case ERROR:
				throw new IllegalArgumentException("packet in packetNode wasn't Identifiable! packetNode: " + packetNode);
			default:
				throw new AssertionError("unknown defaultAction=" + defaultAction);
		}
	}
}
