package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.message.MessageSenderActionNode;
import me.retrodaredevil.solarthing.config.options.MessageSenderProgramOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class MessageSenderMain {
	private MessageSenderMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderMain.class);

	public static int startMessageSender(MessageSenderProgramOptions options) {
		LOGGER.info(SolarThingConstants.SUMMARY_MARKER, "Starting message program. Note that you should use the 'automation' program directly instead.");
		LOGGER.debug("Note that this has to get packets twice before this sends anything.");
		final ActionNode actionNode;
		try {
			actionNode = MessageSenderActionNode.create(options.getMessageSenderFileMap(), options.getMessageEventNodes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return AutomationMain.startAutomation(Collections.singletonList(actionNode), options, 5000);
	}
}
