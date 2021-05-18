package me.retrodaredevil.solarthing.cache.packets.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.solar.outback.OutbackData;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;
import me.retrodaredevil.solarthing.solar.outback.fx.common.FXAccumulationData;

import static java.util.Objects.requireNonNull;

@JsonExplicit
public class FXAccumulationDataCache extends BaseAccumulationDataCache implements IdentificationCacheData, OutbackData {
	private final OutbackIdentifier identifier;

	private final Data mainData;

	private final Data unknownData;

	@JsonCreator
	public FXAccumulationDataCache(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "mainData", required = true) Data mainData,
			@JsonProperty(value = "firstDateMillis", required = true) Long firstDateMillis,
			@JsonProperty(value = "lastDateMillis", required = true) Long lastDateMillis,
			@JsonProperty(value = "unknownData", required = true) Data unknownData,
			@JsonProperty(value = "unknownStartDateMillis", required = true) Long unknownStartDateMillis) {
		super(firstDateMillis, lastDateMillis, unknownStartDateMillis);
		requireNonNull(this.mainData = mainData);
		requireNonNull(this.unknownData = unknownData);

		identifier = new OutbackIdentifier(address);
	}

	@Override
	public FXAccumulationDataCache combine(IdentificationCacheData other) {
		FXAccumulationDataCache data = (FXAccumulationDataCache) other;
		if (isUnknown()) {
			return data;
		}
		if (data.isUnknown()) {
			return this;
		}
		return new FXAccumulationDataCache(
				identifier.getAddress(),
				mainData.plus(data.mainData).plus(data.unknownData),
				getFirstDateMillis(), data.getLastDateMillis(),
				unknownData,
				getUnknownStartDateMillis()
		);
	}

	@Override
	public @NotNull OutbackIdentifier getIdentifier() {
		return identifier;
	}

	@Override
	public int getAddress() { // JsonProperty added to superinterface
		return identifier.getAddress();
	}

	@JsonProperty("mainData")
	public @NotNull Data getMainData() {
		return mainData;
	}

	@JsonProperty("unknownData")
	public @NotNull Data getUnknownData() {
		return unknownData;
	}

	public static class Data implements FXAccumulationData {
		public static final Data ZERO = new Data(0f, 0f, 0f, 0f);

		private final float inverterKWH;
		private final float chargerKWH;
		private final float buyKWH;
		private final float sellKWH;

		@JsonCreator
		public Data(
				@JsonProperty(value = "inverterKWH", required = true) float inverterKWH,
				@JsonProperty(value = "chargerKWH", required = true) float chargerKWH,
				@JsonProperty(value = "buyKWH", required = true) float buyKWH,
				@JsonProperty(value = "sellKWH", required = true) float sellKWH) {
			this.inverterKWH = inverterKWH;
			this.chargerKWH = chargerKWH;
			this.buyKWH = buyKWH;
			this.sellKWH = sellKWH;
		}
		public Data plus(Data other) {
			return new Data(
					inverterKWH + other.inverterKWH,
					chargerKWH + other.chargerKWH,
					buyKWH + other.buyKWH,
					sellKWH + other.sellKWH
			);
		}

		@Override
		public float getInverterKWH() {
			return inverterKWH;
		}

		@Override
		public float getChargerKWH() {
			return chargerKWH;
		}

		@Override
		public float getBuyKWH() {
			return buyKWH;
		}

		@Override
		public float getSellKWH() {
			return sellKWH;
		}
	}
}
