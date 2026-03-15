package me.retrodaredevil.solarthing.pvoutput.data;

import org.jspecify.annotations.NullMarked;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;


@NullMarked
public class AddStatusParametersBuilder implements AddStatusParameters {
	private final SimpleDate date;
	private final SimpleTime time;

	private @Nullable Number energyGeneration;
	private @Nullable Number powerGeneration;
	private @Nullable Number energyConsumption;
	private @Nullable Number powerConsumption;

	private @Nullable Float temperatureCelsius;
	private @Nullable Float voltage;
	private @Nullable Integer cumulativeFlag;
	private @Nullable Integer netFlag;

	private @Nullable Number extendedValue1;
	private @Nullable Number extendedValue2;
	private @Nullable Number extendedValue3;
	private @Nullable Number extendedValue4;
	private @Nullable Number extendedValue5;
	private @Nullable Number extendedValue6;
	private @Nullable String textMessage1;

	public AddStatusParametersBuilder(SimpleDate date, SimpleTime time) {
		this.date = requireNonNull(date);
		this.time = requireNonNull(time);
	}
	public AddStatusParameters build(){
		return new ImmutableAddStatusParameters(
				date, time, energyGeneration, powerGeneration, energyConsumption, powerConsumption, temperatureCelsius, voltage, cumulativeFlag, netFlag,
				extendedValue1, extendedValue2, extendedValue3, extendedValue4, extendedValue5, extendedValue6, textMessage1
		);
	}

	// region Getters
	@Override public SimpleDate getDate() { return date; }
	@Override public SimpleTime getTime() { return time; }

	@Override public @Nullable Number getEnergyGeneration() { return energyGeneration; }
	@Override public @Nullable Number getPowerGeneration() { return powerGeneration; }
	@Override public @Nullable Number getEnergyConsumption() { return energyConsumption; }
	@Override public @Nullable Number getPowerConsumption() { return powerConsumption; }
	@Override public @Nullable Float getTemperatureCelsius() { return temperatureCelsius; }
	@Override public @Nullable Float getVoltage() { return voltage; }
	@Override public @Nullable Integer getCumulativeFlag() { return cumulativeFlag; }
	@Override public @Nullable Integer getNetFlag() { return netFlag; }
	@Override public @Nullable Number getExtendedValue1() { return extendedValue1; }
	@Override public @Nullable Number getExtendedValue2() { return extendedValue2; }
	@Override public @Nullable Number getExtendedValue3() { return extendedValue3; }
	@Override public @Nullable Number getExtendedValue4() { return extendedValue4; }
	@Override public @Nullable Number getExtendedValue5() { return extendedValue5; }
	@Override public @Nullable Number getExtendedValue6() { return extendedValue6; }
	@Override public @Nullable String getTextMessage1() { return textMessage1; }
	// endregion

	public AddStatusParametersBuilder setEnergyGeneration(@Nullable Number energyGeneration) {
		this.energyGeneration = energyGeneration;
		return this;
	}

	public AddStatusParametersBuilder setPowerGeneration(@Nullable Number powerGeneration) {
		this.powerGeneration = powerGeneration;
		return this;
	}

	public AddStatusParametersBuilder setEnergyConsumption(@Nullable Number energyConsumption) {
		this.energyConsumption = energyConsumption;
		return this;
	}

	public AddStatusParametersBuilder setPowerConsumption(@Nullable Number powerConsumption) {
		this.powerConsumption = powerConsumption;
		return this;
	}

	public AddStatusParametersBuilder setTemperatureCelsius(@Nullable Float temperatureCelsius) {
		this.temperatureCelsius = temperatureCelsius;
		return this;
	}

	public AddStatusParametersBuilder setVoltage(@Nullable Float voltage) {
		this.voltage = voltage;
		return this;
	}

	public AddStatusParametersBuilder setCumulativeFlag(@Nullable Integer cumulativeFlag) {
		this.cumulativeFlag = cumulativeFlag;
		return this;
	}

	public AddStatusParametersBuilder setNetFlag(@Nullable Integer netFlag) {
		this.netFlag = netFlag;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue1(@Nullable Number extendedValue1) {
		this.extendedValue1 = extendedValue1;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue2(@Nullable Number extendedValue2) {
		this.extendedValue2 = extendedValue2;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue3(@Nullable Number extendedValue3) {
		this.extendedValue3 = extendedValue3;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue4(@Nullable Number extendedValue4) {
		this.extendedValue4 = extendedValue4;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue5(@Nullable Number extendedValue5) {
		this.extendedValue5 = extendedValue5;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue6(@Nullable Number extendedValue6) {
		this.extendedValue6 = extendedValue6;
		return this;
	}

	public AddStatusParametersBuilder setTextMessage1(@Nullable String textMessage1) {
		this.textMessage1 = textMessage1;
		return this;
	}
	static class ImmutableAddStatusParameters implements AddStatusParameters {
		private final SimpleDate date;
		private final SimpleTime time;

		private final @Nullable Number energyGeneration;
		private final @Nullable Number powerGeneration;
		private final @Nullable Number energyConsumption;
		private final @Nullable Number powerConsumption;

		private final @Nullable Float temperatureCelsius;
		private final @Nullable Float voltage;
		private final @Nullable Integer cumulativeFlag;
		private final @Nullable Integer netFlag;

		private final @Nullable Number extendedValue1;
		private final @Nullable Number extendedValue2;
		private final @Nullable Number extendedValue3;
		private final @Nullable Number extendedValue4;
		private final @Nullable Number extendedValue5;
		private final @Nullable Number extendedValue6;
		private final @Nullable String textMessage1;

		ImmutableAddStatusParameters(SimpleDate date, SimpleTime time, @Nullable Number energyGeneration, @Nullable Number powerGeneration, @Nullable Number energyConsumption, @Nullable Number powerConsumption, @Nullable Float temperatureCelsius, @Nullable Float voltage, @Nullable Integer cumulativeFlag, @Nullable Integer netFlag, @Nullable Number extendedValue1, @Nullable Number extendedValue2, @Nullable Number extendedValue3, @Nullable Number extendedValue4, @Nullable Number extendedValue5, @Nullable Number extendedValue6, @Nullable String textMessage1) {
			this.date = date;
			this.time = time;
			this.energyGeneration = energyGeneration;
			this.powerGeneration = powerGeneration;
			this.energyConsumption = energyConsumption;
			this.powerConsumption = powerConsumption;
			this.temperatureCelsius = temperatureCelsius;
			this.voltage = voltage;
			this.cumulativeFlag = cumulativeFlag;
			this.netFlag = netFlag;
			this.extendedValue1 = extendedValue1;
			this.extendedValue2 = extendedValue2;
			this.extendedValue3 = extendedValue3;
			this.extendedValue4 = extendedValue4;
			this.extendedValue5 = extendedValue5;
			this.extendedValue6 = extendedValue6;
			this.textMessage1 = textMessage1;
		}
		@Override public SimpleDate getDate() { return date; }
		@Override public SimpleTime getTime() { return time; }

		@Override public @Nullable Number getEnergyGeneration() { return energyGeneration; }
		@Override public @Nullable Number getPowerGeneration() { return powerGeneration; }
		@Override public @Nullable Number getEnergyConsumption() { return energyConsumption; }
		@Override public @Nullable Number getPowerConsumption() { return powerConsumption; }
		@Override public @Nullable Float getTemperatureCelsius() { return temperatureCelsius; }
		@Override public @Nullable Float getVoltage() { return voltage; }
		@Override public @Nullable Integer getCumulativeFlag() { return cumulativeFlag; }
		@Override public @Nullable Integer getNetFlag() { return netFlag; }
		@Override public @Nullable Number getExtendedValue1() { return extendedValue1; }
		@Override public @Nullable Number getExtendedValue2() { return extendedValue2; }
		@Override public @Nullable Number getExtendedValue3() { return extendedValue3; }
		@Override public @Nullable Number getExtendedValue4() { return extendedValue4; }
		@Override public @Nullable Number getExtendedValue5() { return extendedValue5; }
		@Override public @Nullable Number getExtendedValue6() { return extendedValue6; }
		@Override public @Nullable String getTextMessage1() { return textMessage1; }
	}
}
