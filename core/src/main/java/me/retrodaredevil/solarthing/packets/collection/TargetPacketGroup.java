package me.retrodaredevil.solarthing.packets.collection;

import me.retrodaredevil.solarthing.packets.instance.TargetPacket;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TargetPacketGroup extends SourcedPacketGroup, TargetPacket, BasicPacketGroup {
}
