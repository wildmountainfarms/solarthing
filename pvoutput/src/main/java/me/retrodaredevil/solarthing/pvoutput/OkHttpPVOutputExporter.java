package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.OutputServiceData;
import me.retrodaredevil.solarthing.pvoutput.data.StatusServiceData;
import okhttp3.*;

import java.io.IOException;

public class OkHttpPVOutputExporter implements PVOutputExporter {
	private final OkHttpClient client;
	private final String apiKey;
	private final int systemId;



	public OkHttpPVOutputExporter(OkHttpClient client, String apiKey, int systemId) {
		this.client = client;
		this.apiKey = apiKey;
		this.systemId = systemId;
	}

	private void add(FormBody.Builder builder, String name, PVOutputString data){
		if(data != null){
			builder.add(name, data.toPVOutputString());
		}
	}
	private void add(FormBody.Builder builder, String name, Object data){
		if(data != null){
			builder.add(name, data.toString());
		}
	}

	@Override
	public void exportOutput(OutputServiceData data) throws PVOutputException {
		FormBody.Builder formBuilder = new FormBody.Builder()
				.add("d", data.getOutputDate().toPVOutputString());
		add(formBuilder, "g", data.getGenerated());
		add(formBuilder, "e", data.getExported());
		add(formBuilder, "pp", data.getPeakPower());
		add(formBuilder, "pt", data.getPeakTime());
		add(formBuilder, "cd", data.getConditionValue());
		add(formBuilder, "tm", data.getMinimumTemperatureCelsius());
		add(formBuilder, "tx", data.getMaximumTemperatureCelsius());
		add(formBuilder, "cm", data.getComments());
		add(formBuilder, "ip", data.getImportPeak());
		add(formBuilder, "io", data.getImportOffPeak());
		add(formBuilder, "is", data.getImportShoulder());
		add(formBuilder, "ih", data.getImportHighShoulder());
		add(formBuilder, "c", data.getConsumption());

		Call call = client.newCall(new Request.Builder()
				.url("https://pvoutput.org/service/r2/addoutput.jsp")
				.addHeader("X-Pvoutput-Apikey", apiKey)
				.addHeader("X-Pvoutput-SystemId", "" + systemId)
				.post(formBuilder.build())
		.build());
		final Response response;
		try {
			response = call.execute();
		} catch (IOException e) {
			throw new PVOutputException(e);
		}
		int code = response.code();
		if(code != 200){
			throw new PVOutputException("Request was not successful! code=" + code + " message=" + response.message());
		}
	}

	@Override
	public void exportStatus(StatusServiceData data) throws PVOutputException {
		throw new UnsupportedOperationException();
	}
}
