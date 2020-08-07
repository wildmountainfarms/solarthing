package me.retrodaredevil.solarthing.pvoutput.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TeamParameters {
	private final int teamId;

	public TeamParameters(int teamId) {
		this.teamId = teamId;
	}

	@JsonProperty("tid")
	public int getTeamId() {
		return teamId;
	}
}
