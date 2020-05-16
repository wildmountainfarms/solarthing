package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;

import java.io.File;
import java.util.List;

@JsonTypeName("message-sender")
@JsonExplicit
public class MessageSenderProgramOptions extends DatabaseTimeZoneOptionBase {

	@JsonProperty("senders")
	private List<File> messageSenderFiles;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.MESSAGE_SENDER;
	}

	public List<File> getMessageSenderFiles() {
		return messageSenderFiles;
	}
}
