package me.retrodaredevil.solarthing.datasource.endpoint.annotations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.datasource.util.ColorDeserializer;

import java.awt.*;

public class Annotation {
	@JsonDeserialize
	private final String name;

	@JsonDeserialize
	private final String datasource;

	@JsonDeserialize(using = ColorDeserializer.class)
	private final Color iconColor;

	@JsonDeserialize
	private final boolean enable;

	@JsonDeserialize
	private final String query;

	private Annotation(){
		this(null, null, null, false, null);
	}

	public Annotation(String name, String datasource, Color iconColor, boolean enable, String query) {
		this.name = name;
		this.datasource = datasource;
		this.iconColor = iconColor;
		this.enable = enable;
		this.query = query;
	}

	public String getName() {
		return name;
	}

	public String getDatasource() {
		return datasource;
	}

	public Color getIconColor() {
		return iconColor;
	}

	public boolean isEnable() {
		return enable;
	}

	public String getQuery() {
		return query;
	}

	@Override
	public String toString() {
		return "Annotation(" +
				"name='" + name + '\'' +
				", datasource='" + datasource + '\'' +
				", iconColor=" + iconColor +
				", enable=" + enable +
				", query='" + query + '\'' +
				')';
	}
}
