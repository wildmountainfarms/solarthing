package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.AddOutputParameters;
import me.retrodaredevil.solarthing.pvoutput.data.AddStatusParameters;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class ServicePVOutputExporter implements PVOutputExporter {
	private final PVOutputService service;

	public ServicePVOutputExporter(PVOutputService service) {
		this.service = service;
	}

	@Override
	public void exportOutput(AddOutputParameters parameters) throws PVOutputException {
		Call<String> call = service.addOutput(parameters);
		final Response<String> response;
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
	public void exportStatus(AddStatusParameters parameters) throws PVOutputException {
		throw new UnsupportedOperationException();
	}
}
