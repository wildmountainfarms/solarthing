package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SessionGetResponse {

	private final Info info;
	private final boolean ok;
	private final UserContext userContext;

	@JsonCreator
	public SessionGetResponse(
			@JsonProperty(value = "info", required = true) Info info,
			@JsonProperty(value = "ok", required = true) boolean ok,
			@JsonProperty(value = "userCtx", required = true) UserContext userContext) {
		requireNonNull(this.info = info);
		this.ok = ok;
		requireNonNull(this.userContext = userContext);
	}

	@JsonProperty("info")
	public Info getInfo() {
		return info;
	}

	@JsonProperty("ok")
	public boolean isOk() {
		return ok;
	}

	@JsonProperty("userCtx")
	public UserContext getUserContext() {
		return userContext;
	}

	public static class Info {
		private final @Nullable String authenticated;
		private final @Nullable String authenticationDatabase;
		private final List<String> authenticationHandlers;

		@JsonCreator
		public Info(
				@JsonProperty("authenticated") String authenticated,
				@JsonProperty("authentication_db") String authenticationDatabase,
				@JsonProperty(value = "authentication_handlers", required = true) List<String> authenticationHandlers) {
			this.authenticated = authenticated;
			this.authenticationDatabase = authenticationDatabase;
			requireNonNull(this.authenticationHandlers = authenticationHandlers);
		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonProperty("authenticated")
		public String getAuthenticated() {
			return authenticated;
		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonProperty("authentication_db")
		public String getAuthenticationDatabase() {
			return authenticationDatabase;
		}

		@JsonProperty("authentication_handlers")
		public List<String> getAuthenticationHandlers() {
			return authenticationHandlers;
		}
	}
}
