package me.retrodaredevil.solarthing.influxdb.retention;

public final class RetentionPolicySetting {
	/**
	 * This corresponds to a default retention policy that is automatically determined when you put a point in a database without specifying a retention policy.
	 */
	public static final RetentionPolicySetting DEFAULT_POLICY = new RetentionPolicySetting(null, false, false, false, null);

	private final String name;
	private final boolean tryToCreate;
	private final boolean automaticallyAlter;
	private final boolean ignoreUnsuccessfulCreate;
	private final RetentionPolicy retentionPolicy;

	private RetentionPolicySetting(String name, boolean tryToCreate, boolean automaticallyAlter, boolean ignoreUnsuccessfulCreate, RetentionPolicy retentionPolicy) {
		this.name = name;
		this.tryToCreate = tryToCreate;
		this.automaticallyAlter = automaticallyAlter;
		this.ignoreUnsuccessfulCreate = ignoreUnsuccessfulCreate;
		this.retentionPolicy = retentionPolicy;
		if(retentionPolicy == null && (tryToCreate || automaticallyAlter || ignoreUnsuccessfulCreate)){
			throw new IllegalArgumentException("Cannot automatically create or alter a retention policy if retentionPolicy is null! tryToCreate=" + tryToCreate + " automaticallyAlter=" + automaticallyAlter + " ignoreUnsuccessfulCreate=" + ignoreUnsuccessfulCreate);
		}
		if(!tryToCreate && ignoreUnsuccessfulCreate){
			throw new IllegalArgumentException("Cannot ignore an unsuccessful create if we aren't even going to try to create!");
		}
		if(automaticallyAlter && ignoreUnsuccessfulCreate){
			throw new IllegalArgumentException("Cannot automatically alter and ignore an unsuccessful create at the same time!");
		}
	}
	public static RetentionPolicySetting createRetentionPolicy(String name, boolean tryToCreate, boolean automaticallyAlter, boolean ignoreUnsuccessfulCreate, RetentionPolicy retentionPolicy) {
		return new RetentionPolicySetting(name, tryToCreate, automaticallyAlter, ignoreUnsuccessfulCreate, retentionPolicy);
	}
	public static RetentionPolicySetting createUnspecifiedRetentionPolicy(String name){
		return new RetentionPolicySetting(name, false, false, false, null);
	}

	public String getName() {
		return name;
	}

	public boolean isTryToCreate() {
		return tryToCreate;
	}

	public boolean isAutomaticallyAlter() {
		return automaticallyAlter;
	}

	public RetentionPolicy getRetentionPolicy() {
		return retentionPolicy;
	}

	public boolean isIgnoreUnsuccessfulCreate() {
		return ignoreUnsuccessfulCreate;
	}
}
