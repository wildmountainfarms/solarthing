package me.retrodaredevil.solarthing.pvoutput;

import me.retrodaredevil.solarthing.pvoutput.data.OutputServiceDataBuilder;
import okhttp3.OkHttpClient;

public class UploadTest {
	public static void main(String[] args) throws PVOutputException {
		new OkHttpPVOutputExporter(
				new OkHttpClient.Builder().build(),
				"",
				72206
		).exportOutput(new OutputServiceDataBuilder(new SimpleDate(2019, 12, 16))
				.setGenerated(4200)
				.setComments("This is a simple test with real data.")
				.build()
		);
	}
}
