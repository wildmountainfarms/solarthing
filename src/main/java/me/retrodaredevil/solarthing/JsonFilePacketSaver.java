package me.retrodaredevil.solarthing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.retrodaredevil.solarthing.packets.handling.PacketHandler;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.util.json.JsonFile;

import java.io.File;
import java.io.IOException;

@Deprecated
public class JsonFilePacketSaver implements PacketHandler {
	private final JsonFile jsonFile;

	public JsonFilePacketSaver(File file) throws IOException {
		if(!file.exists()){
			if(!file.createNewFile()){
				throw new IOException("Unable to create new file: '" + file.getAbsolutePath() + "'");
			}
		}
		jsonFile = new JsonFile(file);
		if(!jsonFile.isJson()){
			jsonFile.load(true);
		}
	}
	public JsonFilePacketSaver(String filePath) throws IOException {
		this(new File(filePath));
	}

	@Override
	public void handle(PacketCollection packetCollection, boolean wasInstant) {

		JsonElement el = jsonFile.getObject().get("packets"); // can be null
		if (el == null) {
			el = new JsonArray();
			jsonFile.getObject().add("packets", el);
		}
		if (el instanceof JsonArray) {
			JsonArray ar = (JsonArray) el;
			JsonObject add = (JsonObject) JsonFile.pa.parse(JsonFile.gson.toJson(packetCollection));
			ar.add(add);
		} else {
			throw new IllegalStateException("The JsonElement retrieved from packet must be a JsonArray");
		}
		try {
			jsonFile.save();
		} catch (IOException e){ // yes, we are going to ignore this to keep the program running
			e.printStackTrace();
			System.err.println("Unable to save to JSON file!");
		}
	}
}
