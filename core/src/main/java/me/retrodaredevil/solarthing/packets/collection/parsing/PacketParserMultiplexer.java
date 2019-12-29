package me.retrodaredevil.solarthing.packets.collection.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.solarthing.packets.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class PacketParserMultiplexer implements JsonPacketParser {
	private final List<JsonPacketParser> parserList;
	private final LenientType lenientType;

	public PacketParserMultiplexer(Collection<? extends JsonPacketParser> parserList, LenientType lenientType) {
		if(parserList.isEmpty()){
			throw new IllegalArgumentException("parserList cannot be empty!");
		}
		this.parserList = Collections.unmodifiableList(new ArrayList<>(parserList));
		this.lenientType = requireNonNull(lenientType);
	}
	public PacketParserMultiplexer(List<JsonPacketParser> parserList) {
		this(parserList, LenientType.FAIL_WHEN_UNHANDLED_WITH_EXCEPTION);
	}

	@Override
	public @Nullable Packet parsePacket(JsonNode packetNode) throws PacketParseException {
		PacketParseException exception = null;
		for(JsonPacketParser parser : parserList){
			try {
				Packet packet = parser.parsePacket(packetNode);
				if(packet != null){
					return packet;
				}
			} catch(PacketParseException ex){
				if(lenientType == LenientType.FAIL_WHEN_UNHANDLED_AND_ANY_EXCEPTION){
					throw ex;
				}
				exception = ex;
			}
		}
		if(lenientType == LenientType.FULLY_LENIENT){
			return null;
		}
		if(exception != null){
			throw exception;
		}
		if(lenientType == LenientType.FAIL_WHEN_UNHANDLED_WITH_EXCEPTION){
			return null;
		}
		if(lenientType == LenientType.FAIL_WHEN_UNHANDLED){
			throw new PacketParseException("parsePacket went unhandled!");
		}
		if(lenientType == LenientType.FAIL_WHEN_UNHANDLED_AND_ANY_EXCEPTION){
			throw new AssertionError("parserList must have been empty... parserList=" + parserList);
		}
		throw new AssertionError("unknown LenientType: " + lenientType);
	}
	public enum LenientType {
		FULLY_LENIENT,
		/** Recommended. This fails when the packets went unhandled and at least one of the parsers threw an exception.*/
		FAIL_WHEN_UNHANDLED_WITH_EXCEPTION,
		/** This fails when any packets went unhandled.*/
		FAIL_WHEN_UNHANDLED,
		/** The least lenient. This fails if any parser throws an exception or */
		FAIL_WHEN_UNHANDLED_AND_ANY_EXCEPTION
	}
}
