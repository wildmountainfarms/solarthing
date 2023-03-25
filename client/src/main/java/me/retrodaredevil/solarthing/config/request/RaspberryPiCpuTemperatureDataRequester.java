package me.retrodaredevil.solarthing.config.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import me.retrodaredevil.solarthing.SolarThingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@JsonTypeName("rpi-cpu-temp")
public final class RaspberryPiCpuTemperatureDataRequester extends CpuTemperatureDataRequester {
	private static final Logger LOGGER = LoggerFactory.getLogger(RaspberryPiCpuTemperatureDataRequester.class);

	public RaspberryPiCpuTemperatureDataRequester() {
		super(1);
	}

	@Override
	public DataRequesterResult create(RequestObject requestObject) {
		LOGGER.warn(SolarThingConstants.SUMMARY_MARKER, "(Deprecated) Do not use rpi-cpu-temp. Instead use cpu-temp");
		return super.create(requestObject);
	}
}
