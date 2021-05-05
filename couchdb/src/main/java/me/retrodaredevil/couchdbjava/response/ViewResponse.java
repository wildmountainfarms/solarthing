package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.JacksonJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.RawListDeserializer;
import me.retrodaredevil.couchdbjava.json.jackson.RawListSerializer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ViewResponse {
	private final int offset;
	private final List<JacksonJsonData> rowsJsonList;
	private final int totalRows;
	private final String updateSequence;

	@JsonCreator
	public ViewResponse(
			@JsonProperty(value = "offset", required = true) int offset,
			@JsonProperty(value = "rows", required = true) List<JacksonJsonData> rowsJsonList,
			@JsonProperty(value = "total_rows", required = true) int totalRows,
			@JsonProperty("update_seq") String updateSequence) {
		this.offset = offset;
		this.rowsJsonList = Collections.unmodifiableList(rowsJsonList);
		this.totalRows = totalRows;
		this.updateSequence = updateSequence;
	}

	@JsonProperty("offset")
	public int getOffset() {
		return offset;
	}

	@JsonProperty("rows")
	public List<? extends JsonData> getRowsJsonList() {
		return rowsJsonList;
	}

	@JsonProperty("total_rows")
	public int getTotalRows() {
		return totalRows;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("update_seq")
	public @Nullable String getUpdateSequence() {
		return updateSequence;
	}


//	public static class DocumentEntry {
//		private final String id;
//		private final String keyJson;
//		private final String valueJson;
//	}
}
