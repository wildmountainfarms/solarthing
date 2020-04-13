package me.retrodaredevil.solarthing.solar.outback.fx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.solarthing.packets.identification.IdentityInfo;
import me.retrodaredevil.solarthing.solar.outback.OutbackIdentifier;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static me.retrodaredevil.util.json.JacksonHelper.require;

@JsonIgnoreProperties(value = {"operatingModeName", "errors", "acModeName", "miscModes", "warnings"}, allowGetters = true)
final class ImmutableFXStatusPacket implements FXStatusPacket {
	private final int address;

	private final float inverterCurrent;
	private final int inverterCurrentRaw;
	private final float chargerCurrent;
	private final int chargerCurrentRaw;
	private final float buyCurrent;
	private final int buyCurrentRaw;
	private final int inputVoltage, inputVoltageRaw;
	private final int outputVoltage, outputVoltageRaw;
	private final float sellCurrent;
	private final int sellCurrentRaw;
	private final int operatingMode, errorMode, acMode;

	private final float batteryVoltage;

	private final int misc, warningMode, chksum;

	private final OutbackIdentifier identifier;
	private final IdentityInfo identityInfo;

	ImmutableFXStatusPacket(
			int address,
			float inverterCurrent, int inverterCurrentRaw,
			float chargerCurrent, int chargerCurrentRaw,
			float buyCurrent, int buyCurrentRaw,
			int inputVoltage, int inputVoltageRaw,
			int outputVoltage, int outputVoltageRaw,
			float sellCurrent, int sellCurrentRaw,
			int operatingMode, int errorMode, int acMode,
			float batteryVoltage,
			int misc, int warningMode, int chksum
	) {
		this.address = address;
		this.inverterCurrent = inverterCurrent;
		this.inverterCurrentRaw = inverterCurrentRaw;
		this.chargerCurrent = chargerCurrent;
		this.chargerCurrentRaw = chargerCurrentRaw;
		this.buyCurrent = buyCurrent;
		this.buyCurrentRaw = buyCurrentRaw;
		this.inputVoltage = inputVoltage;
		this.inputVoltageRaw = inputVoltageRaw;
		this.outputVoltage = outputVoltage;
		this.outputVoltageRaw = outputVoltageRaw;
		this.sellCurrent = sellCurrent;
		this.sellCurrentRaw = sellCurrentRaw;
		this.operatingMode = operatingMode;
		this.errorMode = errorMode;
		this.acMode = acMode;
		this.batteryVoltage = batteryVoltage;
		this.misc = misc;
		this.warningMode = warningMode;
		this.chksum = chksum;

		this.identifier = new OutbackIdentifier(address);
		this.identityInfo = new FXIdentityInfo(address);
	}

	@JsonCreator
	private static ImmutableFXStatusPacket jacksonCreator(
			@JsonProperty(value = "address", required = true) int address,
			@JsonProperty(value = "inverterCurrent", required = true) float inverterCurrent, @JsonProperty(value = "inverterCurrentRaw") Integer inverterCurrentRawNullable,
			@JsonProperty(value = "chargerCurrent", required = true) float chargerCurrent, @JsonProperty(value = "chargerCurrentRaw") Integer chargerCurrentRawNullable,
			@JsonProperty(value = "buyCurrent", required = true) float buyCurrent, @JsonProperty("buyCurrentRaw") Integer buyCurrentRawNullable,
			@JsonProperty(value = "inputVoltage", required = true) int inputVoltage, @JsonProperty("inputVoltageRaw") Integer inputVoltageRawNullable,
			@JsonProperty(value = "outputVoltage", required = true) int outputVoltage, @JsonProperty("outputVoltageRaw") Integer outputVoltageRawNullable,
			@JsonProperty(value = "sellCurrent", required = true) float sellCurrent, @JsonProperty("sellCurrentRaw") Integer sellCurrentRawNullable,
			@JsonProperty(value = "operatingMode", required = true) int operatingMode, @JsonProperty(value = "errorMode", required = true) int errorMode, @JsonProperty(value = "acMode", required = true) int acMode,
			@JsonProperty(value = "batteryVoltage", required = true) float batteryVoltage,
			@JsonProperty(value = "misc", required = true) int misc, @JsonProperty(value = "warningMode", required = true) int warningMode, @JsonProperty(value = "chksum", required = true) int chksum
	) {

		final int inverterCurrentRaw, chargerCurrentRaw, buyCurrentRaw, sellCurrentRaw, inputVoltageRaw, outputVoltageRaw;
		{
			final int number = MiscMode.FX_230V_UNIT.isActive(misc) ? 2 : 1;
			inverterCurrentRaw = inverterCurrentRawNullable == null ? Math.round(inverterCurrent * number) : inverterCurrentRawNullable;
			chargerCurrentRaw = chargerCurrentRawNullable == null ? Math.round(chargerCurrent * number) : chargerCurrentRawNullable;
			buyCurrentRaw = buyCurrentRawNullable == null ? Math.round(buyCurrent * number) : buyCurrentRawNullable;
			sellCurrentRaw = sellCurrentRawNullable == null ? Math.round(sellCurrent * number) : sellCurrentRawNullable;

			inputVoltageRaw = inputVoltageRawNullable == null ? inputVoltage / number : inputVoltageRawNullable;
			outputVoltageRaw = outputVoltageRawNullable == null ? outputVoltage / number : outputVoltageRawNullable;
		}
		return new ImmutableFXStatusPacket(
				address, inverterCurrent, inverterCurrentRaw,
				chargerCurrent, chargerCurrentRaw, buyCurrent, buyCurrentRaw,
				inputVoltage, inputVoltageRaw, outputVoltage, outputVoltageRaw,
				sellCurrent, sellCurrentRaw, operatingMode, errorMode, acMode, batteryVoltage,
				misc, warningMode, chksum
		);
	}

	@NotNull
	@Override
	public IdentityInfo getIdentityInfo() {
		return identityInfo;
	}

	@Override
	public float getInverterCurrent() {
		return inverterCurrent;
	}

	@Override
	public int getInverterCurrentRaw() {
		return inverterCurrentRaw;
	}

	@Override
	public float getChargerCurrent() {
		return chargerCurrent;
	}

	@Override
	public int getChargerCurrentRaw() {
		return chargerCurrentRaw;
	}

	@Override
	public float getBuyCurrent() {
		return buyCurrent;
	}

	@Override
	public int getBuyCurrentRaw() {
		return buyCurrentRaw;
	}

	@Override
	public int getInputVoltage() {
		return inputVoltage;
	}

	@Override
	public int getInputVoltageRaw() {
		return inputVoltageRaw;
	}

	@Override
	public int getOutputVoltage() {
		return outputVoltage;
	}

	@Override
	public int getOutputVoltageRaw() {
		return outputVoltageRaw;
	}

	@Override
	public float getSellCurrent() {
		return sellCurrent;
	}

	@Override
	public int getSellCurrentRaw() {
		return sellCurrentRaw;
	}

	@Override
	public int getOperationalModeValue() {
		return operatingMode;
	}

	@Override
	public int getErrorModeValue() {
		return errorMode;
	}

	@Override
	public int getACModeValue() {
		return acMode;
	}

	@Override
	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	@Override
	public int getMiscValue() {
		return misc;
	}

	@Override
	public int getWarningModeValue() {
		return warningMode;
	}

	@Override
	public int getChksum() {
		return chksum;
	}

	@Override
	public int getAddress() {
		return address;
	}

	@NotNull
    @Override
	public OutbackIdentifier getIdentifier() {
		return identifier;
	}

	@Deprecated
	public static class Deserializer extends JsonDeserializer<ImmutableFXStatusPacket> {

		@Override
		public ImmutableFXStatusPacket deserialize(JsonParser p, DeserializationContext context) throws IOException {
			JsonNode object = p.getCodec().readTree(p);
			final int address = require(context, object, "address", JsonNode::isInt).asInt();

			final float inverterCurrent = require(context, object, "inverterCurrent", JsonNode::isNumber).floatValue();
			final float chargerCurrent = require(context, object , "chargerCurrent", JsonNode::isNumber).floatValue();
			final float buyCurrent = require(context, object, "buyCurrent", JsonNode::isNumber).floatValue();
			final float sellCurrent = require(context, object, "sellCurrent", JsonNode::isNumber).floatValue();
			final int inputVoltage = require(context, object, "inputVoltage", JsonNode::isInt).intValue();
			final int outputVoltage = require(context, object, "outputVoltage", JsonNode::isInt).intValue();

			final int operatingMode = require(context, object, "operatingMode", JsonNode::isInt).intValue();
			final int errorMode = require(context, object, "errorMode", JsonNode::isInt).asInt();
			final int acMode = require(context, object, "acMode", JsonNode::isInt).asInt();

			final float batteryVoltage = require(context, object, "batteryVoltage", JsonNode::isNumber).floatValue();

			final int misc = require(context, object, "misc", JsonNode::isInt).asInt();
			final int warningMode = require(context, object, "warningMode", JsonNode::isInt).asInt();
			final int chksum = require(context, object, "chksum", JsonNode::isInt).asInt();

			final int inverterCurrentRaw, chargerCurrentRaw, buyCurrentRaw, sellCurrentRaw, inputVoltageRaw, outputVoltageRaw;
			{
				final int number = MiscMode.FX_230V_UNIT.isActive(misc) ? 2 : 1;
				inputVoltageRaw = object.get("inputVoltageRaw").asInt(inputVoltage / number);
				outputVoltageRaw = object.get("outputVoltageRaw").asInt(outputVoltage / number);

				inverterCurrentRaw = object.get("inverterCurrentRaw").asInt(Math.round(inverterCurrent * number));
				chargerCurrentRaw = object.get("chargerCurrentRaw").asInt(Math.round(chargerCurrent * number));
				buyCurrentRaw = object.get("buyCurrentRaw").asInt(Math.round(buyCurrent * number));
				sellCurrentRaw = object.get("sellCurrentRaw").asInt(Math.round(sellCurrent * number));
			}

			return new ImmutableFXStatusPacket(
					address, inverterCurrent, inverterCurrentRaw,
					chargerCurrent, chargerCurrentRaw, buyCurrent, buyCurrentRaw,
					inputVoltage, inputVoltageRaw, outputVoltage, outputVoltageRaw,
					sellCurrent, sellCurrentRaw, operatingMode, errorMode, acMode, batteryVoltage,
					misc, warningMode, chksum
			);
		}
	}
}
