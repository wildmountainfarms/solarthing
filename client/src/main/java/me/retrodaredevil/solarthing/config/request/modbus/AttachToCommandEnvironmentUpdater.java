package me.retrodaredevil.solarthing.config.request.modbus;

import me.retrodaredevil.solarthing.type.open.OpenSource;
import me.retrodaredevil.solarthing.actions.command.EnvironmentUpdater;
import me.retrodaredevil.solarthing.actions.environment.InjectEnvironment;
import me.retrodaredevil.solarthing.commands.packets.open.RequestCommandPacket;
import me.retrodaredevil.solarthing.packets.Packet;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Will attach a collection of environment objects to a {@link InjectEnvironment.Builder} only if a given predicate is matched for a given command name.
 * <p>
 * This command name is determined by {@link RequestCommandPacket} which is retrieved from {@link OpenSource#getPacket()}
 */
public class AttachToCommandEnvironmentUpdater implements EnvironmentUpdater {
	private final Collection<Object> environmentsToAdd;
	private final Predicate<? super String> shouldAttachToCommandPredicate;

	public AttachToCommandEnvironmentUpdater(Collection<Object> environmentsToAdd, Predicate<? super String> shouldAttachToCommandPredicate) {
		this.environmentsToAdd = environmentsToAdd;
		this.shouldAttachToCommandPredicate = shouldAttachToCommandPredicate;
	}

	@Override
	public void updateInjectEnvironment(OpenSource source, InjectEnvironment.Builder injectEnvironmentBuilder) {
		Packet packet = source.getPacket();
		if (packet instanceof RequestCommandPacket) {
			String commandName = ((RequestCommandPacket) packet).getCommandName();
			if (shouldAttachToCommandPredicate.test(commandName)) {
				for (Object environment : environmentsToAdd) {
					injectEnvironmentBuilder.add(environment);
				}
			}
		}
	}
}
