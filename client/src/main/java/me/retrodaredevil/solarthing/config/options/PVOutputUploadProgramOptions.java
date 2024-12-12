package me.retrodaredevil.solarthing.config.options;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.JsonExplicit;
import me.retrodaredevil.solarthing.packets.identification.IdentifierFragmentMatcher;
import me.retrodaredevil.solarthing.packets.identification.IdentifierRepFragment;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.PacketVoltageProvider;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.PacketTemperatureCelsiusProvider;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.TemperatureCelsiusProvider;
import me.retrodaredevil.solarthing.program.subprogram.pvoutput.provider.VoltageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@JsonTypeName("pvoutput-upload")
@JsonExplicit
public class PVOutputUploadProgramOptions extends DatabaseTimeZoneOptionBase implements AnalyticsOption, DatabaseOption, ProgramOptions {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputUploadProgramOptions.class);

	@JsonProperty(value = "system_id", required = true)
	private int systemId;
	@JsonProperty(value = "api_key", required = true)
	private String apiKey;

	@JsonProperty("required")
	private Map<Integer, List<String>> requiredIdentifierMap = null;

	@JsonProperty(AnalyticsOption.PROPERTY_NAME)
	private boolean isAnalyticsEnabled = AnalyticsOption.DEFAULT_IS_ANALYTICS_ENABLED;

	@JsonProperty("voltage_identifier")
	private IdentifierRepFragment voltageIdentifierFragmentObject = null;
	@JsonProperty("voltage_from")
	private VoltageProvider voltageProvider = null;

	@JsonProperty("temperature_identifier")
	private IdentifierRepFragment temperatureIdentifierFragmentObject = null;
	@JsonProperty("temperature_from")
	private TemperatureCelsiusProvider temperatureCelsiusProvider = null;

	@JsonProperty("include_import")
	private boolean includeImport = false;
	@JsonProperty("include_export")
	private boolean includeExport = false;

	@JsonProperty("join_teams")
	private boolean joinTeams = false;

	@Override
	public ProgramType getProgramType() {
		return ProgramType.PVOUTPUT_UPLOAD;
	}
	@Override
	public boolean isAnalyticsOptionEnabled() {
		return isAnalyticsEnabled;
	}

	public int getSystemId() {
		return systemId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public Map<Integer, List<String>> getRequiredIdentifierMap() {
		Map<Integer, List<String>> r = requiredIdentifierMap;
		if (r == null) {
			return Collections.emptyMap();
		}
		return r;
	}
	@Deprecated
	public IdentifierFragmentMatcher getVoltageIdentifierFragmentMatcher() {
		IdentifierRepFragment object = voltageIdentifierFragmentObject;
		if (object == null) {
			return IdentifierFragmentMatcher.NO_MATCH;
		}
		return object;
	}
	@Deprecated
	public IdentifierFragmentMatcher getTemperatureIdentifierFragmentMatcher() {
		IdentifierRepFragment object = temperatureIdentifierFragmentObject;
		if (object == null) {
			return IdentifierFragmentMatcher.NO_MATCH;
		}
		return object;
	}

	public VoltageProvider getVoltageProvider() {
		IdentifierRepFragment fragmentObject = voltageIdentifierFragmentObject;
		if (fragmentObject != null) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Do not use \"voltage_identifier\" anymore! Switch to \"voltage_from\"!");
			return new PacketVoltageProvider(fragmentObject, null); // null voltagePacketType defaults to PV
		}
		VoltageProvider voltageProvider = this.voltageProvider;
		return voltageProvider == null ? VoltageProvider.NONE : voltageProvider;
	}
	public TemperatureCelsiusProvider getTemperatureCelsiusProvider() {
		IdentifierRepFragment fragmentObject = temperatureIdentifierFragmentObject;
		if (fragmentObject != null) {
			LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Do not use \"temperature_identifier\" anymore! Switch to \"temperature_from\"!");
			return new PacketTemperatureCelsiusProvider(fragmentObject, null); // null temperaturePacketType defaults to PACKET
		}
		TemperatureCelsiusProvider temperatureCelsiusProvider = this.temperatureCelsiusProvider;
		return temperatureCelsiusProvider == null ? TemperatureCelsiusProvider.NONE : temperatureCelsiusProvider;
	}

	public boolean isIncludeImport() {
		return includeImport;
	}

	public boolean isIncludeExport() {
		return includeExport;
	}

	public boolean isJoinTeams() {
		return joinTeams;
	}

}
