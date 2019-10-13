package me.retrodaredevil.solarthing.packets;

/**
 * Represents the type of a certain packet
 * <p>
 * Usually implemented by an enum representing types of a certain packet category. The implementor of this interface
 * should not override the {@link #toString()} method because {@link Enum#valueOf(Class, String)} may be used
 */
public interface DocumentedPacketType {
}
