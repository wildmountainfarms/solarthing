package me.retrodaredevil.solarthing.influxdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import me.retrodaredevil.solarthing.annotations.TagKeys;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.DocumentedPacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.identification.Identifiable;
import me.retrodaredevil.solarthing.packets.identification.Identifier;
import me.retrodaredevil.solarthing.packets.identification.SupplementaryIdentifier;

import java.util.*;

@UtilityClass
public final class PointUtil {
	private PointUtil() { throw new UnsupportedOperationException(); }

	public static Map<String, String> getTags(Packet packet) {
		Map<String, String> r = new HashMap<>();
		if(packet instanceof Identifiable){
			Identifier identifier = ((Identifiable) packet).getIdentifier();
			r.put("identifier", identifier.getRepresentation());
			if(identifier instanceof SupplementaryIdentifier){
				SupplementaryIdentifier supplementaryIdentifier = (SupplementaryIdentifier) identifier;
				r.put("identifier_supplementaryTo", supplementaryIdentifier.getSupplementaryTo().getRepresentation());
			}
		}
		if(packet instanceof DocumentedPacket){
			DocumentedPacket documentedPacket = (DocumentedPacket) packet;
			DocumentedPacketType type = documentedPacket.getPacketType();
			r.put("packetType", type.toString());
		}
		return r;
	}
	public static Collection<String> getTagKeys(Class<?> clazz){
		/*
		Why we have to do this: https://stackoverflow.com/questions/26910620/class-getannotations-getdeclaredannotations-returns-empty-array-for-subcla#26911089
		 */
		Collection<String> tagKeys = new HashSet<>();
		for(Class<?> interfaceClass : clazz.getInterfaces()){
			tagKeys.addAll(getTagKeys(interfaceClass));
		}
		TagKeys[] tagKeysAnnotations = clazz.getAnnotationsByType(TagKeys.class); // since Java 8, but that's fine
		for(TagKeys tagKeysAnnotation : tagKeysAnnotations){
			tagKeys.addAll(Arrays.asList(tagKeysAnnotation.value()));
		}
		return tagKeys;
	}
	public static Set<Map.Entry<String, ValueNode>> flattenJsonObject(ObjectNode object) {
		Map<String, ValueNode> r = new LinkedHashMap<>();
		for (Iterator<Map.Entry<String, JsonNode>> it = object.fields(); it.hasNext(); ) {
			Map.Entry<String, JsonNode> entry = it.next();
			String key = entry.getKey();
			JsonNode element = entry.getValue();
			if (element.isValueNode() && !element.isNull()) {
				r.put(key, (ValueNode) element);
			} else if(element.isObject()){
				Set<Map.Entry<String, ValueNode>> flat = flattenJsonObject((ObjectNode) element);
				for(Map.Entry<String, ValueNode> subEntry : flat){
					r.put(key + "." + subEntry.getKey(), subEntry.getValue());
				}
			}
			// ignore nulls and arrays
		}
		return r.entrySet();
	}
}
