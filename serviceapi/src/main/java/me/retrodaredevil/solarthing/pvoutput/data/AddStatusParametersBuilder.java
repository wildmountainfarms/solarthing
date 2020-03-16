package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;

import static java.util.Objects.requireNonNull;

public class AddStatusParametersBuilder implements AddStatusParameters {
	private final SimpleDate date;
	private final SimpleTime time;

	private Number energyGeneration;
	private Number powerGeneration;
	private Number energyConsumption;
	private Number powerConsumption;

	private Float temperatureCelsius;
	private Float voltage;
	private Integer cumulativeFlag;
	private Integer netFlag;

	private Number extendedValue1;
	private Number extendedValue2;
	private Number extendedValue3;
	private Number extendedValue4;
	private Number extendedValue5;
	private Number extendedValue6;
	private String textMessage1;

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

	@Override public Number getEnergyGeneration() { return energyGeneration; }
	@Override public Number getPowerGeneration() { return powerGeneration; }
	@Override public Number getEnergyConsumption() { return energyConsumption; }
	@Override public Number getPowerConsumption() { return powerConsumption; }
	@Override public Float getTemperatureCelsius() { return temperatureCelsius; }
	@Override public Float getVoltage() { return voltage; }
	@Override public Integer getCumulativeFlag() { return cumulativeFlag; }
	@Override public Integer getNetFlag() { return netFlag; }
	@Override public Number getExtendedValue1() { return extendedValue1; }
	@Override public Number getExtendedValue2() { return extendedValue2; }
	@Override public Number getExtendedValue3() { return extendedValue3; }
	@Override public Number getExtendedValue4() { return extendedValue4; }
	@Override public Number getExtendedValue5() { return extendedValue5; }
	@Override public Number getExtendedValue6() { return extendedValue6; }
	@Override public String getTextMessage1() { return textMessage1; }
	// endregion

	public AddStatusParametersBuilder setEnergyGeneration(Number energyGeneration) {
		this.energyGeneration = energyGeneration;
		return this;
	}

	public AddStatusParametersBuilder setPowerGeneration(Number powerGeneration) {
		this.powerGeneration = powerGeneration;
		return this;
	}

	public AddStatusParametersBuilder setEnergyConsumption(Number energyConsumption) {
		this.energyConsumption = energyConsumption;
		return this;
	}

	public AddStatusParametersBuilder setPowerConsumption(Number powerConsumption) {
		this.powerConsumption = powerConsumption;
		return this;
	}

	public AddStatusParametersBuilder setTemperatureCelsius(Float temperatureCelsius) {
		this.temperatureCelsius = temperatureCelsius;
		return this;
	}

	public AddStatusParametersBuilder setVoltage(Float voltage) {
		this.voltage = voltage;
		return this;
	}

	public AddStatusParametersBuilder setCumulativeFlag(Integer cumulativeFlag) {
		this.cumulativeFlag = cumulativeFlag;
		return this;
	}

	public AddStatusParametersBuilder setNetFlag(Integer netFlag) {
		this.netFlag = netFlag;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue1(Number extendedValue1) {
		this.extendedValue1 = extendedValue1;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue2(Number extendedValue2) {
		this.extendedValue2 = extendedValue2;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue3(Number extendedValue3) {
		this.extendedValue3 = extendedValue3;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue4(Number extendedValue4) {
		this.extendedValue4 = extendedValue4;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue5(Number extendedValue5) {
		this.extendedValue5 = extendedValue5;
		return this;
	}

	public AddStatusParametersBuilder setExtendedValue6(Number extendedValue6) {
		this.extendedValue6 = extendedValue6;
		return this;
	}

	public AddStatusParametersBuilder setTextMessage1(String textMessage1) {
		this.textMessage1 = textMessage1;
		return this;
	}
	static class ImmutableAddStatusParameters implements AddStatusParameters {
		private final SimpleDate date;
		private final SimpleTime time;

		private final Number energyGeneration;
		private final Number powerGeneration;
		private final Number energyConsumption;
		private final Number powerConsumption;

		private final Float temperatureCelsius;
		private final Float voltage;
		private final Integer cumulativeFlag;
		private final Integer netFlag;

		private final Number extendedValue1;
		private final Number extendedValue2;
		private final Number extendedValue3;
		private final Number extendedValue4;
		private final Number extendedValue5;
		private final Number extendedValue6;
		private final String textMessage1;

		ImmutableAddStatusParameters(SimpleDate date, SimpleTime time, Number energyGeneration, Number powerGeneration, Number energyConsumption, Number powerConsumption, Float temperatureCelsius, Float voltage, Integer cumulativeFlag, Integer netFlag, Number extendedValue1, Number extendedValue2, Number extendedValue3, Number extendedValue4, Number extendedValue5, Number extendedValue6, String textMessage1) {
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

		@Override public Number getEnergyGeneration() { return energyGeneration; }
		@Override public Number getPowerGeneration() { return powerGeneration; }
		@Override public Number getEnergyConsumption() { return energyConsumption; }
		@Override public Number getPowerConsumption() { return powerConsumption; }
		@Override public Float getTemperatureCelsius() { return temperatureCelsius; }
		@Override public Float getVoltage() { return voltage; }
		@Override public Integer getCumulativeFlag() { return cumulativeFlag; }
		@Override public Integer getNetFlag() { return netFlag; }
		@Override public Number getExtendedValue1() { return extendedValue1; }
		@Override public Number getExtendedValue2() { return extendedValue2; }
		@Override public Number getExtendedValue3() { return extendedValue3; }
		@Override public Number getExtendedValue4() { return extendedValue4; }
		@Override public Number getExtendedValue5() { return extendedValue5; }
		@Override public Number getExtendedValue6() { return extendedValue6; }
		@Override public String getTextMessage1() { return textMessage1; }
	}
}
