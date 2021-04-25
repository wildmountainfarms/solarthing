package me.retrodaredevil.couchdbjava;

public class ViewQueryBuilder {
	private String designDocId;
	private String viewName;
	private String key;
//	private Keys keys;
	private String startKey;
	private String startDocId;
	private String endKey;
	private String endDocId;
	private Integer limit = null;
	private String staleOk;
	private boolean descending;
	private Integer skip = null;
	private boolean group;
	private Integer groupLevel = null;
	private boolean reduce = true;
	private boolean includeDocs = false;
	private boolean inclusiveEnd = true;
	private boolean ignoreNotFound = false;
	private boolean updateSeq = false;

}
