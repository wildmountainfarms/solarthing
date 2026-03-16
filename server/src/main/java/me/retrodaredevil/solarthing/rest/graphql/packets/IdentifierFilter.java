package me.retrodaredevil.solarthing.rest.graphql.packets;

import me.retrodaredevil.solarthing.rest.graphql.packets.nodes.PacketNode;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifiable;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
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
		this.identifierRepresentation = requireNonNull(identifierRepresentation);
		this.acceptSupplementary = acceptSupplementary;
		this.defaultAction = requireNonNull(defaultAction);
	}
	public IdentifierFilter(String identifierRepresentation, boolean acceptSupplementary) {
		this(identifierRepresentation, acceptSupplementary, DefaultAction.NO_KEEP);
	}

	@Override
	public boolean keep(PacketNode<?> packetNode) {
		Object packet = packetNode.getPacket();
		if (packet instanceof Identifiable identifiable) {
			return (acceptSupplementary
					&& packet instanceof SupplementaryIdentifiable supplementaryIdentifiable
					&& supplementaryIdentifiable.getIdentifier().getSupplementaryTo().getRepresentation().equals(identifierRepresentation))
					|| identifiable.getIdentifier().getRepresentation().equals(identifierRepresentation);
		}
		return switch (defaultAction) {
			case KEEP -> true;
			case NO_KEEP -> false;
			case ERROR -> throw new IllegalArgumentException("packet in packetNode wasn't Identifiable! packetNode: " + packetNode);
		};
	}
}
