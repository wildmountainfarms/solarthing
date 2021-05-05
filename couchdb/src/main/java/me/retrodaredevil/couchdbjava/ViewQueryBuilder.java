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

public class ViewQueryBuilder {
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

	public ViewQuery build() {
		return new ViewQuery(conflicts, descending, endKeyJson, endKeyDocId, group, groupLevel, includeDocs, attachments, includeEncodingInfo,
				inclusiveEnd, keyJson, keysJsonList, limit, reduce, skip, sorted, stable, startKeyJson, startKeyDocId, update, updateSequence);
	}

	public ViewQueryBuilder conflicts(Boolean conflicts) {
		this.conflicts = conflicts;
		if (Boolean.TRUE.equals(conflicts)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryBuilder descending(Boolean descending) { this.descending = descending; return this; }
	// endKey defined below
	public ViewQueryBuilder endKeyDocId(String endKeyDocId) { this.endKeyDocId = endKeyDocId; return this; }
	public ViewQueryBuilder group(Boolean group) { this.group = group; return this; }
	public ViewQueryBuilder groupLevel(Integer groupLevel) {
		this.groupLevel = groupLevel;
		if (groupLevel != null) {
			group(true);
		}
		return this;
	}
	public ViewQueryBuilder includeDocs(Boolean includeDocs) { this.includeDocs = includeDocs; return this; }
	public ViewQueryBuilder attachments(Boolean attachments) {
		this.attachments = attachments;
		if (Boolean.TRUE.equals(attachments)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryBuilder includeEncodingInfo(Boolean includeEncodingInfo) {
		this.includeEncodingInfo = includeEncodingInfo;
		if (Boolean.TRUE.equals(includeEncodingInfo)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryBuilder inclusiveEnd(Boolean inclusiveEnd) { this.inclusiveEnd = inclusiveEnd; return this; }
	// key defined below
	// TODO keys
	public ViewQueryBuilder limit(Integer limit) { this.limit = limit; return this; }
	public ViewQueryBuilder reduce(Boolean reduce) { this.reduce = reduce; return this; }
	public ViewQueryBuilder skip(Integer skip) { this.skip = skip; return this; }
	public ViewQueryBuilder sorted(Boolean sorted) { this.sorted = sorted; return this; }
	public ViewQueryBuilder stable(Boolean stable) { this.stable = stable; return this; }
	// startKey defined below
	public ViewQueryBuilder startKeyDocId(String startKeyDocId) { this.startKeyDocId = startKeyDocId; return this; }
	public ViewQueryBuilder update(Update update) { this.update = update; return this; }
	public ViewQueryBuilder updateSequence(Boolean updateSequence) { this.updateSequence = updateSequence; return this; }

	// region endKey
	public ViewQueryBuilder endKey(long endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryBuilder endKey(int endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryBuilder endKey(float endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryBuilder endKey(double endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryBuilder endKey(boolean endKey) {
		return endKeyJson(booleanToJson(endKey));
	}
	public ViewQueryBuilder endKey(String endKey) {
		return endKeyJson(stringToJson(endKey));
	}
	public ViewQueryBuilder endKeyJson(JsonData endKeyJson) {
		this.endKeyJson = endKeyJson;
		return this;
	}
	// endregion
	// region key
	public ViewQueryBuilder key(long key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryBuilder key(int key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryBuilder key(float key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryBuilder key(double key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryBuilder key(boolean key) {
		return keyJson(booleanToJson(key));
	}
	public ViewQueryBuilder key(String key) {
		return keyJson(stringToJson(key));
	}
	public ViewQueryBuilder keyJson(JsonData keyJson) {
		this.keyJson = keyJson;
		return this;
	}
	// endregion
	// region keys
	// Note: This reason is different from the other similar regions since this deals with a list/arrays
	public ViewQueryBuilder keys(long... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (long key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryBuilder keys(int... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (int key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryBuilder keys(double... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (double key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryBuilder keys(float... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (float key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryBuilder keys(boolean... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (boolean key : keys) {
			keysJsonList.add(booleanToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryBuilder keysJson(JsonData... keysJsonList) { return keysJson(Arrays.asList(keysJsonList)); }
	public ViewQueryBuilder keysJson(List<JsonData> keysJsonList) { this.keysJsonList = keysJsonList; return this; }
	// endregion
	// region startKey
	public ViewQueryBuilder startKey(long startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryBuilder startKey(int startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryBuilder startKey(float startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryBuilder startKey(double startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryBuilder startKey(boolean startKey) {
		return startKeyJson(booleanToJson(startKey));
	}
	public ViewQueryBuilder startKey(String startKey) {
		return startKeyJson(stringToJson(startKey));
	}
	public ViewQueryBuilder startKeyJson(JsonData startKeyJson) {
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
