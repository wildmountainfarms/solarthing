package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ViewQueryParamsBuilder {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Boolean conflicts = null;
	private Boolean descending = null;
	private JsonData endKeyJson = null;
	private String endKeyDocId = null;
	private Boolean group = null;
	private Integer groupLevel = null;
	private Boolean includeDocs = null;
	private Boolean attachments = null;
	private Boolean includeEncodingInfo = null;
	private Boolean inclusiveEnd = null;
	private JsonData keyJson = null;
	private List<JsonData> keysJsonList = null;
	private Integer limit = null;
	private Boolean reduce = null;
	private Integer skip = null;
	private Boolean sorted = null;
	private Boolean stable = null;
	// "stale" is deprecated
	private JsonData startKeyJson;
	private String startKeyDocId;
	private Update update = null;
	private Boolean updateSequence = null;

	public ViewQueryParams build() {
		return new ViewQueryParams(conflicts, descending, endKeyJson, endKeyDocId, group, groupLevel, includeDocs, attachments, includeEncodingInfo,
				inclusiveEnd, keyJson, keysJsonList, limit, reduce, skip, sorted, stable, startKeyJson, startKeyDocId, update, updateSequence);
	}

	public ViewQueryParamsBuilder conflicts(Boolean conflicts) {
		this.conflicts = conflicts;
		if (Boolean.TRUE.equals(conflicts)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryParamsBuilder descending(Boolean descending) { this.descending = descending; return this; }
	public ViewQueryParamsBuilder descending() { return descending(true); }
	// endKey defined below
	public ViewQueryParamsBuilder endKeyDocId(String endKeyDocId) { this.endKeyDocId = endKeyDocId; return this; }
	public ViewQueryParamsBuilder group(Boolean group) { this.group = group; return this; }
	public ViewQueryParamsBuilder groupLevel(Integer groupLevel) {
		this.groupLevel = groupLevel;
		if (groupLevel != null) {
			group(true);
		}
		return this;
	}
	public ViewQueryParamsBuilder includeDocs(Boolean includeDocs) { this.includeDocs = includeDocs; return this; }
	public ViewQueryParamsBuilder attachments(Boolean attachments) {
		this.attachments = attachments;
		if (Boolean.TRUE.equals(attachments)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryParamsBuilder includeEncodingInfo(Boolean includeEncodingInfo) {
		this.includeEncodingInfo = includeEncodingInfo;
		if (Boolean.TRUE.equals(includeEncodingInfo)) {
			includeDocs(true);
		}
		return this;
	}

	/**
	 * Defaults to true
	 */
	public ViewQueryParamsBuilder inclusiveEnd(Boolean inclusiveEnd) { this.inclusiveEnd = inclusiveEnd; return this; }
	// key defined below
	// keys defined below
	public ViewQueryParamsBuilder limit(Integer limit) { this.limit = limit; return this; }
	public ViewQueryParamsBuilder reduce(Boolean reduce) { this.reduce = reduce; return this; }
	public ViewQueryParamsBuilder skip(Integer skip) { this.skip = skip; return this; }
	public ViewQueryParamsBuilder sorted(Boolean sorted) { this.sorted = sorted; return this; }
	public ViewQueryParamsBuilder stable(Boolean stable) { this.stable = stable; return this; }
	// startKey defined below
	public ViewQueryParamsBuilder startKeyDocId(String startKeyDocId) { this.startKeyDocId = startKeyDocId; return this; }
	public ViewQueryParamsBuilder update(Update update) { this.update = update; return this; }
	public ViewQueryParamsBuilder updateSequence(Boolean updateSequence) { this.updateSequence = updateSequence; return this; }

	// region endKey
	public ViewQueryParamsBuilder endKey(long endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(int endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(float endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(double endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(boolean endKey) {
		return endKeyJson(booleanToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(String endKey) {
		return endKeyJson(stringToJson(endKey));
	}
	public ViewQueryParamsBuilder endKeyJson(JsonData endKeyJson) {
		this.endKeyJson = endKeyJson;
		return this;
	}
	// endregion
	// region key
	public ViewQueryParamsBuilder key(long key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(int key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(float key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(double key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(boolean key) {
		return keyJson(booleanToJson(key));
	}
	public ViewQueryParamsBuilder key(String key) {
		return keyJson(stringToJson(key));
	}
	public ViewQueryParamsBuilder keyJson(JsonData keyJson) {
		this.keyJson = keyJson;
		return this;
	}
	// endregion
	// region keys
	// Note: This reason is different from the other similar regions since this deals with a list/arrays
	public ViewQueryParamsBuilder keys(long... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (long key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(int... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (int key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(double... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (double key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(float... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (float key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(boolean... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (boolean key : keys) {
			keysJsonList.add(booleanToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keysJson(JsonData... keysJsonList) { return keysJson(Arrays.asList(keysJsonList)); }
	public ViewQueryParamsBuilder keysJson(List<JsonData> keysJsonList) { this.keysJsonList = keysJsonList; return this; }
	// endregion
	// region startKey
	public ViewQueryParamsBuilder startKey(long startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(int startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(float startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(double startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(boolean startKey) {
		return startKeyJson(booleanToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(String startKey) {
		return startKeyJson(stringToJson(startKey));
	}
	public ViewQueryParamsBuilder startKeyJson(JsonData startKeyJson) {
		this.startKeyJson = startKeyJson;
		return this;
	}
	// endregion

	private static JsonData stringToJson(String string) {
		try {
			return new StringJsonData(MAPPER.writeValueAsString(string));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	private static JsonData numberToJson(Number number) {
		requireNonNull(number);
		try {
			return new StringJsonData(MAPPER.writeValueAsString(number));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	private static JsonData booleanToJson(boolean b) {
		try {
			return new StringJsonData(MAPPER.writeValueAsString(b));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public enum Update {
		TRUE("true"), FALSE("false"), LAZY("lazy");
		@JsonValue
		private final String value;

		Update(String value) {
			this.value = value;
		}
	}
}
