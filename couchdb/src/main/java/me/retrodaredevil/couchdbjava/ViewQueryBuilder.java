package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewQueryBuilder {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@JsonProperty("conflicts")
	private Boolean conflicts = null;
	@JsonProperty("descending")
	private Boolean descending = null;
	@JsonRawValue
	@JsonProperty("endkey")
	private String endKey = null;
	@JsonProperty("endkey_docid")
	private String endKeyDocId = null;
	@JsonProperty("group")
	private Boolean group = null;
	@JsonProperty("group_level")
	private Integer groupLevel = null;
	@JsonProperty("include_docs")
	private Boolean includeDocs = null;
	@JsonProperty("attachments")
	private Boolean attachments = null;
	@JsonProperty("att_encoding_info")
	private Boolean includeEncodingInfo = null;
	@JsonProperty("inclusive_end")
	private Boolean inclusiveEnd = null;
	@JsonRawValue
	@JsonProperty("key")
	private String key = null;
//	@JsonProperty("keys")
//	private List<String> keys;
	@JsonProperty("limit")
	private Integer limit = null;
	@JsonProperty("reduce")
	private Boolean reduce = null;
	@JsonProperty("skip")
	private Integer skip = null;
	@JsonProperty("sorted")
	private Boolean sorted = null;
	@JsonProperty("stable")
	private Boolean stable = null;
	@JsonProperty("stale")
	private Boolean stale = null;
	@JsonRawValue
	@JsonProperty("startkey")
	private String startKey;
	@JsonProperty("startkey_docid")
	private String startKeyDocId;
	@JsonProperty("update")
	private Update update = null;
	@JsonProperty("update_seq")
	private Boolean updateSequence = null;

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
	public ViewQueryBuilder stale(Boolean stale) { this.stale = stale; return this; }
	public ViewQueryBuilder startKeyDocId(String startKeyDocId) { this.startKeyDocId = startKeyDocId; return this; }
	public ViewQueryBuilder update(Update update) { this.update = update; return this; }
	public ViewQueryBuilder updateSequence(Boolean updateSequence) { this.updateSequence = updateSequence; return this; }

	public ViewQueryBuilder endKeyRaw(String endKey) {
		this.endKey = endKey;
		return this;
	}
	public ViewQueryBuilder startKey(long startKey) {
		return startKeyRaw("" + startKey);
	}
	public ViewQueryBuilder startKey(String startKey) {
		try {
			return startKeyRaw(MAPPER.writeValueAsString(startKey));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	public ViewQueryBuilder startKeyRaw(String startKeyRaw) {
		this.startKey = startKeyRaw;
		return this;
	}


	private static void add(Map<String, String> map, String key, Object object) {
		if (object != null) {
			map.put(key, object.toString());
		}
	}

	public Map<String, String> buildMap() {
		Map<String, String> r = new HashMap<>();
		add(r, "conflicts", conflicts);
		add(r, "descending", descending);
		add(r, "endkey", endKey);
		add(r, "endkey_docid", endKeyDocId);
		add(r, "group", group);
		add(r, "group_level", groupLevel);
		add(r, "include_docs", includeDocs);
		add(r, "attachments", attachments);
		add(r, "att_encoding_info", includeEncodingInfo);



		return Collections.unmodifiableMap(r);
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
