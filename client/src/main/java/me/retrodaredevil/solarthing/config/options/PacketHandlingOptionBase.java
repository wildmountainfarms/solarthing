package me.retrodaredevil.solarthing.config.options;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PacketHandlingOptionBase implements PacketHandlingOption {
	private JsonArray databases = null;
	private String source = "default";
	private Integer fragment = null;
	private Integer unique = null;

	@Override
	public List<File> getDatabaseConfigurationFiles() {
		List<File> r = new ArrayList<>(databases.size());
		for(JsonElement element : databases){
			r.add(new File(element.getAsString()));
		}
		return r;
	}

	@Override
	public String getSourceId() {
		return source;
	}

	@Override
	public Integer getFragmentId() {
		return fragment;
	}

	@Override
	public Integer getUniqueIdsInOneHour() {
		return unique;
	}
}
