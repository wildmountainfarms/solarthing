package me.retrodaredevil.solarthing.graphql.packets;

import me.retrodaredevil.solarthing.packets.identification.Identifiable;

import static java.util.Objects.requireNonNull;

public class IdentifierFilter implements PacketFilter {
	public enum DefaultAction {
		KEEP,
		NO_KEEP,
		ERROR
	}
	private final String identifierRepresentation;
	private final DefaultAction defaultAction;

	public IdentifierFilter(String identifierRepresentation, DefaultAction defaultAction) {
		requireNonNull(this.identifierRepresentation = identifierRepresentation);
		requireNonNull(this.defaultAction = defaultAction);
	}
	public IdentifierFilter(String identifierRepresentation) {
		this(identifierRepresentation, DefaultAction.NO_KEEP);
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		Object packet = packetNode.getPacket();
		if (packet instanceof Identifiable) {
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
