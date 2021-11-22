package me.retrodaredevil.solarthing.rest.command;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.action.node.environment.InjectEnvironment;
import me.retrodaredevil.action.node.environment.VariableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * While this deal with commands in the general SolarThing sense, this is a new concept. REST commands are
 * requested with some API key and if authorized, an action will be run.
 */
@RestController
@RequestMapping("/command")
public class CommandController {

	private final CommandHandler commandHandler;

	public CommandController(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	/**
	 * Runs the given action. A response is not returned until the action is done running
	 *
	 * @param apiKey The api key
	 * @param commandName The name of the command, which corresponds to an action
	 * @return
	 */
	@GetMapping(
			path = "/run",
			produces = "application/json"
	)
	public CommandRequestResponse runCommand(String apiKey, String commandName, String sourceId) {
		// Also consider using this way instead of exceptions: https://stackoverflow.com/a/60079942
		if (apiKey == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "apiKey is required!");
		}
		if (commandName == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "commandName is required!");
		}
		if (sourceId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sourceId is required!");
		}
		if (!commandHandler.isAuthorized(apiKey)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized with the given api key!");
		}
		ActionNode actionNode = commandHandler.getActionNode(commandName);
		if (actionNode == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No corresponding command with commandName: " + commandName);
		}
		InjectEnvironment injectEnvironment = commandHandler.createInjectEnvironment(sourceId);
		// We don't know or care what thread this is running on, so we won't have a shared global variable environment.
		//   We could make a shared global environment a feature of this down the line, but for now let's keep this simple
		ActionEnvironment actionEnvironment = new ActionEnvironment(new VariableEnvironment(), new VariableEnvironment(), injectEnvironment);
		Action action = actionNode.createAction(actionEnvironment);
		while (!action.isDone()) {
			action.update();
			try {
				Thread.sleep(5); // This is here to make sure our CPU doesn't go to 100% for no reason
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace(); // TODO eventually we'll get better logging, right?
				action.end();
				return new CommandRequestResponse(false);
			}
		}
		action.end();
		return new CommandRequestResponse(true);
	}
}
