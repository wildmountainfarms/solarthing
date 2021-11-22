package me.retrodaredevil.solarthing.actions.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.node.ActionNode;
import me.retrodaredevil.action.node.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.AlterPacketsProvider;
import me.retrodaredevil.solarthing.actions.environment.AlterPacketsEnvironment;
import me.retrodaredevil.solarthing.database.VersionedPacket;
import me.retrodaredevil.solarthing.type.alter.StoredAlterPacket;
import me.retrodaredevil.solarthing.type.alter.flag.FlagUtil;

import java.time.Instant;
import java.util.List;

import static java.util.Objects.requireNonNull;

@JsonTypeName("flag")
public class FlagActionNode implements ActionNode {
	private final String flagName;
	private final boolean not;

	@JsonCreator
	public FlagActionNode(@JsonProperty(value = "name", required = true) String flagName, @JsonProperty("not") Boolean not) {
		requireNonNull(this.flagName = flagName);
		this.not = not != null && not;
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		AlterPacketsEnvironment alterPacketsEnvironment = actionEnvironment.getInjectEnvironment().get(AlterPacketsEnvironment.class);

		AlterPacketsProvider provider = alterPacketsEnvironment.getAlterPacketsProvider();
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				List<VersionedPacket<StoredAlterPacket>> packets = provider.getPackets();
				if (packets != null) {
					Instant now = Instant.now();
					if (FlagUtil.isFlagActive(now, flagName, packets.stream().map(VersionedPacket::getPacket)) != not) {
						setDone(true);
					}
				}
			}
		};
	}
}
