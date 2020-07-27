package me.retrodaredevil.solarthing.actions.mate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.solarthing.actions.ActionNode;
import me.retrodaredevil.solarthing.actions.PacketGroupProvider;
import me.retrodaredevil.solarthing.actions.environment.ActionEnvironment;
import me.retrodaredevil.solarthing.actions.environment.LatestPacketGroupEnvironment;
import me.retrodaredevil.solarthing.solar.outback.OutbackUtil;
import me.retrodaredevil.solarthing.solar.outback.fx.FXStatusPacket;
import me.retrodaredevil.solarthing.solar.outback.fx.OperationalMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 * An action that becomes "done" when it is in a certain operational mode, or if {@link #not} is true, when it's not in a certain operational mode
 */
@JsonTypeName("fxoperational")
public class FXOperationalModeActionNode implements ActionNode {
	private static final Logger LOGGER = LoggerFactory.getLogger(FXOperationalModeActionNode.class);

	private final OperationalMode operationalMode;
	private final boolean not;

	public FXOperationalModeActionNode(OperationalMode operationalMode, boolean not) {
		this.operationalMode = operationalMode;
		this.not = not;
	}
	@JsonCreator
	public static FXOperationalModeActionNode create(@JsonProperty(value = "mode", required = true) String operationalMode, @JsonProperty("not") Boolean not) {
		return new FXOperationalModeActionNode(parseMode(operationalMode), Boolean.TRUE.equals(not));
	}
	private static OperationalMode parseMode(String modeString) {
		requireNonNull(modeString);
		modeString = modeString.replaceAll(" ", "");
		for (OperationalMode mode : OperationalMode.values()) {
			if (mode.getModeName().replaceAll(" ", "").equalsIgnoreCase(modeString)) {
				return mode;
			}
		}
		throw new IllegalArgumentException("No mode for '" + modeString + "'");
	}

	@Override
	public Action createAction(ActionEnvironment actionEnvironment) {
		LatestPacketGroupEnvironment latestPacketGroupEnvironment = actionEnvironment.getInjectEnvironment().get(LatestPacketGroupEnvironment.class);
		PacketGroupProvider packetGroupProvider = latestPacketGroupEnvironment.getPacketGroupProvider();
		return new SimpleAction(false) {
			@Override
			protected void onUpdate() {
				super.onUpdate();
				FXStatusPacket fxStatusPacket = OutbackUtil.getMasterFX(packetGroupProvider.getPacketGroup());
				if (fxStatusPacket == null) {
					LOGGER.warn("No master FX Status Packet!");
				} else {
					setDone((fxStatusPacket.getOperationalMode() == operationalMode) == !not);
				}
			}
		};

	}
}
