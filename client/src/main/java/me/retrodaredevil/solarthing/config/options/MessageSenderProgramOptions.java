package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.config.message.MessageEventNode;

import java.io.File;
import java.util.List;
import java.util.Map;

@JsonTypeName("message-sender")
@JsonExplicit
public class MessageSenderProgramOptions extends DatabaseTimeZoneOptionBase {

	@JsonProperty("senders")
	private Map<String, File> messageSenderFileMap;
	@JsonProperty("events")
	private List<MessageEventNode> messageEventNodeList;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.MESSAGE_SENDER;
	}

	public Map<String, File> getMessageSenderFileMap() { return messageSenderFileMap; }

	public List<MessageEventNode> getMessageEventNodes() {
		return messageEventNodeList;
	}
}
