package me.retrodaredevil.solarthing.pvoutput.solar.outback;

import me.retrodaredevil.solarthing.pvoutput.data.OutputServiceData;
import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.solar.outback.mx.MXStatusPacket;

import java.util.Collection;

/**
 * Should be used for the last {@link MXStatusPacket}s of a day
 */
public class MXOutputServiceData implements OutputServiceData {
	private final SimpleDate date;
	private final Collection<? extends MXStatusPacket> mxStatusPackets;

	public MXOutputServiceData(SimpleDate date, Collection<? extends MXStatusPacket> mxStatusPackets) {
		this.date = date;
		this.mxStatusPackets = mxStatusPackets;
	}


	@Override
	public SimpleDate getOutputDate() {
		return date;
	}

	@Override
	public Integer getGenerated() {
		float kwh = 0;
		for(MXStatusPacket packet : mxStatusPackets){
			kwh += packet.getDailyKWH();
		}
		return Math.round(kwh * 1000);
	}

	@Override
	public Number getExported() {
		return null;
	}

	@Override
	public Number getPeakPower() {
		return null;
	}

	@Override
	public SimpleTime getPeakTime() {
		return null;
	}

	@Override
	public String getConditionValue() {
		return null;
	}

	@Override
	public Float getMinimumTemperatureCelsius() {
		return null;
	}

	@Override
	public Float getMaximumTemperatureCelsius() {
		return null;
	}

	@Override
	public String getComments() {
		return null;
	}

	@Override
	public Number getImportPeak() {
		return null;
	}

	@Override
	public Number getImportOffPeak() {
		return null;
	}

	@Override
	public Number getImportShoulder() {
		return null;
	}

	@Override
	public Number getImportHighShoulder() {
		return null;
	}

	@Override
	public Number getConsumption() {
		return null;
	}
}
