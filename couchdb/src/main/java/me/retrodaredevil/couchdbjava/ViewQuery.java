package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import me.retrodaredevil.couchdbjava.json.JsonData;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewQuery {
	@JsonProperty("conflicts")
	private final Boolean conflicts;
	@JsonProperty("descending")
	private final Boolean descending;
	@JsonRawValue
	@JsonProperty("endkey")
	private final JsonData endKeyJson;
	@JsonProperty("endkey_docid")
	private final String endKeyDocId;
	@JsonProperty("group")
	private final Boolean group;
	@JsonProperty("group_level")
	private final Integer groupLevel;
	@JsonProperty("include_docs")
	private final Boolean includeDocs;
	@JsonProperty("attachments")
	private final Boolean attachments;
	@JsonProperty("att_encoding_info")
	private final Boolean includeEncodingInfo;
	@JsonProperty("inclusive_end")
	private final Boolean inclusiveEnd;
	@JsonRawValue
	@JsonProperty("key")
	private final JsonData keyJson;
	@JsonProperty("keys")
	private final List<JsonData> keysJsonList;
	@JsonProperty("limit")
	private final Integer limit;
	@JsonProperty("reduce")
	private final Boolean reduce;
	@JsonProperty("skip")
	private final Integer skip;
	@JsonProperty("sorted")
	private final Boolean sorted;
	@JsonProperty("stable")
	private final Boolean stable;
	// "stale" is deprecated
	@JsonRawValue
	@JsonProperty("startkey")
	private final JsonData startKeyJson;
	@JsonProperty("startkey_docid")
	private final String startKeyDocId;
	@JsonProperty("update")
	private final ViewQueryBuilder.Update update;
	@JsonProperty("update_seq")
	private final Boolean updateSequence;

	public ViewQuery(Boolean conflicts, Boolean descending, JsonData endKeyJson, String endKeyDocId, Boolean group, Integer groupLevel, Boolean includeDocs, Boolean attachments, Boolean includeEncodingInfo, Boolean inclusiveEnd, JsonData keyJson, List<JsonData> keysJsonList, Integer limit, Boolean reduce, Integer skip, Boolean sorted, Boolean stable, JsonData startKeyJson, String startKeyDocId, ViewQueryBuilder.Update update, Boolean updateSequence) {
		this.conflicts = conflicts;
		this.descending = descending;
		this.endKeyJson = endKeyJson;
		this.endKeyDocId = endKeyDocId;
		this.group = group;
		this.groupLevel = groupLevel;
		this.includeDocs = includeDocs;
		this.attachments = attachments;
		this.includeEncodingInfo = includeEncodingInfo;
		this.inclusiveEnd = inclusiveEnd;
		this.keyJson = keyJson;
		this.keysJsonList = keysJsonList;
		this.limit = limit;
		this.reduce = reduce;
		this.skip = skip;
		this.sorted = sorted;
		this.stable = stable;
		this.startKeyJson = startKeyJson;
		this.startKeyDocId = startKeyDocId;
		this.update = update;
		this.updateSequence = updateSequence;
	}
}
