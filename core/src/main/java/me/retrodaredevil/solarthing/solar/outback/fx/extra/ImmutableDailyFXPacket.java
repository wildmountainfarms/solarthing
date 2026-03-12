package me.retrodaredevil.solarthing.solar.outback.fx.extra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.solarthing.jackson.UnwrappedDeserializer;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacketType;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.BaseFXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXDailyData;
import me.retrodaredevil.solarthing.solar.outback.fx.common.ImmutableFXDailyData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(using = ImmutableDailyFXPacket.Deserializer.class)
@NullMarked
public class ImmutableDailyFXPacket extends BaseFXDailyData implements DailyFXPacket {

	public ImmutableDailyFXPacket(FXDailyData fxDailyData, OutbackIdentifier outbackIdentifier) {
		super(SolarExtraPacketType.FX_DAILY, fxDailyData, outbackIdentifier);
	}
	public ImmutableDailyFXPacket(FXDailyData fxDailyData) {
		this(fxDailyData, new OutbackIdentifier(fxDailyData.getAddress()));
	}

	// TODO remove custom deserializer
	@Deprecated
	public static class Deserializer extends UnwrappedDeserializer<ImmutableDailyFXPacket, Builder> {
		Deserializer() {
			super(Builder.class, Builder::build);
		}
	}

	static class Builder {

		// @Nullable only for NullAway compatibility
		@JsonUnwrapped
		@JsonProperty(required = true)
		@JsonDeserialize(as = ImmutableFXDailyData.class)
		private @Nullable FXDailyData fxDailyData;

		public ImmutableDailyFXPacket build(){
			return new ImmutableDailyFXPacket(requireNonNull(fxDailyData));
		}
	}

}
